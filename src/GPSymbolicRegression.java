import java.util.*;

import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.*;
import org.jgap.gp.terminal.*;
/**
 * 
 * @author Krina Nagar
 *
 */
public class GPSymbolicRegression extends GPProblem {
	
	public static Variable vx;
	protected static Float[] x = {-2.00f, -1.75f, -1.50f, -1.25f, -1.00f, -0.75f, -0.50f, -0.25f, 0.00f, 0.25f, 0.50f, 0.75f, 1.00f, 1.25f, 1.50f, 1.75f, 2.00f, 2.25f, 2.50f, 2.75f};
	protected static float[] y = {37.00000f, 24.16016f, 15.06250f, 8.91016f, 5.00000f, 2.72266f, 1.56250f, 1.09766f, 1.00000f, 1.03516f, 1.06250f, 1.03516f, 1.00000f, 1.09766f, 1.56250f, 2.72266f, 5.00000f, 8.91016f, 15.06250f, 24.16016f};

	/**
	 * Creates new genetic symbolic regression algorithm
	 * @param configuration file
	 * @throws InvalidConfigurationException
	 */
	public GPSymbolicRegression(GPConfiguration conf) throws InvalidConfigurationException {
		super(conf);
	}
	
	/**
	 * @return GPGenotype
	 * @throws InvalidConfigurationException
	 */
	@Override
	public GPGenotype create() throws InvalidConfigurationException {
		GPConfiguration conf = getGPConfiguration();
		//return types
		Class[] types = {CommandGene.FloatClass}; 
		//argument types
		Class[][] argTypes = {{}};
		//define function set
		CommandGene[][] nodeSets = {{
			vx = Variable.create(conf, "X", CommandGene.FloatClass),
			new Multiply(conf, CommandGene.FloatClass),
			new Add(conf, CommandGene.FloatClass),
			new Divide(conf, CommandGene.FloatClass),
			new Subtract(conf, CommandGene.FloatClass),
			new Terminal(conf, CommandGene.FloatClass, 1.0d, 2.0d, true),
		}};
		
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets,20, true);
	}
	/**
	 * Initialise genetic program configurations
	 * @return GPProblem
	 * @throws InvalidConfigurationException
	 */
	public static GPProblem initialiseConfigurations() throws InvalidConfigurationException{
		   GPConfiguration config = new GPConfiguration();
		   
		   config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
		    config.setMaxInitDepth(4);
		    config.setPopulationSize(1000);
		    config.setMaxCrossoverDepth(8);
		    config.setFitnessFunction(new GPSymbolicRegression.FormulaFitnessFunction());
		    config.setStrictProgramCreation(true);
		    config.setCrossoverProb(0.80f);
	        config.setMutationProb(0.10f);
	        config.setReproductionProb(0.10f);
		    GPProblem problem = new GPSymbolicRegression(config);
		    return problem;
	}
	/**
	 * Fitness function for evaluating the produced formulas, represented as GP
	 * programs. The fitness is computed by calculating the result (Y) of the
	 * function/formula for integer inputs 0 to 20 (X). The sum of the differences
  	 * between expected Y and actual Y is the fitness, the lower the better (as
  	 * it is a defect rate here).
	 */
	 public static class FormulaFitnessFunction extends GPFitnessFunction {
		 protected double evaluate(final IGPProgram a_subject) {
		      return computeRawFitness(a_subject);
		    }
	 }
	 
	 /**
	  * Calculates raw fitness value for each generation provided 
	  * @param genetic program
	  * @return fitness value
	  */
	 public static double computeRawFitness(final IGPProgram prog) {
		 double error = 0.0f;
	     Object[] noargs = new Object[0];
	     
	     for (int i = 0; i < 20; i++) {
	    	 vx.set(x[i]);
	         try {
	        	 double result = prog.execute_float(0, noargs);
	        	 error += Math.abs(result - y[i]);
	        	 if (Double.isInfinite(error)) {
	                 return Double.MAX_VALUE;
	               }
	             } catch (ArithmeticException ex) {
	            	 System.out.println("x = " + x[i].floatValue());
	                 System.out.println(prog);
	                 throw ex;
	         }
	     }
	     if (error < 0.001) {
	         error = 0.0d;
	         
	     }
	     return error;
	 }
	 
	 public static void main(String[] args) throws Exception {
		 GPProblem problem = initialiseConfigurations();
		 GPGenotype gp = problem.create();
		 gp.setVerboseOutput(true);
		 double bestFit = -1.0d;
		 //evolve 800 times
	        for (int gen = 1; gen <= 800; gen++) {
	        	gp.evolve();
	        	gp.calcFitness();
	            double fitness = gp.getAllTimeBest().getFitnessValue();

	            // Update fitness value
	            if (bestFit < 0.0d || fitness < bestFit) {
	                bestFit = fitness;
	            }

	            // termination criteria
	            if (bestFit == 0.0d) {
	                break;
	            }
	        }
		 System.out.println("BEST SOLUTION:");
		 gp.outputSolution(gp.getAllTimeBest());
	 }

}

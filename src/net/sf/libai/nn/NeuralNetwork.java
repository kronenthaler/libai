package net.sf.libai.nn;

import java.util.Random;
import javax.swing.JProgressBar;
import net.sf.libai.common.*;

/**
 *	Neural network abstraction.
 *	Provides the methods to trains, simulate an calculate the error.
 *	@author kronenthaler
 */
public abstract class NeuralNetwork {
	protected Plotter plotter;
	protected JProgressBar progress;

	public void setPlotter(Plotter plotter){ this.plotter = plotter; }
	public void setProgressBar(JProgressBar pb) { progress = pb; }

	/** Instance preallocated of the function signum */
	public static Signum signum = new Signum();

	/** Instance preallocated of the function symmetric signum */
	public static SymmetricSignum ssignum = new SymmetricSignum();

	/** Instance preallocated of the function identity */
	public static Identity identity = new Identity();

	/** Instance preallocated of the function hyperbolic tangent */
	public static HyperbolicTangent tanh = new HyperbolicTangent();

	/** Instance preallocated of the function sigmoid */
	public static Sigmoid sigmoid = new Sigmoid();

	/**
	 *	Train this neural network with the list of <code>patterns</code> and the expected <code>answers</code>.
	 *	Use the learning rate <code>alpha</code> for many <code>epochs</code>. Take <code>length</code> patterns from the
	 *	position <code>offset</code> until the <code>minerror</code> will be reach.
	 *	@param patterns	The patterns to be learned.
	 *	@param answers The expected answers.
	 *	@param alpha	The learning rate.
	 *	@param epochs	The maximum number of iterations
	 *	@param offset	The first pattern position
	 *	@param length	How many patterns will be used.
	 *	@param minerror The minimal error expected.
	 */
	public abstract void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror);

	/**
	 *	Calculate the output for the <code>pattern</code>.
	 *	@param pattern Pattern to use as input.
	 *	@return The output for the neural network.
	 */
	public abstract Matrix simulate(Matrix pattern);

	/**
	 *	Calculate the output for the <code>pattern</code> and left the result in <code>result</code>.
	 *	@param pattern Pattern to use as input.
	 *	@param result The output for the input.
	 */
	public abstract void simulate(Matrix pattern,Matrix result);
    
    /**
	 *	Save the neural network to the file in the <code>path</code>
	 *	@param path The path for the output file.
	 *	@return <code>true</code> if the file can be created and written, <code>false</code> otherwise.
	 */
    public abstract boolean save(String path);

	/**
	 *	Open the neural network from the file in the <code>path</code>
	 *	@param path The path for the input file.
	 *	@return <code>true</code> if the file can be created and readed, <code>false</code> otherwise.
	 */
    public abstract boolean open(String path);

	/**
	 *	Alias of train(patterns, answers, alpha, epochs, 0, patterns.length, 1.e-5);
	 *	@param patterns	The patterns to be learned.
	 *	@param answers The expected answers.
	 *	@param alpha	The learning rate.
	 *	@param epochs	The maximum number of iterations
	 */
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs){
		train(patterns, answers, alpha, epochs, 0, patterns.length, 1.e-5);
	}

	/**
	 *	Alias of train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	 *	@param patterns	The patterns to be learned.
	 *	@param answers The expected answers.
	 *	@param alpha	The learning rate.
	 *	@param epochs	The maximum number of iterations
	 *	@param offset	The first pattern position
	 *	@param length	How many patterns will be used.
	 */
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs,int offset, int length){
		train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	}

	/**
	 *	Calculate from a set of patterns.
	 *	Alias of error(patterns, answers, 0, patterns.length)
	 *	@param patterns The array with the patterns to test
	 *	@param answers The array with the expected answers for the patterns.
	 *	@return The error calculate for the patterns.
	 */
	public double error(Matrix[] patterns, Matrix[] answers){
		return error(patterns, answers, 0, patterns.length);
	}

	/**
	 *	Calculates the mean cuadratic error. Is the standard error metric for neural
	 *	networks. Just a few networks needs a diferent type of error metric.
	 *	@param patterns The array with the patterns to test
	 *	@param answers The array with the expected answers for the patterns.
	 *	@param offset The initial position inside the array.
	 *	@param length How many patterns must be taken from the offset.
	 *	@return The mean cuadratic error.
	 */
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		double error=0.0;
		Matrix Y = new Matrix(answers[0].getRows(), 1);

		for(int i=0,n=Y.getRows();i<length;i++){
			simulate(patterns[i+offset],Y);	//inner product

			for(int j=0;j<n;j++)
				error += Math.pow(answers[i+offset].position(j,0)-Y.position(j,0),2);
		}

		return error/(double)length;
	}

	/**
	 *	Calculates the square Euclidean distance between two vectors.
	 *	@param a Vector a.
	 *	@param b Vector b.
	 *	@return The square euclidean distance.
	 */
	public static double euclideanDistance2(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double diff = (a[i] - b[i]);
			sum += diff*diff;
		}
		return sum;
    }

	/**
	 *	Calculates the square Euclidean distance between two column matrix.
	 *	@param a Matrix a.
	 *	@param b Matrix b.
	 *	@return The square euclidean distance.
	 */
	public static double euclideanDistance2(Matrix a, Matrix b) {
		try{
			double sum = 0;
			for (int i = 0; i < a.getRows(); i++) {
				double diff = (a.position(i,0) - b.position(i,0));
				sum += diff*diff;
			}
			return sum;
		}catch(RuntimeException e){
			System.out.println("a: "+a);
			System.out.println("\nb: "+b);
			throw e;
		}
    }

	/**
	 *	Calculate the Gaussian function with standard desviation <code>sigma</code> and input parameter
	 *	<code>u^2</code>
	 *	@return e^(-u^2/2.sigma)
	 */
	public static double gaussian(double u2, double sigma) {
		return Math.exp((-u2) / (sigma * 2.0));
    }

	public static void shuffle(int[] sort){
		Random rand = new Random();
		for(int i=0;i<sort.length;i++){
			int j = rand.nextInt(sort.length);
			int aux=sort[i];
			sort[i]=sort[j];
			sort[j]=aux;
		}
	}
}

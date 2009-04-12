package net.sf.libai.nn;

import java.util.*;

import net.sf.libai.common.*;

/**
 *
 * @author kronenthaler
 */
public abstract class NeuralNetwork {
	protected Plotter plotter;

	public void setPlotter(Plotter plotter){ this.plotter = plotter; }

	public static Signum signum = new Signum();
	public static SymmetricSignum ssignum = new SymmetricSignum();
	public static Identity identity = new Identity();
	public static TangentHyperbolic tanh = new TangentHyperbolic();
	public static Sigmoid sigmoid = new Sigmoid();

	public abstract void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror);
    public abstract Matrix simulate(Matrix pattern);
	public abstract void simulate(Matrix pattern,Matrix result);
    
    //persistence
    public abstract boolean save(String path);
    public abstract boolean open(String path);

	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs){
		train(patterns, answers, alpha, epochs, 0, patterns.length, 1.e-5);
	}

	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs,int offset, int length){
		train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	}

	public double error(Matrix[] patterns, Matrix[] answers){
		return error(patterns, answers, 0, patterns.length);
	}

	/**
	 * mean cuadratic error.
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

	//common functions 
	public static double euclideanDistance2(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double diff = (a[i] - b[i]);
			sum += diff*diff;
		}
		return sum;
    }

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

	public static double gaussian(double u2, double sigma) {
		return Math.exp((-u2) / (sigma * 2.0));
    }
}

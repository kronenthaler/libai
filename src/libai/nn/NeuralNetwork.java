/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.nn;

import libai.common.ProgressDisplay;
import libai.common.Matrix;
import libai.common.Plotter;
import java.io.*;
import java.util.Random;
import javax.swing.JProgressBar;

/**
 * Neural network abstraction. Provides the methods to train, simulate and
 * calculate the error.
 *
 * @author kronenthaler
 */
public abstract class NeuralNetwork implements Serializable {
	protected transient Plotter plotter;
	protected transient ProgressDisplay progress;

	public void setPlotter(Plotter plotter) {
		this.plotter = plotter;
	}

	public void setProgressBar(ProgressDisplay pb) {
		progress = pb;
	}

	/**
	 * Train this neural network with the list of
	 * <code>patterns</code> and the expected <code>answers</code>. 
	 * Use the learning rate <code>alpha</code> for many <code>epochs</code>. 
	 * Take <code>length</code> patterns from the position <code>offset</code> 
	 * until the <code>minerror</code> is reached.
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers	The expected answers.
	 * @param alpha		The learning rate.
	 * @param epochs	The maximum number of iterations
	 * @param offset	The first pattern position
	 * @param length	How many patterns will be used.
	 * @param minerror	The minimal error expected.
	 */
	public abstract void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror);

	/**
	 * Calculate the output for the
	 * <code>pattern</code>.
	 *
	 * @param pattern	Pattern to use as input.
	 * @return The output for the neural network.
	 */
	public abstract Matrix simulate(Matrix pattern);

	/**
	 * Calculate the output for the
	 * <code>pattern</code> and left the result in
	 * <code>result</code>.
	 *
	 * @param pattern	Pattern to use as input.
	 * @param result	The output for the input.
	 */
	public abstract void simulate(Matrix pattern, Matrix result);

	/**
	 * Save the neural network to the file in the
	 * <code>path</code>
	 *
	 * @param path	The path for the output file.
	 * @return <code>true</code> if the file can be created and written, 
	 * <code>false</code> otherwise.
	 */
	public boolean save(String path) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(this);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Alias of train(patterns, answers, alpha, epochs, 0, patterns.length,
	 * 1.e-5);
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers	The expected answers.
	 * @param alpha	The learning rate.
	 * @param epochs	The maximum number of iterations
	 */
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs) {
		train(patterns, answers, alpha, epochs, 0, patterns.length, 1.e-5);
	}

	/**
	 * Alias of train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers	The expected answers.
	 * @param alpha	The learning rate.
	 * @param epochs	The maximum number of iterations
	 * @param offset	The first pattern position
	 * @param length	How many patterns will be used.
	 */
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length) {
		train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	}

	/**
	 * Calculate from a set of patterns. Alias of error(patterns, answers, 0,
	 * patterns.length)
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers The array with the expected answers for the patterns.
	 * @return The error calculate for the patterns.
	 */
	public double error(Matrix[] patterns, Matrix[] answers) {
		return error(patterns, answers, 0, patterns.length);
	}

	/**
	 * Calculates the mean quadratic error. Is the standard error metric for
	 * neural networks. Just a few networks needs a different type of error
	 * metric.
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers The array with the expected answers for the patterns.
	 * @param offset The initial position inside the array.
	 * @param length How many patterns must be taken from the offset.
	 * @return The mean quadratic error.
	 */
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		double error = 0.0;
		Matrix Y = new Matrix(answers[0].getRows(), 1);

		for (int i = 0, n = Y.getRows(); i < length; i++) {
			simulate(patterns[i + offset], Y);	//inner product

			for (int j = 0; j < n; j++)
				error += Math.pow(answers[i + offset].position(j, 0) - Y.position(j, 0), 2);
		}

		return error / (double) length;
	}

	/**
	 * Calculates the square Euclidean distance between two vectors.
	 *
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The square Euclidean distance.
	 */
	public static double euclideanDistance2(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double diff = (a[i] - b[i]);
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Calculates the square Euclidean distance between two column matrix.
	 *
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The square Euclidean distance.
	 */
	public static double euclideanDistance2(Matrix a, Matrix b) {
		try {
			double sum = 0;
			for (int i = 0; i < a.getRows(); i++) {
				double diff = (a.position(i, 0) - b.position(i, 0));
				sum += diff * diff;
			}
			return sum;
		} catch (RuntimeException e) {
			System.out.println("a: " + a);
			System.out.println("\nb: " + b);
			throw e;
		}
	}

	/**
	 * Calculate the Gaussian function with standard deviation
	 * <code>sigma</code> and input parameter
	 * <code>u^2</code>
	 *
	 * @param u2  {@code u2}
	 * @param sigma  {@code sigma}
	 * @return e^(-u^2/2.sigma)
	 */
	public static double gaussian(double u2, double sigma) {
		return Math.exp((-u2) / (sigma * 2.0));
	}

	public static void shuffle(int[] sort) {
		Random rand = new Random();
		for (int i = 0; i < sort.length; i++) {
			int j = rand.nextInt(sort.length);
			int aux = sort[i];
			sort[i] = sort[j];
			sort[j] = aux;
		}
	}
}

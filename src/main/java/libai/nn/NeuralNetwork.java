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

import libai.common.matrix.Column;
import libai.common.Plotter;
import libai.common.Precondition;
import libai.common.ProgressDisplay;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Neural network abstraction. Provides the methods to train, simulate and
 * calculate the error.
 *
 * @author kronenthaler
 */
public abstract class NeuralNetwork implements Serializable {
	private static final long serialVersionUID = 2851521924022998819L;
	protected final Random random;
	protected transient Plotter plotter;
	protected transient ProgressDisplay progress;

	public NeuralNetwork() {
		this(getDefaultRandomGenerator());
	}

	public NeuralNetwork(Random rand) {
		random = rand;
	}

	public static final Random getDefaultRandomGenerator() {
		return ThreadLocalRandom.current();
	}

	public static final <NN extends NeuralNetwork> NN open(String path) throws IOException, ClassNotFoundException {
		return (NN) open(new File(path));
	}

	public static final <NN extends NeuralNetwork> NN open(File file) throws IOException, ClassNotFoundException {
		try (FileInputStream fis = new FileInputStream(file)) {
			return (NN) open(fis);
		}
	}

	public static final <NN extends NeuralNetwork> NN open(InputStream input) throws IOException, ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(input)) {
			return (NN) in.readObject();
		}
	}

	/**
	 * Calculates the square Euclidean distance between two vectors.
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The square Euclidean distance.
	 */
	public static double euclideanDistance2(double[] a, double[] b) {
		Precondition.check(a.length == b.length, "a & b must have the same length");

		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double diff = (a[i] - b[i]);
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Calculates the square Euclidean distance between two column matrix.
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param a Column matrix a.
	 * @param b Column matrix b.
	 * @return The square Euclidean distance.
	 */
	public static double euclideanDistance2(Column a, Column b) {
		Precondition.check(a.getRows() == b.getRows(), "a & b must have the same length");

		double sum = 0;
		for (int i = 0; i < a.getRows(); i++) {
			double diff = (a.position(i, 0) - b.position(i, 0));
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Calculates the Gaussian function with standard deviation
	 * {@code sigma} and input parameter {@code u^2}
	 *
	 * @param u2    {@code u2}
	 * @param sigma {@code sigma}
	 * @return {@code e^(-u^2/2.sigma)}
	 */
	public static double gaussian(double u2, double sigma) {
		return Math.exp((-u2) / (sigma * 2.0));
	}

	public Plotter getPlotter() {
		return plotter;
	}

	public void setPlotter(Plotter plotter) {
		this.plotter = plotter;
	}

	public ProgressDisplay getProgressBar() {
		return progress;
	}

	/**
	 * Sets a {@link ProgressDisplay} to the {@code NeuralNetwork}. The value
	 * will go from {@code -epochs} to {@code 0}, and updated every training
	 * epoch.
	 * <p><i>Note: </i> Classes that implement {@link
	 * NeuralNetwork#train(Column[], Column[], double, int, int, int, double)}
	 * are responsible for this behavior.</p>
	 *
	 * @param pb ProgressDisplay
	 */
	public void setProgressBar(ProgressDisplay pb) {
		progress = pb;
	}

	/**
	 * Trains this neural network with the list of {@code patterns} and the
	 * expected {@code answers}.
	 * <p>Use the learning rate {@code alpha} for many {@code epochs}.
	 * Take {@code length} patterns from the position {@code offset} until the
	 * {@code minerror} is reached.</p>
	 * <p>{@code patterns} and {@code answers} must be arrays of
	 * non-{@code null} <b>column</b> matrices</p>
	 *
	 * @param patterns The patterns to be learned.
	 * @param answers  The expected answers.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @param offset   The first pattern position
	 * @param length   How many patterns will be used.
	 * @param minerror The minimal error expected.
	 */
	public abstract void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror);

	/**
	 * Alias of train(patterns, answers, alpha, epochs, 0, patterns.length,
	 * 1.e-5).
	 * <p>{@code patterns} and {@code answers} must be arrays of
	 * non-{@code null} <b>column</b> matrices</p>
	 *
	 * @param patterns The patterns to be learned.
	 * @param answers  The expected answers.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @see NeuralNetwork#train(Column[], Column[], double, int, int, int, double)
	 */
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs) {
		train(patterns, answers, alpha, epochs, 0, patterns.length, 1.e-5);
	}

	/**
	 * Alias of train(patterns, answers, alpha, epochs, offset, length, 1.e-5).
	 * <p>{@code patterns} and {@code answers} must be arrays of
	 * non-{@code null} <b>column</b> matrices</p>
	 *
	 * @param patterns The patterns to be learned.
	 * @param answers  The expected answers.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @param offset   The first pattern position
	 * @param length   How many patterns will be used.
	 * @see NeuralNetwork#train(Column[], Column[], double, int, int, int, double)
	 */
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length) {
		train(patterns, answers, alpha, epochs, offset, length, 1.e-5);
	}

	/**
	 * Calculates the output for the {@code pattern}.
	 *
	 * @param pattern Pattern to use as input.
	 * @return The output for the neural network.
	 */
	public abstract Column simulate(Column pattern);

	/**
	 * Calculates the output for the {@code pattern} and left the result in
	 * {@code result}.
	 *
	 * @param pattern Pattern to use as input.
	 * @param result  The output for the input.
	 */
	public abstract void simulate(Column pattern, Column result);

	/**
	 * Saves the neural network to the file in the given {@code path}
	 *
	 * @param path The path for the output file.
	 * @return {@code true} if the file can be created and written,
	 * {@code false} otherwise.
	 */
	public boolean save(String path) {
		try (FileOutputStream fos = new FileOutputStream(path);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(this);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Calculates from a set of patterns. Alias of error(patterns, answers, 0,
	 * patterns.length)
	 * <p>{@code patterns} and {@code answers} must be arrays of
	 * non-{@code null} <b>column</b> matrices</p>
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers  The array with the expected answers for the patterns.
	 * @return The error calculate for the patterns.
	 * @see NeuralNetwork#error(Column[], Column[], int, int)
	 */
	public double error(Column[] patterns, Column[] answers) {
		return error(patterns, answers, 0, patterns.length);
	}

	/**
	 * Calculates the mean quadratic error. It is the standard error metric for
	 * neural networks. Just a few networks needs a different type of error
	 * metric.
	 * <p>{@code patterns} and {@code answers} must be arrays of
	 * non-{@code null} <b>column</b> matrices</p>
	 * <p><i>NOTE:</i> Assertions of the dimensions are made with {@code assert}
	 * statement. You must enable this on runtime to be effective.</p>
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers  The array with the expected answers for the patterns.
	 * @param offset   The initial position inside the array.
	 * @param length   How many patterns must be taken from the offset.
	 * @return The mean quadratic error.
	 */
	public double error(Column[] patterns, Column[] answers, int offset, int length) {
		Precondition.check(patterns.length == answers.length, "There must be the same amount of patterns and answers");
		Precondition.check(offset >= 0 && offset < patterns.length, "offset must be in the interval [0, %d), found,  %d", patterns.length, offset);
		Precondition.check(length >= 0 && length <= patterns.length - offset, "length must be in the interval (0, %d], found,  %d", patterns.length - offset, length);

		double error = 0.0;
		Column Y = new Column(answers[0].getRows());

		for (int i = 0; i < length; i++) {
			simulate(patterns[i + offset], Y);    //inner product
			error += euclideanDistance2(answers[i + offset], Y);
		}

		return error / (double) length;
	}

	protected void initializeProgressBar(int maximum) {
		if (progress != null) {
			progress.setMaximum(maximum);
			progress.setMinimum(0);
			progress.setValue(0);
		}
	}
}

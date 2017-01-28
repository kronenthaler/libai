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
package libai.nn.unsupervised;

import libai.common.Matrix;
import libai.nn.NeuralNetwork;

import java.util.Random;

/**
 * Competitive Learning is an unsupervised network where "the winner takes all".
 * A pattern is presented to each neuron, the closest neuron wins the right to
 * be updated. The update makes this neuron fittest for that pattern in the
 * future. Finally, the network learns a set of descriptive patterns or
 * centroids (if is compared with a clustering algorithm). In general, the
 * output for that network will be a binary codified class, when only one bit on
 * (the winner). One of the problems with the competitive learning is the unable
 * to know beforehand, the order of the output. For example, suppose we have 3
 * possible outputs (100, 010, 001). For the first random initialization, the
 * winner for the first pattern is the neuron 0, so the output is 100. But, if
 * retrain with a new random initialization, the winner for the same pattern
 * will be the neuron 2, so the output is 001. That is not necessary incorrect
 * because the relative order could be maintained between the inputs. Therefore,
 * before calculate any error metric we need to label the patterns with the
 * respective answers, in a similar way as Kohonen does.
 *
 * @author kronenthaler
 */
public class Competitive extends NeuralNetwork {
	private static final long serialVersionUID = 3792932568798202152L;

	protected Matrix W;
	protected int ins, outs;
	protected int winner;

	/**
	 * Constructor. Creates a network with the specified number of inputs and
	 * outputs.
	 *
	 * @param in Number of inputs.
	 * @param out Number of outputs.
	 */
	public Competitive(int in, int out) {
		this(in, out, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Creates a network with the specified number of inputs and
	 * outputs.
	 *
	 * @param in Number of inputs.
	 * @param out Number of outputs.
	 * @param rand Random generator used for creating matrices
	 */
	public Competitive(int in, int out, Random rand) {
		super(rand);
		ins = in;
		outs = out;
		W = new Matrix(outs, ins);

		W.fill(true, random);
	}

	/**
	 * Train the network using "the winner takes all". For each neuron the
	 * Euclidean distance between the pattern and the neuron is calculated. The
	 * position with the lowest distance is updated with the rule:<br>
	 * Ww = Ww + alpha.(pattern - Ww)<br>
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers 	The expected answers.
	 * @param alpha		The learning rate.
	 * @param epochs	The maximum number of iterations
	 * @param offset	The first pattern position
	 * @param length	How many patterns will be used.
	 * @param minerror The minimal error expected.
	 */
	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort = new int[length]; // [0,length)
		double error = 1;

		Matrix r = new Matrix(1, ins);
		Matrix row = new Matrix(1, ins);

		//initialize sort array
		Matrix[] patternsT = new Matrix[length];
		for (int i = 0; i < length; i++) {
			patternsT[i] = patterns[i + offset].transpose();
			sort[i] = i;
		}

		if (progress != null) {
			progress.setMaximum(epochs);
			progress.setMinimum(0);
			progress.setValue(0);
		}

		for(int currentEpoch=0; currentEpoch < epochs && error > minerror; currentEpoch++){
			//shuffle patterns
			shuffle(sort);

			for (int i = 0; i < length; i++) {
				//calculate the distance of each pattern to each neuron (rows in W), keep the winner
				simulateNoChange(patterns[sort[i] + offset]);

				//Ww = Ww + alpha . (p - Ww); w is the row of winner neuron
				patternsT[sort[i]].copy(r);
				row.setRow(0, W.getRow(winner));
				r.subtract(row, r);
				r.multiply(alpha, r);
				row.add(r, r);

				W.setRow(winner, r.getRow(0));
			}

			error = error(patterns, answers, offset, length);

			if (plotter != null)
				plotter.setError(currentEpoch, error);
			if (progress != null)
				progress.setValue(currentEpoch);
		}

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		Matrix ret = new Matrix(W.getRows(), 1);
		simulate(pattern, ret);
		return ret;
	}

	/**
	 * Calculate the output for the
	 * <code>pattern</code> and left the result in
	 * <code>result</code>. The result will be a row matrix fill with 0 except
	 * for the winner position.
	 *
	 * @param pattern Pattern to use as input.
	 * @param result The output for the input.
	 */
	@Override
	public void simulate(Matrix pattern, Matrix result) {
		simulateNoChange(pattern);

		result.setValue(0);
		result.position(winner, 0, 1);
	}

	protected void simulateNoChange(Matrix pattern) {
		double[] row;
		double d = Double.MAX_VALUE;
		winner = -1;
		for (int j = 0; j < W.getRows(); j++) {
			row = W.getRow(j);
			double dist = euclideanDistance2(pattern.getCol(0), row);
			if (dist < d) {
				d = dist;
				winner = j;
			}
		}
	}

	/**
	 * Calculate the error using the average distance between the closest
	 * neuron. Less distance means less error and vice versa.
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers The array with the expected answers for the patterns.
	 * @param offset The initial position inside the array.
	 * @param length How many patterns must be taken from the offset.
	 * @return The average distance between the pattern and the winner for that
	 * pattern.
	 */
	@Override
	public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
		//average of the distances to the closest neuron
		double[] row;
		double acum = 0;
		for (int i = offset; i < offset + length; i++) {
			double d = Double.MAX_VALUE;

			for (int j = 0; j < outs; j++) {
				row = W.getRow(j);
				double dist = Math.sqrt(euclideanDistance2(patterns[i].getCol(0), row));
				d = Math.min(dist, d);
			}

			acum += d;
		}

		return acum / (double) length;
	}
}

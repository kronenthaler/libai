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
package libai.nn.supervised;

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.matrix.Row;
import libai.nn.unsupervised.Competitive;

import java.util.Random;

/**
 * Learning Vector Quantization or LVQ. Is an hybrid neural network with 3
 * layers (1-input, 1-hidden, 1-output). The first set of weights are trained
 * using a competitive approach, and the second set of weights are trained using
 * a supervised approach. Therefore the first steps are taken from the
 * competitive network. This network was proposed by Teuvo Kohonen as
 * alternative to the standard competitive. networks.
 *
 * @author kronenthaler
 */
public class LVQ extends Competitive {
	private static final long serialVersionUID = 6603129562167746698L;

	protected Matrix W2;
	protected int subclasses;

	/**
	 * Constructor. Number of inputs, number of subclasses and number of
	 * outputs.
	 *
	 * @param in       Number of input to the network.
	 * @param subclass Number of subclasses for output class. Greater subdivision
	 *                 provides better classification.
	 * @param out      Number of outputs for the network
	 */
	public LVQ(int in, int subclass, int out) {
		this(in, subclass, out, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Number of inputs, number of subclasses and number of
	 * outputs.
	 *
	 * @param in       Number of input to the network.
	 * @param subclass Number of subclasses for output class. Greater subdivision
	 *                 provides better classification.
	 * @param out      Number of outputs for the network
	 * @param rand     Random generator used for creating matrices
	 */
	public LVQ(int in, int subclass, int out, Random rand) {
		super(in, out, rand);

		subclasses = subclass;

		W = new Matrix(subclasses * outs, ins);
		W2 = new Matrix(outs, subclasses * outs);

		W.fill(true, random);
		W2.setValue(0);

		//fill W2 with 1 per row
		for (int i = 0, j = 0, k = 0; i < W2.getColumns(); i++) {
			W2.position(j, i, 1);
			if (k++ == subclasses - 1) {
				j++;
				k = 0;
			}
		}
	}

	/**
	 * Train the network using a hybrid scheme. Uses the "winner takes all"
	 * rule.
	 *
	 * @param patterns The patterns to be learned.
	 * @param answers  The expected answers.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @param offset   The first pattern position
	 * @param length   How many patterns will be used.
	 * @param minerror The minimal error expected.
	 */
	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		int[] sort = new int[length];
		double error = 1;
		Row r = new Row(ins);
		Row row = new Row(W.getColumns());

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

		for (int currentEpoch = 0; currentEpoch < epochs && error > minerror; currentEpoch++) {
			//shuffle patterns
			shuffle(sort);

			for (int i = 0; i < length; i++) {
				//calculate the distance of each pattern to each neuron (rows in W), keep the winner
				int winnerOut = -1;
				int winnerT = -1;

				simulateNoChange(patterns[sort[i] + offset]);

				//find the row with the value 1 in the column winner of W2
				for (int j = 0; j < W2.getRows(); j++) {
					if (W2.position(j, winner) == 1)
						winnerOut = j;
					if (answers[sort[i] + offset].position(j, 0) == 1)
						winnerT = j;
				}

				//Ww = Ww +/- alpha . (p - Ww); //w is the row of winner neuron
				patternsT[sort[i]].copy(r);
				row.setRow(0, W.getRow(winner));
				r.subtract(row, r);
				r.multiply((winnerT == winnerOut) ? alpha : -alpha, r); //if winner in T == winner int out + else -
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
	public Column simulate(Column pattern) {
		Column ret = new Column(outs);
		W2.multiply(super.simulate(pattern), ret);
		return ret;
	}

	/**
	 * Calculates the number of incorrect answers over the total.
	 *
	 * @param patterns The array with the patterns to test
	 * @param answers  The array with the expected answers for the patterns.
	 * @param offset   The initial position inside the array.
	 * @param length   How many patterns must be taken from the offset.
	 * @return The relation between the incorrect answers and the total number
	 * of answers.
	 */
	@Override
	public double error(Column[] patterns, Column[] answers, int offset, int length) {
		//relation between correct answers and total answers
		int correct = 0;
		Column ret1 = new Column(W2.getColumns());
		Column ret = new Column(outs);

		for (int i = 0; i < length; i++) {
			simulate(patterns[i + offset], ret1);
			W2.multiply(ret1, ret);

			if (ret.equals(answers[i + offset]))
				correct++;
		}

		return (length - correct) / (double) length;
	}
}

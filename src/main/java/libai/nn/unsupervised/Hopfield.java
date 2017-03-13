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

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.functions.SymmetricSign;
import libai.common.matrix.Row;
import libai.nn.NeuralNetwork;

/**
 * Hopfield's networks are the most important and most applicable recurrent
 * neural network. This Hopfield networks uses an deterministic unsupervised
 * training algorithm and a bipolar encoding for the training patterns and
 * answers. As the Hebb network this network is a associative memory. The main
 * goal of this network is memorize and retrieve the memorized patterns without
 * noise.
 *
 * @author kronenthaler
 */
public class Hopfield extends NeuralNetwork {
	private static final long serialVersionUID = 9081060788269921587L;

	protected Matrix W;
	protected static SymmetricSign ssign = new SymmetricSign();

	/**
	 * Constructor. Receives the number of input to the network.
	 *
	 * @param inputs The number of input to the network.
	 */
	public Hopfield(int inputs) {
		W = new Matrix(inputs, inputs);
	}

	/**
	 * Train the network. The answers, alpha, epochs and minerror are meaningless
	 * in this algorithm.
	 *
	 * @param patterns The patterns to be learned.
	 * @param answers  The expected answers. [ignored]
	 * @param alpha    The learning rate. [ignored]
	 * @param epochs   The maximum number of iterations. [ignored]
	 * @param offset   The first pattern position.
	 * @param length   How many patterns will be used.
	 * @param minerror The minimal error expected. [ignored]
	 */
	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		Row patternT = new Row(patterns[0].getRows());
		Matrix temp = new Matrix(W.getRows(), W.getColumns());

		if (progress != null) {
			progress.setMaximum(length - 1);
			progress.setMinimum(0);
			progress.setValue(0);
		}

		// W = Sum(p[i]p[i]^t); wii = 0
		for (int i = 0; i < length; i++) {
			patterns[i + offset].apply(ssign, patterns[i + offset]);
			Matrix pattern = patterns[i + offset];

			//p^t.p
			pattern.transpose(patternT);
			pattern.multiply(patternT, temp);
			W.add(temp, W);

			if (progress != null)
				progress.setValue(i);
		}

		for (int i = 0; i < W.getRows(); i++)
			W.position(i, i, 0);

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	@Override
	public Column simulate(Column pattern) {
		Column result = new Column(pattern.getRows());
		simulate(pattern, result);
		return result;
	}

	@Override
	public void simulate(Column pattern, Column result) {
		pattern.copy(result);
		Column previous = new Column(pattern);
		previous.setValue(0);

		while (!result.equals(previous)) {
			result.copy(previous);

			for (int col = 0; col < result.getRows(); col++) {
				Matrix column = new Matrix(result.getRows(), result.getColumns(), W.getCol(col));

				double dotProduct = result.dotProduct(column);
				result.position(col, 0, dotProduct == 0 ? dotProduct : (dotProduct > 0 ? 1 : -1));
			}
		}
	}
}

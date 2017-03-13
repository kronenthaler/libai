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
import libai.common.functions.SymmetricSign;
import libai.nn.NeuralNetwork;

import java.util.Random;

/**
 * Hebbian supervised networks are good for pattern retrieval and reconstructions.
 * These networks are only able to learn binary patterns because its
 * output function (symmetric sign). However, they can deal with partially corrupted
 * patterns and retrieve the original one without noise. The Hebbian networks uses the Hebb's rule for
 * training. The Hebb's rule is one of the most important training rules in
 * unsupervised networks. Other algorithms like Kohonen uses this rule as base.
 *
 * @author kronenthaler
 */
public class Hebb extends NeuralNetwork {
	private static final long serialVersionUID = 7754681003525186940L;

	protected double phi;
	protected Matrix W;
	protected static SymmetricSign sign = new SymmetricSign();


	/**
	 * Constructor. Creates a Hebbian network with the equals number of inputs
	 * and outputs. Set the decay constant to zero to eliminate it. Alias of
	 * this(inputs, 0);
	 *
	 * @param inputs  Number of inputs for the network.
	 * @param outputs Number of outputs for the network.
	 */
	public Hebb(int inputs, int outputs) {
		this(inputs, outputs, 0);
	}

	/**
	 * Constructor. Creates a Hebbian network with the equals number of inputs
	 * and outputs. Set the constant for decay
	 * <code>phi</code>. If phi = 0 the network don't forget anything, if phi =
	 * 1 the network just remember the las pattern.
	 *
	 * @param inputs  Number of inputs and outputs for the networks.
	 * @param outputs Number of outputs for the network.
	 * @param phi     Decay constant.
	 */
	public Hebb(int inputs, int outputs, double phi) {
		this(inputs, outputs, phi, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Creates a Hebbian network with the equals number of inputs
	 * and outputs. Set the constant for decay
	 * <code>phi</code>. If phi = 0 the network don't forget anything, if phi =
	 * 1 the network just remember the las pattern.
	 *
	 * @param inputs  Number of inputs and outputs for the networks.
	 * @param outputs Number of outputs for the network.
	 * @param phi     Decay constant.
	 * @param rand    Random generator used for creating matrices
	 */
	public Hebb(int inputs, int outputs, double phi, Random rand) {
		super(rand);
		this.phi = 1 - phi; //precalculation for the decay 1-phi
		W = new Matrix(outputs, inputs);
		W.setValue(0); // important!! the network should be initialized with 0
	}

	/**
	 * Train the network using the Hebb's rule with decay. The hebb's rule,
	 * consist on reinforce the right connections if they produce a correct
	 * answer and inhibit the others. The decay term has an influence in how
	 * much affects the previous knowledge to the reinforcement.
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
		Matrix temp = new Matrix(W.getRows(), W.getColumns());
		double error = 1;

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
				//F(wx)
				//simulate(patterns[sort[i] + offset], Y); // for unsupervised training
				Matrix Y = answers[sort[i] + offset];

				//W=(1-phi)*W + alpha*Y*pt;
				W.multiply(phi, W);
				Y.multiply(patternsT[sort[i]], temp);
				temp.multiply(alpha, temp);
				W.add(temp, W);
			}

			error = error(patterns, answers, offset, length);

			if (plotter != null)
				plotter.setError(currentEpoch, error);
			if (progress != null)
				progress.setValue(epochs);
		}

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	@Override
	public Column simulate(Column pattern) {
		Column ret = new Column(W.getRows()); // must match the output size
		simulate(pattern, ret);
		return ret;
	}

	/**
	 * Calculate the output for the pattern and left the result on result.
	 * result = sign(W * pattern)
	 *
	 * @param pattern The input pattern
	 * @param result  The output result.
	 */
	@Override
	public void simulate(Column pattern, Column result) {
		W.multiply(pattern, result);
		result.apply(sign, result);
	}
}

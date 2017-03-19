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


import libai.common.Shuffler;
import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.functions.Sign;

import java.util.Random;


/**
 * Perceptron is the first trainable neural network proposed. The network is
 * formed by one matrix (Weights) and one vector (Bias). The output for the
 * network is calculated by O = sign(W * pattern + b).
 *
 * @author kronenthaler
 */
public class Perceptron extends SupervisedLearning {
	private static final long serialVersionUID = 2795822735956649552L;
	protected static Sign signum = new Sign();
	protected Matrix W;
	protected Column b;
	protected int ins, outs;

	/**
	 * Constructor.
	 *
	 * @param in  Number of inputs for the network = number of elements in the
	 *            patterns.
	 * @param out Number of outputs for the network.
	 */
	public Perceptron(int in, int out) {
		this(in, out, getDefaultRandomGenerator());
	}

	/**
	 * Constructor.
	 *
	 * @param in   Number of inputs for the network = number of elements in the
	 *             patterns.
	 * @param out  Number of outputs for the network.
	 * @param rand Random generator used for creating matrices
	 */
	public Perceptron(int in, int out, Random rand) {
		super(rand);

		ins = in;
		outs = out;

		W = new Matrix(outs, ins);
		b = new Column(out);

		W.fill(true, random);
		b.fill(true, random);
	}

	/**
	 * Train the perceptron using the standard update rule: <br>
	 * W = W + alpha.e.pattern^t<br>
	 * b = b + alpha.e
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
		validatePreconditions(patterns, answers, epochs, offset, length, minerror);

		Matrix[] patternsT = new Matrix[length];
		for (int i = 0; i < length; i++) {
			patternsT[i] = patterns[i + offset].transpose();
		}

		Column Y = new Column(outs);
		Column E = new Column(outs);
		Matrix aux = new Matrix(outs, ins);

		double error = 1;
		Shuffler shuffler = new Shuffler(length, this.random);
		initializeProgressBar(epochs);

		for (int currentEpoch = 0; currentEpoch < epochs && error > minerror; currentEpoch++) {
			//shuffle patterns
			int[] sort = shuffler.shuffle();

			for (int i = 0; i < length; i++) {
				//F(wx+b)
				simulate(patterns[sort[i] + offset], Y);

				//e=t-y
				answers[sort[i] + offset].subtract(Y, E);    //error

				//alpha*e.p^t
				E.multiply(alpha, E);
				E.multiply(patternsT[sort[i]], aux);

				W.add(aux, W);//W+(alpha*e.p^t)
				b.add(E, b);  //b+(alpha*e)
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
	public Column simulate(Column p) {
		Column Y = new Column(outs);
		simulate(p, Y);
		return Y;
	}

	/**
	 * Calculate the output for the pattern and left the result on result.
	 * result = signum(W * pattern + b)
	 *
	 * @param pattern The input pattern
	 * @param result  The output result.
	 */
	@Override
	public void simulate(Column pattern, Column result) {
		W.multiply(pattern, result);    //inner product
		result.add(b, result);            //bias
		result.apply(signum, result);    //thresholding
	}
}
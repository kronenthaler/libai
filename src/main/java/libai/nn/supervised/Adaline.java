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

import java.util.Random;
import libai.common.Matrix;

/**
 * <b>Ada</b>ptative <b>Line</b>ar neural network. Is a special case of the
 * single layer Perceptron. Uses a identity as exit function. The only
 * difference between the training algorithms is the 2*alpha multiplication.
 * Because of this, the Adaline implementation is a subclass of Perceptron
 * single layer.
 *
 * @author kronenthaler
 */
public class Adaline extends Perceptron {
	private static final long serialVersionUID = 6108456796562627466L;

	public Adaline() {
	}

	/**
	 * Constructor.
	 *
	 * @param ins Number of inputs for the network = number of elements in the
	 * patterns.
	 * @param outs Number of outputs for the network.
	 */
	public Adaline(int ins, int outs) {
		super(ins, outs);
	}

	/**
	 * Constructor.
	 *
	 * @param ins Number of inputs for the network = number of elements in the
	 * patterns.
	 * @param outs Number of outputs for the network.
	 * @param rand Random generator used for creating matrices
	 */
	public Adaline(int ins, int outs, Random rand) {
		super(ins, outs, rand);
	}

	/**
	 * Alias of super.train(patterns, answers, 2*alpha, epochs, offset, length,
	 * minerror);
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers The expected answers.
	 * @param alpha	The learning rate.
	 * @param epochs	The maximum number of iterations
	 * @param offset	The first pattern position
	 * @param length	How many patterns will be used.
	 * @param minerror The minimal error expected.
	 */
	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		super.train(patterns, answers, 2 * alpha, epochs, offset, length, minerror);
	}

	/**
	 * Calculate the output for the pattern and left the result on result.
	 * result = W * pattern + b
	 *
	 * @param pattern The input pattern
	 * @param result The output result.
	 */
	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern, result);	//inner product
		result.add(b, result);		//bias
	}
}

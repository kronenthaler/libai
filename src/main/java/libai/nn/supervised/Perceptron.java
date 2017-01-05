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

import java.io.*;
import java.util.Random;
import libai.common.Matrix;
import libai.common.functions.Sign;
import libai.nn.NeuralNetwork;


/**
 * Perceptron is the first trainable neural network proposed. The network is
 * formed by one matrix (Weights) and one vector (Bias). The output for the
 * network is calculated by O = sign(W * pattern + b).
 *
 * @author kronenthaler
 */
public class Perceptron extends NeuralNetwork {
	private static final long serialVersionUID = 2795822735956649552L;

	protected Matrix W, b;
	protected int ins, outs;
	protected static Sign signum = new Sign();

	public Perceptron() {
	}

	/**
	 * Constructor.
	 *
	 * @param in Number of inputs for the network = number of elements in the
	 * patterns.
	 * @param out Number of outputs for the network.
	 */
	public Perceptron(int in, int out) {
		this(in, out, null);
	}

	/**
	 * Constructor.
	 *
	 * @param in Number of inputs for the network = number of elements in the
	 * patterns.
	 * @param out Number of outputs for the network.
	 * @param rand Random generator used for creating matrices
	 */
	public Perceptron(int in, int out, Random rand) {
		super(rand);

		ins = in;
		outs = out;

		W = new Matrix(outs, ins);
		b = new Matrix(out, 1);

		W.fill();
		b.fill();
	}

	/**
	 * Train the perceptron using the standard update rule: <br>
	 * W = W + alpha.e.pattern^t<br>
	 * b = b + alpha.e
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
		int[] sort = new int[length]; // [0,length)
		double error = 1;
		Matrix Y = new Matrix(outs, 1);
		Matrix E = new Matrix(outs, 1);
		Matrix aux = new Matrix(outs, ins);

		//initialize sort array
		Matrix[] patternsT = new Matrix[length];
		for (int i = 0; i < length; i++) {
			patternsT[i] = patterns[i + offset].transpose();
			sort[i] = i;
		}

		if (progress != null) {
			progress.setMaximum(0);
			progress.setMinimum(-epochs);
			progress.setValue(-epochs);
		}

		while ((error = error(patterns, answers, offset, length)) > minerror && epochs-- > 0) {
			//if(error > prevError) break; //optional to avoid overtrainning problems
			//shuffle patterns
			shuffle(sort);

			for (int i = 0; i < length; i++) {
				//F(wx+b)
				simulate(patterns[sort[i] + offset], Y);

				//e=t-y
				answers[sort[i] + offset].subtract(Y, E);	//error

				//alpha*e.p^t
				E.multiply(alpha, E);
				E.multiply(patternsT[sort[i]], aux);

				W.add(aux, W);//W+(alpha*e.p^t)
				b.add(E, b);  //b+(alpha*e)
			}

			if (plotter != null)
				plotter.setError(epochs, error);
			if (progress != null)
				progress.setValue(-epochs);
		}
		if (progress != null)
			progress.setValue(1);
	}

	@Override
	public Matrix simulate(Matrix p) {
		Matrix Y = new Matrix(outs, 1);
		simulate(p, Y);
		return Y;
	}

	/**
	 * Calculate the output for the pattern and left the result on result.
	 * result = signum(W * pattern + b)
	 *
	 * @param pattern The input pattern
	 * @param result The output result.
	 */
	@Override
	public void simulate(Matrix pattern, Matrix result) {
		W.multiply(pattern, result);		//inner product
		result.add(b, result);			//bias
		result.apply(signum, result);	//thresholding
	}

	/**
	 * Deserializes a {@code Perceptron}
	 *
	 * @param path Path to file
	 * @return Restored {@code Perceptron instance}
	 * @see NeuralNetwork#save(java.lang.String)
	 */
	public static Perceptron open(String path) {
		try (FileInputStream fis = new FileInputStream(path);
			 ObjectInputStream in = new ObjectInputStream(fis)) {
			return (Perceptron) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
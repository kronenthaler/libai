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
import libai.common.functions.Function;
import libai.nn.NeuralNetwork;
import libai.nn.supervised.backpropagation.Backpropagation;
import libai.nn.supervised.backpropagation.StandardBackpropagation;

import java.util.Random;

/**
 * Multi Layer Perceptron or MLP. MultiLayerPerceptron was the first algorithm proposed to train
 * multilayer neurons using the general delta rule. This implementation supports multiple backpropagation implementations
 * via a Backpropagation interface. Check the package {@code libai.nn.supervised.backpropagation} for more details about
 * the supported implementations.
 *
 * @author kronenthaler
 */
public class MultiLayerPerceptron extends NeuralNetwork {
	private static final long serialVersionUID = 3155220303024711102L;

	private final Matrix W[];
	private final Column Y[], b[], u[]; //WY + b = u

	private final int nperlayer[]; //number of neurons per layer, including the input layer
	private final int layers;
	private final Function[] func;
	private transient Backpropagation trainer;

	/**
	 * Constructor. Creates a MultiLayerPerceptron with {@code nperlayer.length} layers. The
	 * number of neurons per layer is defined in {@code nperlayer}.
	 * The {@code nperlayer[0]} means the input layer. For each layer {@code i}
	 * the neurons applies the output function {@code funcs[i]}. These functions
	 * must be derivable. The training algorithm is standard backpropagation.
	 *
	 * @param nperlayer Number of neurons per layer including the input layer.
	 * @param funcs Function to apply per layer. The function[0] could be null.
	 */
	public MultiLayerPerceptron(int[] nperlayer, Function[] funcs) {
		this(nperlayer, funcs, new StandardBackpropagation());
	}

	/**
	 * Constructor. Creates a MultiLayerPerceptron with {@code nperlayer.length} layers. The
	 * number of neurons per layer is defined in {@code nperlayer}. The
	 * {@code nperlayer[0]} means the input layer. For each layer the neurons
	 * applies the output function {@code funcs[i]}. These functions must be
	 * derivable. The parameter {@code beta} means the momentum influence.
	 * A different implementation of the backpropagation algorithm can be provided on the {@code trainer}
	 * object.
	 *
	 * @param nperlayer Number of neurons per layer including the input layer.
	 * @param funcs Function to apply per layer. The function[0] could be null.
	 * @param trainer The backpropagation implementation to be used during training
	 */
	public MultiLayerPerceptron(int[] nperlayer, Function[] funcs, Backpropagation trainer) {
		this(nperlayer, funcs, trainer, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Creates a MultiLayerPerceptron with {@code nperlayer.length} layers. The
	 * number of neurons per layer is defined in {@code nperlayer}. The
	 * {@code nperlayer[0]} means the input layer. For each layer the neurons
	 * applies the output function {@code funcs[i]}. These functions must be
	 * derivable.
	 * The training algorithm is standard backpropagation. A Random generator can be pass to
	 * initialize the matrices.
	 *
	 * @param nperlayer Number of neurons per layer including the input layer.
	 * @param funcs Function to apply per layer. The function[0] could be null.
	 * @param rand Random generator used for creating matrices
	 */
	public MultiLayerPerceptron(int[] nperlayer, Function[] funcs, Random rand) {
		this(nperlayer, funcs, new StandardBackpropagation(), rand);
	}

	/**
	 * Constructor. Creates a MultiLayerPerceptron with {@code nperlayer.length} layers. The
	 * number of neurons per layer is defined in {@code nperlayer}. The
	 * {@code nperlayer[0]} means the input layer. For each layer the neurons
	 * applies the output function {@code funcs[i]}. These functions must be
	 * derivable. A different backpropagation implementation can be provided along with a
	 * random generator to initialize the matrices.
	 *
	 * @param nperlayer Number of neurons per layer including the input layer.
	 * @param funcs Function to apply per layer. The function[0] could be null.
	 * @param trainer The backpropagation implementation to be used during training
	 * @param rand Random generator used for creating matrices
	 */
	public MultiLayerPerceptron(int[] nperlayer, Function[] funcs, Backpropagation trainer, Random rand) {
		super(rand);

		this.nperlayer = nperlayer;
		func = funcs;

		this.trainer = trainer;
		layers = nperlayer.length;

		W = new Matrix[layers];//position zero reserved
		b = new Column[layers];//position zero reserved
		Y = new Column[layers];//position zero reserved for the input pattern
		u = new Column[layers];//position zero reserved

		initialize();
	}

	/**
	 * Initialize the matrix and auxiliary buffers.
	 */
	private void initialize() {
		Y[0] = new Column(nperlayer[0]);

		for (int i = 1; i < layers; i++) {
			W[i] = new Matrix(nperlayer[i], nperlayer[i - 1]);
			b[i] = new Column(nperlayer[i]);

			W[i].fill(true, random); // fill randomly
			b[i].fill(true, random); // fill randomly

			u[i] = new Column(W[i].getRows());
			Y[i] = new Column(u[i].getRows());
		}
	}

	/**
	 * Train the network using the standard backpropagation algorithm. The
	 * pattern is propagated from the input to the final layer (the output).
	 * Then the error for the final layer is computed. The error is calculated
	 * backwards to the first hidden layer, calculating the differentials
	 * between input and expected output (backpropagation). Finally, the weights
	 * and biases are updated using the delta rule:<br>
	 * W[i] = W[i] + beta*(W[i]-Wprev[i]) - (1-beta)*alpha.d[i].Y[i-1]^t <br>
	 * B[i] = B[i] + beta*(B[i]-Bprev[i]) - (1-beta)*alpha.d[i]<br>
	 *  @param patterns    The patterns to be learned.
	 * @param answers The expected answers.
	 * @param alpha    The learning rate.
	 * @param epochs    The maximum number of iterations
	 * @param offset    The first pattern position
	 * @param length    How many patterns will be used.
	 * @param minerror The minimal error expected.
	 */
	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(epochs);
			progress.setValue(0);
		}

		// initialize the trainer with the set of matrices required
		trainer.initialize(this, nperlayer, func, W, Y, b, u);
		// train the network, a.k.a. update the weights of W, and b accordingly to the outputs.
		trainer.train(patterns, answers, alpha, epochs, offset, length, minerror);

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	@Override
	public Column simulate(Column pattern) {
		simulate(pattern, null);
		return Y[layers - 1];
	}

	@Override
	public void simulate(Column pattern, Column result) {
		//Y[0]=x
		pattern.copy(Y[0]);

		//Y[i]=Fi(<W[i],Y[i-1]>+b)
		for (int j = 1; j < layers; j++) {
			W[j].multiply(Y[j - 1], u[j]);
			u[j].add(b[j], u[j]);
			u[j].apply(func[j], Y[j]);
		}

		if (result != null)
			Y[layers - 1].copy(result);
	}
}
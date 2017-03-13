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
import libai.nn.NeuralNetwork;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Radial Basis Function or RBF. Is an hybrid neural network with 3 layers
 * (1-input, 1-hidden, 1-output). The hidden layers is trained using a
 * stochastic clustering algorithm: k-means. The final layer is trained using
 * the Adaline rule. The k-means algorithm is used to set up the position of the
 * "centers" of the radial basis functions, as this process is regardless of the
 * output and invariant over the input, could be used a highly efficient
 * algorithm. This implementation uses only Gaussian functions as radial basis
 * functions.
 *
 * @author kronenthaler
 */
public class RBF extends Adaline {
	private static final long serialVersionUID = 6772562276994202439L;

	private Column centers[];
	protected int nperlayer[];//{#inputs,#Neurons,#outputs}
	protected double[] sigma;

	/**
	 * Constructor. Receives an array with the information of the number of
	 * neurons per layer. Layer[0] is the input layer. Layer[1] is the hidden
	 * layer and represents the number radial functions to use. layer[2] is the
	 * output layer.
	 *
	 * @param nperlayer Neurons Per Layer.
	 */
	public RBF(int[] nperlayer) {
		this(nperlayer, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Receives an array with the information of the number of
	 * neurons per layer. Layer[0] is the input layer. Layer[1] is the hidden
	 * layer and represents the number radial functions to use. layer[2] is the
	 * output layer.
	 *
	 * @param nperlayer Neurons Per Layer.
	 * @param rand      Random generator used for creating matrices
	 */
	public RBF(int[] nperlayer, Random rand) {
		super(nperlayer[1], nperlayer[2], rand); // input, outputs

		this.nperlayer = nperlayer;

		sigma = new double[nperlayer[1]];
	}

	/**
	 * Train the network using a hybrid scheme. First set the centers of the
	 * radial basis functions using k-means algorithm. After that the radius of
	 * the function is calculated using n-nearest neighbors. When n = the number
	 * of inputs. Then the output for the hidden layer are precalculated and
	 * used as input for the Adaline training.
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
		// TODO: add Preconditions here
		if (progress != null) {
			progress.setMaximum(0);
			progress.setMinimum(-epochs * 2);
			progress.setValue(-epochs * 2);
		}

		//apply k-means to the patterns
		centers = kmeans(nperlayer[1], patterns, offset, length);

		//calculate sigmas. p-closest neighbors, p is the input dimension
		PriorityQueue<Double>[] neighbors = new PriorityQueue[nperlayer[1]];
		for (int i = 0; i < nperlayer[1]; i++)
			neighbors[i] = new PriorityQueue<>();

		for (int i = 0; i < nperlayer[1] - 1; i++) {
			for (int j = i + 1; j < nperlayer[1]; j++) {
				double current = Math.sqrt(euclideanDistance2(centers[i], centers[j]));
				neighbors[i].add(-current);
				neighbors[j].add(-current);
			}
		}

		for (int i = 0; i < nperlayer[1]; i++) {
			double acum = 0;
			for (int j = 0; j < nperlayer[0] && !neighbors[i].isEmpty(); j++) {
				acum += -neighbors[i].poll();
			}
			sigma[i] = acum / nperlayer[0];
		}

		//precalculate the ouputs for each pattern in the hidden layer
		Column Y[] = new Column[patterns.length];
		for (int j = 0; j < length; j++) {
			Y[j + offset] = new Column(nperlayer[1]);
			simulateNoChange(patterns[j + offset], Y[j + offset]);
		}

		//Train the adaline network, but keep the weights and biases in this instance.
		Adaline temp = new Adaline(nperlayer[1], nperlayer[2], random);
		temp.W = W;
		temp.b = b;
		temp.setPlotter(plotter);
		temp.setProgressBar(progress);
		temp.train(Y, answers, alpha, epochs, offset, length, minerror);

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	/**
	 * k-means clustering algorithm. For the cloud of patterns found the k
	 * points closest to every point in the cloud. The centroids found probably
	 * will no be any of the patterns in the cloud.
	 *
	 * @param k        Number of centroids to find.
	 * @param patterns The cloud of patterns
	 * @param offset   Initial position of the cloud.
	 * @param length   How many patterns are in the cloud.
	 * @return An array with the centroids.
	 */
	private Column[] kmeans(int k, Column[] patterns, int offset, int length) {
		int i, j, l;

		Column[] ctemp = new Column[k];
		ArrayList<Integer>[] partitions = new ArrayList[k];
		Column aux = new Column(patterns[0].getRows());
		Column aux1 = new Column(patterns[0].getRows());

		for (i = 0; i < k; i++) {
			ctemp[i] = new Column(patterns[0].getRows());
			int index = random.nextInt(length) + offset;//abs((int)(ctemp[i].random(&xzxzx)*npatterns));;
			patterns[index].copy(ctemp[i]);
			partitions[i] = new ArrayList<>();
		}

		while (true) {
			double min, current;
			for (l = 0; l < k; l++)
				partitions[l].clear();
			j = 0;
			for (i = 0; i < length; i++) {
				min = Double.MAX_VALUE;
				for (l = 0; l < k; l++) {
					current = euclideanDistance2(patterns[i + offset], ctemp[l]);
					if (current < min) {
						min = current;
						j = l;
					}
				}
				partitions[j].add(i + offset);
			}

			boolean exit = true;
			for (i = 0; i < k; i++) {
				int total = 0;
				aux1.setValue(0);
				for (j = 0; j < partitions[i].size(); j++) {
					aux1.add(patterns[partitions[i].get(j)], aux1);
					total++;
				}

				if (total == 0) {
					//empty partition take a random pattern as centroid
					ctemp[i] = new Column(patterns[0].getRows());
					int index = random.nextInt(length) + offset;
					patterns[index].copy(ctemp[i]);

					exit = false;
				} else {
					aux1.multiply(1.0 / (double) total, aux);

					if (!aux.equals(ctemp[i])) {
						aux.copy(ctemp[i]);
						exit = false;
					}
				}
			}
			if (exit)
				return ctemp;
		}
	}

	@Override
	public Column simulate(Column pattern) {
		Column y = new Column(nperlayer[2]);
		simulate(pattern, y);
		return y;
	}

	@Override
	public void simulate(Column pattern, Column result) {
		Column aux = new Column(nperlayer[1]);
		simulateNoChange(pattern, aux);
		super.simulate(aux, result);
	}

	/**
	 * Calculate the distance from the pattern to each centroid and apply the
	 * Gaussian function to that distance left the result in
	 * <code>result</code>.
	 *
	 * @param pattern Pattern to evaluate
	 * @param result  The matrix to put the result.
	 */
	private void simulateNoChange(Column pattern, Column result) {
		for (int i = 0; i < nperlayer[1]; i++) {
			double current = euclideanDistance2(pattern, centers[i]);
			result.position(i, 0, NeuralNetwork.gaussian(current, sigma[i]));
		}
	}
}

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

import libai.common.Shuffler;
import libai.common.matrix.Column;
import libai.common.Pair;
import libai.nn.NeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Kohonen's Self-organizative Maps or SOM or Kohonen. This maps are one of the
 * most important unsupervised neural networks of the history. The most
 * important feature of the kohonen's maps is the possibility of transform any
 * multidimensional space into a R^2 space, providing a highly precise
 * clustering method. One of the most famous examples for the kohonen's maps is
 * the transform the RGB color cube into a plane where the reds, greens, blues,
 * etc are clustered in a very similar way of the any color picker utility.
 *
 * @author kronenthaler
 */
public class Kohonen extends UnsupervisedLearning {
	private static final long serialVersionUID = 8918172607912802829L;

	private Column W[];                    //array of weights ijk, with k positions.
	private int[][] map;                //map of the outputs
	private int[] nperlayer;            //array of 3 positions, {#inputs,#rows,#columns}
	private double neighborhood;
	private int stepsx[], stepsy[];

	/**
	 * Constructor. Creates a kohonen's map with nperlayer[0] inputs,
	 * nperlayer[1] rows and nperlayer[2] columns. Additional set the initial
	 * size of the neighborhood and the way in the neighbors are connected.
	 *
	 * @param nperlayer    Number of neurons (input, rows and columns)
	 * @param neighborhood Initial size of the neighborhood
	 * @param neighboursX  neighbors along the X-axis
	 * @param neighboursY  neighbors along the Y-axis
	 */
	public Kohonen(int[] nperlayer, double neighborhood, int[] neighboursX, int[] neighboursY) {
		this(nperlayer, neighborhood, neighboursX, neighboursY, getDefaultRandomGenerator());
	}

	/**
	 * Constructor. Creates a kohonen's map with nperlayer[0] inputs,
	 * nperlayer[1] rows and nperlayer[2] columns. Additional set the initial
	 * size of the neighborhood and the way in the neighbors are connected.
	 *
	 * @param nperlayer    Number of neurons (input, rows and columns)
	 * @param neighborhood Initial size of the neighborhood
	 * @param neighboursX  neighbors along the X-axis as deltas from origin eg. +1, 0, -1, 2
	 * @param neighboursY  neighbors along the Y-axis as deltas from origin eg. +1, 0, -1, 2
	 * @param rand         Random generator used for creating matrices
	 */
	public Kohonen(int[] nperlayer, double neighborhood, int[] neighboursX, int[] neighboursY, Random rand) {
		super(rand);
		this.nperlayer = nperlayer;
		this.neighborhood = neighborhood;

		W = new Column[nperlayer[1] * nperlayer[2]];
		stepsx = neighboursX;
		stepsy = neighboursY;

		for (int i = 0; i < nperlayer[1]; i++) {
			for (int j = 0; j < nperlayer[2]; j++) {
				W[(i * nperlayer[2]) + j] = new Column(nperlayer[0]);
				W[(i * nperlayer[2]) + j].fill(true);
			}
		}
		map = new int[nperlayer[1]][nperlayer[2]];
		for (int i = 0; i < map.length; i++)
			Arrays.fill(map[i], -1);
	}

	/**
	 * Constructor. Creates a kohonen's map using the standard neighborhood (up,
	 * down, left, right). Alias of Kohonen(nperlayer, _neighborhood, new
	 * int[]{0,0,1,-1}, new int[]{-1,1,0,0});
	 *
	 * @param nperlayer    Number of neurons (input, rows and columns)
	 * @param neighborhood Initial size of the neighborhood
	 */
	public Kohonen(int[] nperlayer, double neighborhood) {
		this(nperlayer, neighborhood, new int[]{0, 0, 1, -1}, new int[]{-1, 1, 0, 0});
	}

	/**
	 * Constructor. Creates a kohonen's map using the standard neighborhood (up,
	 * down, left, right). Alias of Kohonen(nperlayer, _neighborhood, new
	 * int[]{0,0,1,-1}, new int[]{-1,1,0,0});
	 *
	 * @param nperlayer    Number of neurons (input, rows and columns)
	 * @param neighborhood Initial size of the neighborhood
	 * @param random       Random generator used for creating matrices
	 */
	public Kohonen(int[] nperlayer, double neighborhood, Random random) {
		this(nperlayer, neighborhood, new int[]{0, 0, 1, -1}, new int[]{-1, 1, 0, 0}, random);
	}

	/**
	 * Train the map. The answers are omitted for the training process but are
	 * necessary for the labeling of the map.
	 *
	 * @param patterns The patterns to be learned.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @param offset   The first pattern position
	 * @param length   How many patterns will be used.
	 */
	@Override
	public void train(Column[] patterns, double alpha, int epochs, int offset, int length) {
		validatePreconditions(patterns, epochs, offset, length);

		double lambda = neighborhood;
		double alpha1 = alpha;

		Shuffler shuffler = new Shuffler(length, NeuralNetwork.getDefaultRandomGenerator());
		initializeProgressBar(epochs);

		Column temp = new Column(nperlayer[0]);

		for (int currentEpoch = 0; currentEpoch < epochs; currentEpoch++) {
			//System.out.println("epoch: "+curr_epoch);
			//shuffle
			int[] sort = shuffler.shuffle();

			for (int k = 0; k < length; k++) {
				//Who is the winner
				Pair<Integer, Integer> winner = getWinnerCell(patterns[sort[k] + offset]);

				//Update winner and neighbors.
				for (int i = 0; i < nperlayer[1]; i++) {
					for (int j = 0; j < nperlayer[2]; j++) {
						Column Mij = getPrototypeAt(i, j);
						patterns[sort[k] + offset].subtract(Mij, temp);
						temp.multiply(alpha1 * neighbor(i, j, winner.first, winner.second), temp);
						Mij.add(temp, Mij);
					}
				}
			}

			//update neighborhood's ratio.
			if (neighborhood >= 0.5)
				neighborhood = lambda * Math.exp(-(float) currentEpoch / (float) epochs);

			//update alpha
			if (alpha1 > 0.001)
				alpha1 = alpha * Math.exp(-(float) currentEpoch / (float) epochs);

			if (progress != null)
				progress.setValue(currentEpoch);
		}

		if (progress != null)
			progress.setValue(progress.getMaximum());
	}

	@Override
	public Column simulate(Column pattern) {
		Column ret = new Column(nperlayer[0]);
		simulate(pattern, ret);
		return ret;
	}

	@Override
	public void simulate(Column pattern, Column result) {
		Pair<Integer, Integer> winner = getWinnerCell(pattern);
		getPrototypeAt(winner.first, winner.second).copy(result);
	}

	private Pair<Integer, Integer> getWinnerCell(Column pattern) {
		Pair<Integer, Integer> winner = new Pair<>(0, 0);
		double min = Double.MAX_VALUE;
		for (int i = 0; i < nperlayer[1]; i++) {
			for (int j = 0; j < nperlayer[2]; j++) {
				double temp = euclideanDistance2(pattern, getPrototypeAt(i, j));
				if (temp < min) {
					min = temp;
					winner.first = i;
					winner.second = j;
				}
			}
		}
		return winner;
	}

	public Column getPrototypeAt(int i, int j) {
		return W[(i * nperlayer[2]) + j];
	}

	private double neighbor(int i, int j, int ig, int jg) {
		return gaussian(distance(i, j, ig, jg), neighborhood * neighborhood);
	}

	private double distance(int i, int j, int ig, int jg) {
		return (((i - ig) * (i - ig)) + ((j - jg) * (j - jg)));
	}

	/**
	 * @return The label map.
	 */
	public int[][] getMap() {
		return map;
	}

	/**
	 * Label the output for the patterns and expand the results through the
	 * neighbors until the map is completely fill. NOTE: The expansion isn't an
	 * standard process but is very helpful to avoid unknown answers.
	 *
	 * @param patterns The patterns to label
	 * @param answers  The expected answer for the patterns
	 * @param offset   The initial pattern position
	 * @param length   How many patterns to label.
	 */
	public void expandMap(Column[] patterns, Column[] answers, int offset, int length) {
		//System.out.println("labelling...");
		for (int k = 0; k < length; k++) {
			Pair<Integer, Integer> winner = getWinnerCell(patterns[k + offset]);
			//simulate(patterns[k + offset], winner);

			int i = winner.first;
			int j = winner.second;

			if (map[i][j] == -1) //no overlapping
				map[i][j] = (int) answers[k + offset].position(0, 0); //must have just one position and should be an integer
		}

		ArrayList<Pair<Integer, Integer>> q = new ArrayList<>();

		for (int i = 0; i < nperlayer[1]; i++)
			for (int j = 0; j < nperlayer[2]; j++)
				if (map[i][j] != -1)
					q.add(new Pair<>(i, j));

		//System.out.println("BFS...");
		while (!q.isEmpty()) {
			Pair<Integer, Integer> current = q.remove(0);
			int c = map[current.first][current.second];

			for (int k = 0; k < stepsx.length; k++) {
				int i = current.first + stepsx[k];
				int j = current.second + stepsy[k];
				if (i >= 0 && i < nperlayer[1] && j >= 0 && j < nperlayer[2] && map[i][j] == -1) {
					q.add(new Pair<>(i, j));
					map[i][j] = c;
				}
			}
		}
	}
}

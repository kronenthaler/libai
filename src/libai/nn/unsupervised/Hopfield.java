package libai.nn.unsupervised;

import libai.nn.NeuralNetwork;
import java.io.*;
import java.util.*;

import libai.common.Matrix;
import libai.common.SymmetricSign;

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
	protected Matrix W;
	protected static SymmetricSign ssign = new SymmetricSign();
	/**
	 * Constructor. Receives the number of input to the network.
	 *
	 * @param inputs	The number of input to the network.
	 */
	public Hopfield(int inputs) {
		W = new Matrix(inputs, inputs);
	}

	/**
	 * Train the network. The answers, alpha, epochs and minerror are meaningless
	 * in this algorithm.
	 *
	 * @param patterns	The patterns to be learned.
	 * @param answers	The expected answers. [useless]
	 * @param alpha	The learning rate. [useless]
	 * @param epochs	The maximum number of iterations [useless]
	 * @param offset	The first pattern position
	 * @param length	How many patterns will be used.
	 * @param minerror The minimal error expected.	[useless]
	 */
	@Override
	public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		Matrix I = new Matrix(W.getRows(), W.getColumns(), true);
		Matrix patternT = new Matrix(patterns[0].getColumns(), patterns[0].getRows());
		Matrix temp = new Matrix(W.getRows(), W.getColumns());

		if (progress != null) {
			progress.setMaximum(length - 1);
			progress.setMinimum(0);
			progress.setValue(0);
		}

		for (int i = 0; i < length; i++) {
			patterns[i + offset].apply(ssign, patterns[i + offset]);
			Matrix pattern = patterns[i + offset];

			//p^t.p
			pattern.transpose(patternT);
			pattern.multiply(patternT, temp);

			temp.subtract(I, temp);

			W.add(temp, W);

			if (progress != null)
				progress.setValue(i);
		}
	}

	@Override
	public Matrix simulate(Matrix pattern) {
		pattern.apply(ssign, pattern);
		Matrix result = new Matrix(pattern.getRows(), pattern.getColumns());
		simulate(pattern, result);
		return result;
	}

	@Override
	public void simulate(Matrix pattern, Matrix result) {
		for (int col = 0; col < pattern.getRows(); col++) {
			Matrix column = new Matrix(pattern.getRows(), pattern.getColumns(), W.getCol(col));
			//hacer el producto punto entre esta fila

			double dotProduct = pattern.dotProduct(column);
			result.position(col, 0, dotProduct > 0 ? 1 : -1);
		}
	}

	public static Hopfield open(String path) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
			Hopfield p = (Hopfield) in.readObject();
			in.close();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

package libai.nn.supervised.backpropagation;

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.functions.Function;
import libai.nn.NeuralNetwork;


/**
 * Created by kronenthaler on 08/01/2017.
 */
public interface Backpropagation {
	/**
	 * Initializes the Backpropagation algorithm with the basic structures needed. This method usually should be called in the
	 * NeuralNetwork's train method right before calling the Backpropagation's train method.
	 */
	void initialize(NeuralNetwork nn, int[] nperlayer, Function[] functions, Matrix[]W, Column[]Y, Column[]b, Column[]u);

	/**
	 * Trains a neural network using the backpropagation implementaion.
	 */
	void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror);
}

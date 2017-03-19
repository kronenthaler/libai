package libai.nn.unsupervised;

import libai.common.Precondition;
import libai.common.matrix.Column;
import libai.nn.NeuralNetwork;

import java.util.Random;

/**
 * Created by kronenthaler on 19/03/2017.
 */
public abstract class UnsupervisedLearning extends NeuralNetwork {
	public UnsupervisedLearning() {
		super();
	}

	public UnsupervisedLearning(Random rand) {
		super(rand);
	}

	/**
	 * Trains this neural network with the list of {@code patterns} and the
	 * expected {@code answers}.
	 * <p>Use the learning rate {@code alpha} for many {@code epochs}.
	 * Take {@code length} patterns from the position {@code offset}.</p>
	 * <p>{@code patterns} must be array of
	 * non-{@code null} <b>column</b> matrices</p>
	 *
	 * @param patterns The patterns to be learned.
	 * @param alpha    The learning rate.
	 * @param epochs   The maximum number of iterations
	 * @param offset   The first pattern position
	 * @param length   How many patterns will be used.
	 */
	public abstract void train(Column[] patterns, double alpha, int epochs, int offset, int length);

	@Override
	public void train(Column[] patterns, Column[] answers, double alpha, int epochs, int offset, int length, double minerror) {
		train(patterns, alpha, epochs, offset, length);
	}

	protected void validatePreconditions(Column[] patterns, int epochs, int offset, int length) {
		Precondition.check(offset >= 0 && offset < patterns.length, "offset must be in the interval [0, %d), found,  %d", patterns.length, offset);
		Precondition.check(length >= 0 && length <= patterns.length - offset, "length must be in the interval (0, %d], found,  %d", patterns.length - offset, length);
		Precondition.check(epochs > 0, "The number of epochs must be a positive non zero integer");
	}
}

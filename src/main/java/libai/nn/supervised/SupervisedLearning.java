package libai.nn.supervised;

import libai.common.Precondition;
import libai.common.matrix.Column;
import libai.nn.NeuralNetwork;

import java.util.Random;

/**
 * Created by kronenthaler on 19/03/2017.
 */
public abstract class SupervisedLearning extends NeuralNetwork {
	public SupervisedLearning(Random rand) {
		super(rand);
	}

	protected void validatePreconditions(Column[] patterns, Column[] answers, int epochs, int offset, int length, double minerror) {
		Precondition.check(patterns.length == answers.length, "There must be the same amount of patterns and answers");
		Precondition.check(offset >= 0 && offset < patterns.length, "offset must be in the interval [0, %d), found,  %d", patterns.length, offset);
		Precondition.check(length >= 0 && length <= patterns.length - offset, "length must be in the interval (0, %d], found,  %d", patterns.length - offset, length);
		Precondition.check(epochs > 0, "The number of epochs must be a positive non zero integer");
		Precondition.check(minerror >= 0, "The error must be a positive number");
	}
}
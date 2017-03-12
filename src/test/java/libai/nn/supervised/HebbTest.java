package libai.nn.supervised;

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.nn.NeuralNetwork;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by kronenthaler on 29/01/2017.
 */
public class HebbTest {
	@Test
	public void testTrain(){
		NeuralNetwork nn = new Hebb(25, 25, 0.05);
		Column[] patterns = new Column[]{
				new Column(25, new double[]{
						-1, 1, 1, 1,-1,
						1,-1,-1,-1, 1,
						1,-1,-1,-1, 1,
						1,-1,-1,-1, 1,
						-1, 1, 1, 1,-1}),
				new Column(25, new double[]{
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						-1,-1,+1,+1,+1,
						-1,-1,+1,+1,+1,}),
		};

		Column[] answers = new Column[]{
				new Column(25, new double[]{
						-1, 1, 1, 1,-1,
						1,-1,-1, 1, 1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1
				}),
				new Column(25, new double[]{
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1,}),
		};

		nn.train(patterns, patterns, 0.005, 1000, 0, patterns.length);
		assertTrue(nn.error(answers, patterns, 0, patterns.length) < 1.e-5);
	}

	@Test
	public void testClassifier(){
		// same input as before, just each element is given a different answer vector.
		NeuralNetwork nn = new Hebb(25, 1, 0.05);
		Column[] patterns = new Column[]{
				new Column(25, new double[]{
						-1, 1, 1, 1,-1,
						1,-1,-1,-1, 1,
						1,-1,-1,-1, 1,
						1,-1,-1,-1, 1,
						-1, 1, 1, 1,-1}),
				new Column(25, new double[]{
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						-1,-1,+1,+1,+1,
						-1,-1,+1,+1,+1,}),
		};

		Column[] answers = new Column[]{
				new Column(1, new double[]{1}),
				new Column(1, new double[]{-1}),
		};

		nn.train(patterns, answers, 0.005, 1000, 0, patterns.length);

		Column[] patterns2 = new Column[]{
				new Column(25, new double[]{
						-1, 1, 1, 1,-1,
						1,-1,-1, 1, 1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1
				}),
				new Column(25, new double[]{
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						+1,+1,+1,-1,-1,
						-1,-1,-1,-1,-1,
						-1,-1,-1,-1,-1,}),
		};
		assertTrue(nn.error(patterns2, answers, 0, patterns2.length) < 1.e-5);
	}
}

/*
 * MIT License
 *
 * Copyright (c) 2017 Federico Vera <https://github.com/dktcoding>
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

import demos.common.SimpleProgressDisplay;
import libai.common.kernels.GaussianKernel;
import libai.common.kernels.PolynomialKernel;
import libai.common.kernels.SigmoidalKernel;
import libai.common.matrix.Column;
import libai.common.MatrixIOTest;
import libai.common.kernels.LinearKernel;
import libai.nn.NeuralNetwork;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class SVMTest {
	@Test
	public void testDemo() {
		int n = 100;
		int t = 40;

		Column[] patterns = new Column[n + t];
		Column[] answers = new Column[n + t];

		Random r = new Random(0);
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
			answers[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
			answers[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		SVM net = new SVM(new LinearKernel(), new Random(0));
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(patterns, answers, 0.001, 10000, 0, n);

		assumeTrue("SVM didn't converge, try again", 0.001 > net.error(patterns, answers, n, t));

		for (int i = n; i < patterns.length; i++) {
			assertEquals(answers[i].position(0, 0), net.simulate(patterns[i]).position(0, 0), 1e-12);
		}
	}

	@Test
	public void testTraining() throws Exception {
		File resourcesDirectory = new File("src/test/resources/tic-tac-toe");
		String data = new Scanner(resourcesDirectory, "UTF8").useDelimiter("\\Z").next();

		String[] lines = data.split("\n");

		Column[] patterns = new Column[lines.length];
		Column[] answers = new Column[lines.length];

		for (int i = 0; i < lines.length; i++) {
			String[] tokens = lines[i].split(" ");
			patterns[i] = new Column(tokens.length - 1);
			answers[i] = new Column(1);

			for (int j = 0; j < tokens.length - 1; j++) {
				patterns[i].position(j, 0, Double.parseDouble(tokens[j]));
			}
			answers[i].position(0, 0, Double.parseDouble(tokens[tokens.length - 1]));
		}

		SVM net = new SVM(new GaussianKernel(1), new Random(0));
		net.setTrainingParam(SVM.PARAM_TOLERANCE, 0.1);
		net.setTrainingParam(SVM.PARAM_C, 0.5);
		net.train(patterns, answers, 0, 10000000, 0, patterns.length);

		assertTrue(net.error(patterns, answers) == 0.008350730688935281);
	}

	@Test
	public void testPolynomialKernel() throws Exception {
		int n = 100;
		int t = 40;

		Column[] patterns = new Column[n + t];
		Column[] answers = new Column[n + t];

		Random r = new Random(0);
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
			answers[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
			answers[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		SVM net = new SVM(new PolynomialKernel(1, 0, 2), new Random(0));
		net.setTrainingParam(SVM.PARAM_TOLERANCE, 0.01);
		net.setTrainingParam(SVM.PARAM_C, 0.05);
		net.setTrainingParam(SVM.PARAM_EPSILON, 0.0001);

		net.train(patterns, answers, 0, 10000, 0, n);
		assertTrue(net.error(patterns, answers, n, t) < 0.1);
	}

	@Test
	public void testSigmoidalKernel() throws Exception {
		int n = 100;
		int t = 40;

		Column[] patterns = new Column[n + t];
		Column[] answers = new Column[n + t];

		Random r = new Random(0);
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1, Math.tanh(i + 1) + inc});
			answers[i] = new Column(1, new double[]{inc > 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1.33, Math.tanh(i + 1.33) + inc});
			answers[i] = new Column(1, new double[]{inc > 0 ? +1 : -1});
		}

		SVM net = new SVM(new SigmoidalKernel(8, 0), new Random(0));
		net.setTrainingParam(SVM.PARAM_TOLERANCE, 0.001);
		net.setTrainingParam(SVM.PARAM_C, 0.005);
		net.setTrainingParam(SVM.PARAM_EPSILON, 0.0001);

		net.train(patterns, answers, 0, 10000, 0, n);
		assertTrue(net.error(patterns, answers, n, t) < 0.15);
	}

	@Test
	public void testSaveOpen() {
		int n = 100;
		int t = 40;

		Column[] patterns = new Column[n + t];
		Column[] ans = new Column[n + t];

		Random r = new Random();
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Column(2, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Column(1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		NeuralNetwork net = new SVM(new LinearKernel());
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(patterns, ans, 0.001, 1000, 0, n);

		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());

		String tmp = System.getProperty("java.io.tmpdir") + File.separator;
		tmp = tmp + "foo.svm";
		assertTrue(net.save(tmp));
		try {
			SVM net2 = SVM.open(tmp);
			assertNotNull(net2);
			new File(tmp).delete();

			assertEquals(net.error(patterns, ans), net2.error(patterns, ans), 0);
			for (int i = n; i < patterns.length; i++) {
				assertEquals(net.simulate(patterns[i]).position(0, 0), net2.simulate(patterns[i]).position(0, 0), 0);
			}
		} catch (IOException e) {
			fail();
		} catch (ClassNotFoundException e1) {
			fail();
		}

	}

	@Test(expected = NullPointerException.class)
	public void testNullPath() throws IOException, ClassNotFoundException {
		SVM.open((String) null);
	}
}

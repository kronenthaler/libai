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
import libai.common.Matrix;
import libai.common.MatrixIOTest;
import libai.common.kernels.LinearKernel;
import libai.nn.NeuralNetwork;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class SVMTest {
	@Test
	public void testDemo() {
		int n = 100;
		int t = 40;

		Matrix[] patterns = new Matrix[n + t];
		Matrix[] ans = new Matrix[n + t];

		Random r = new Random(0);
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Matrix(2, 1, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Matrix(2, 1, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		NeuralNetwork net = new SVM(new LinearKernel(), new Random(0));
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(patterns, ans, 0.001, 10000, 0, n);

		assumeTrue("SVM didn't converge, try again", 0.001 > net.error(patterns, ans));

		for (int i = n; i < patterns.length; i++) {
			assertEquals(ans[i].position(0, 0), net.simulate(patterns[i]).position(0, 0), 1e-12);
		}
	}

	@Test
	public void testSaveOpen() {
		int n = 100;
		int t = 40;

		Matrix[] patterns = new Matrix[n + t];
		Matrix[] ans = new Matrix[n + t];

		Random r = new Random();
		for (int i = 0; i < n; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Matrix(2, 1, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
		}

		for (int i = n; i < n + t; i++) {
			int inc = r.nextInt(10);
			patterns[i] = new Matrix(2, 1, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
			ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
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
		} catch(IOException e) {
			fail();
		} catch(ClassNotFoundException e1) {
			fail();
		}

	}

	@Test(expected=NullPointerException.class)
	public void testNullPath() throws IOException, ClassNotFoundException{
		SVM.open((String)null);
	}
}

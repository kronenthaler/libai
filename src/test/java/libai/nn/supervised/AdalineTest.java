/*
 * MIT License
 *
 * Copyright (c) 2016 Federico Vera <https://github.com/dktcoding>
 *
 * Permission is hereby granted, free of charge, to any person obtaining ada copy
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
import libai.common.MatrixIOTest;
import libai.nn.NeuralNetwork;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.round;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class AdalineTest {

	@Test
	public void testTrainOr() {
		Adaline ada = new Adaline(2, 1, new Random(0));
		Column[] ins = new Column[4];
		ins[0] = new Column(2, new double[]{0, 0});
		ins[1] = new Column(2, new double[]{0, 1});
		ins[2] = new Column(2, new double[]{1, 0});
		ins[3] = new Column(2, new double[]{1, 1});
		Column[] out = new Column[4];
		out[0] = new Column(1, new double[]{0});
		out[1] = new Column(1, new double[]{1});
		out[2] = new Column(1, new double[]{1});
		out[3] = new Column(1, new double[]{1});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
		Column res = new Column(1);
		ada.simulate(ins[0], res);
		assertEquals(0, round(res.position(0, 0)));
		ada.simulate(ins[1], res);
		assertEquals(1, round(res.position(0, 0)));
		ada.simulate(ins[2], res);
		assertEquals(1, round(res.position(0, 0)));
		ada.simulate(ins[3], res);
		assertEquals(1, round(res.position(0, 0)));
	}

	@Test
	public void testTrainAnd() {
		// Trains an Or and tests simulate(Matrix, Matrix)
		Adaline ada = new Adaline(2, 1, new Random(0));
		Column[] ins = new Column[4];
		ins[0] = new Column(2, new double[]{0, 0});
		ins[1] = new Column(2, new double[]{0, 1});
		ins[2] = new Column(2, new double[]{1, 0});
		ins[3] = new Column(2, new double[]{1, 1});
		Column[] out = new Column[4];
		out[0] = new Column(1, new double[]{0});
		out[1] = new Column(1, new double[]{0});
		out[2] = new Column(1, new double[]{0});
		out[3] = new Column(1, new double[]{1});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
	}

	@Test
	public void testIO() {
		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		Adaline ada = new Adaline(2, 1, new Random(0));
		Column[] ins = new Column[4];
		ins[0] = new Column(2, new double[]{0, 0});
		ins[1] = new Column(2, new double[]{0, 1});
		ins[2] = new Column(2, new double[]{1, 0});
		ins[3] = new Column(2, new double[]{1, 1});
		Column[] out = new Column[4];
		out[0] = new Column(1, new double[]{1});
		out[1] = new Column(1, new double[]{1});
		out[2] = new Column(1, new double[]{1});
		out[3] = new Column(1, new double[]{0});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
		assertEquals(1, round(ada.simulate(ins[0]).position(0, 0)));
		assertEquals(1, round(ada.simulate(ins[1]).position(0, 0)));
		assertEquals(1, round(ada.simulate(ins[2]).position(0, 0)));
		assertEquals(0, round(ada.simulate(ins[3]).position(0, 0)));

		String foo = System.getProperty("java.io.tmpdir")
				+ File.separator + "adaline.tmp";
		new File(foo).deleteOnExit();

		assertTrue(ada.save(foo));

		try {
			Adaline a2 = Adaline.open(foo);
			assertNotNull(a2);
			assertNotEquals(ada, a2);

			assertEquals(ada.simulate(ins[0]), a2.simulate(ins[0]));
			assertEquals(ada.simulate(ins[1]), a2.simulate(ins[1]));
			assertEquals(ada.simulate(ins[2]), a2.simulate(ins[2]));
			assertEquals(ada.simulate(ins[3]), a2.simulate(ins[3]));
		} catch (IOException e) {
			fail();
		} catch (ClassNotFoundException e1) {
			fail();
		}
	}

	@Test
	public void testDemo() {
		int n = 40;
		int t = 10;

		Column[] patterns = new Column[n + t];
		Column[] ans = new Column[n + t];

		for (int i = 0; i < n; i++) {
			patterns[i] = new Column(1, new double[]{i + 1});
			ans[i] = new Column(1, new double[]{(2 * (i + 1)) + 3});
		}

		for (int i = n; i < n + t; i++) {
			patterns[i] = new Column(1, new double[]{i + 1.33});
			ans[i] = new Column(1, new double[]{(2 * (i + 1.33)) + 3});
		}

		NeuralNetwork net = new Adaline(1, 1);
		net.train(patterns, ans, 0.001, 1000, 0, n);

		assertTrue(1e-5 > net.error(patterns, ans, 0, n));
		assertTrue(1e-3 > net.error(patterns, ans, n, t));
		for (int i = n; i < patterns.length; i++) {
			double res = net.simulate(patterns[i]).position(0, 0);
			assertEquals(ans[i].position(0, 0), res, 1e-2);
		}
	}

}

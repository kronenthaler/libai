/*
 * MIT License
 *
 * Copyright (c) 2017 Federico Vera <https://github.com/dktcoding>
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

import demos.common.SimpleProgressDisplay;
import java.io.File;
import javax.swing.JProgressBar;
import libai.common.Matrix;
import libai.common.MatrixIOTest;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class RBFTest {
	@Test
	public void testDemo() {
		int n = 40;
		int m = 1;
		int l = 1;
		int test = 12;
		Matrix[] p = new Matrix[n + test];
		Matrix[] t = new Matrix[n + test];
		double delta = 0.1;
		double x = 0;
		for (int i = 0; i < n; i++, x += delta) {
			p[i] = new Matrix(m, 1);
			t[i] = new Matrix(l, 1);

			p[i].position(0, 0, x);
			t[i].position(0, 0, f(x));
		}

		delta = 0.33;
		x = 0;
		for (int i = n; i < n + test && x < 4; i++, x += delta) {
			p[i] = new Matrix(m, 1);
			t[i] = new Matrix(l, 1);

			p[i].position(0, 0, x);
			t[i].position(0, 0, f(x));
		}

		int nperlayer[] = {m, 10, l};
		RBF net = new RBF(nperlayer);
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(p, t, 0.001, 600000, 0, n);

		assumeTrue("RBF didn't converge, try again", 0.0001 > net.error(p, t));

		for (int i = n; i < p.length; i++) {
			assertEquals(t[i].position(0, 0),net.simulate(p[i]).position(0, 0), 0.1);
		}
	}

	@Test
	public void testSaveOpen() {
		int n = 40;
		int m = 1;
		int l = 1;
		int test = 12;
		Matrix[] p = new Matrix[n + test];
		Matrix[] t = new Matrix[n + test];
		double delta = 0.1;
		double x = 0;
		for (int i = 0; i < n; i++, x += delta) {
			p[i] = new Matrix(m, 1);
			t[i] = new Matrix(l, 1);

			p[i].position(0, 0, x);
			t[i].position(0, 0, f(x));
		}

		delta = 0.33;
		x = 0;
		for (int i = n; i < n + test && x < 4; i++, x += delta) {
			p[i] = new Matrix(m, 1);
			t[i] = new Matrix(l, 1);

			p[i].position(0, 0, x);
			t[i].position(0, 0, f(x));
		}

		int nperlayer[] = {m, 10, l};
		RBF net = new RBF(nperlayer);
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(p, t, 0.001, 600000, 0, n);

		assumeTrue("RBF didn't converge, try again", 0.0001 > net.error(p, t));

		for (int i = n; i < p.length; i++) {
			assertEquals(t[i].position(0, 0),net.simulate(p[i]).position(0, 0), 0.1);
		}

		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());

		String tmp = System.getProperty("java.io.tmpdir") + File.separator;
		tmp = tmp + "foo.rbf";
		assertTrue(net.save(tmp));
		RBF net2 = RBF.open(tmp);
		assertNotNull(net2);
		new File(tmp).delete();

		assertEquals(net.error(p, t), net2.error(p, t), 0);
		for (int i = n; i < p.length; i++) {
			assertEquals(net.simulate(p[i]).position(0, 0), net2.simulate(p[i]).position(0, 0), 0);
		}

	}

	@Test
	public void testNullPath(){
		RBF rbf = new RBF();
		assertNull(RBF.open(null));
	}

	static double f(double x) {
		return Math.sin(x) + Math.cos(x);
	}
}

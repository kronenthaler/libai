/*
 * MIT License
 *
 * Copyright (c) 2017 Ignacio Calderon <https://github.com/kronenthaler>
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
package libai.nn.unsupervised;

import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.common.ProgressDisplay;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kronenthaler on 31/01/2017.
 */
public class KohonenTest {
	private static final ProgressDisplay progress = new ProgressDisplay() {
		int value, min, max;
		@Override
		public void setMinimum(int v) {
			min = v;
		}

		@Override
		public void setMaximum(int v) {
			max = v;
		}

		@Override
		public void setValue(int v) {
			value=v;
			assertTrue(v >= min);
			assertTrue(v <= max);
		}

		@Override
		public int getMaximum() {
			return max;
		}

		@Override
		public int getMinimum() {
			return min;
		}

		@Override
		public int getValue() {
			return value;
		}
	};

	@Test
	public void testDemo(){
		Column[] p = new Column[100];
		Column[] c = new Column[100];
		Column[] test = new Column[20];
		Column[] ctest = new Column[20];
		Random r = new Random(0);

		for(int i=0;i<p.length;i++){
			p[i] = new Column(2);
			c[i] = new Column(1, new double[]{ i/(int)(p.length/2) });
			p[i].fill(true, r);
			p[i].add(new Column(2, new double[]{i > p.length/2 ? 10 : -10, i > p.length/2 ? 10 : -10}), p[i]);
		}

		for(int i=0;i<test.length;i++){
			test[i] = new Column(2);
			ctest[i] = new Column(2, new double[]{i > p.length/2 ? 10 : -10, i > p.length/2 ? 10 : -10});
			test[i].fill(true, r);
			test[i].add(new Column(2, new double[]{i > p.length/2 ? 10 : -10, i > p.length/2 ? 10 : -10}), test[i]);
		}

		int nperlayer[] = {2, 20, 20};
		final Kohonen net = new Kohonen(nperlayer, 10, r);
		net.setProgressBar(progress);
		net.train(p, c, 1, 1000, 0, p.length);

		// the winning cell should not be too far away from the ideal (-10, 10) or (10, 10) pattern.
		assertTrue(net.error(test, ctest, 0, test.length) < 0.7);
		assertEquals(net.getProgressBar().getValue(), net.getProgressBar().getMaximum());
	}
}

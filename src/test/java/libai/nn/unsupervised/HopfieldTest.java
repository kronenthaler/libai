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
import libai.common.ProgressDisplay;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kronenthaler on 05/03/2017.
 */
public class HopfieldTest {
	private static final ProgressDisplay progress = new ProgressDisplay() {
		int value, min, max;

		@Override
		public int getMaximum() {
			return max;
		}

		@Override
		public void setMaximum(int v) {
			max = v;
		}

		@Override
		public int getMinimum() {
			return min;
		}

		@Override
		public void setMinimum(int v) {
			min = v;
		}

		@Override
		public int getValue() {
			return value;
		}

		@Override
		public void setValue(int v) {
			value = v;
			assertTrue(v >= min);
			assertTrue(v <= max);
		}
	};

	@Test
	public void testDemo() {
		Column[] patterns = new Column[]{
				new Column(25, new double[]{
						-1, -1, +1, -1, -1,
						-1, -1, +1, -1, -1,
						+1, +1, +1, +1, +1,
						-1, -1, +1, -1, -1,
						-1, -1, +1, -1, -1,
				}),
				new Column(25, new double[]{
						+1, -1, -1, -1, +1,
						-1, +1, -1, +1, -1,
						-1, -1, +1, -1, -1,
						-1, +1, -1, +1, -1,
						+1, -1, -1, -1, +1,
				}),
		};

		Column[] answers = new Column[]{
				new Column(25, new double[]{
						-1, -1, +1, -1, -1,
						-1, -1, +1, -1, -1,
						+1, +1, +1, +1, +1,
						-1, -1, -1, -1, -1,
						-1, -1, -1, -1, -1,
				}),
				new Column(25, new double[]{
						+1, -1, -1, -1, +1,
						-1, +1, -1, +1, -1,
						-1, -1, +1, -1, -1,
						-1, -1, -1, -1, -1,
						-1, -1, -1, -1, +1,
				}),
		};

		final Hopfield net = new Hopfield(25);
		net.setProgressBar(progress);
		net.train(patterns, null, 0, 1, 0, patterns.length);

		assertTrue(net.error(answers, patterns, 0, patterns.length) < 1.e-5);
		assertEquals(net.simulate(answers[0]), patterns[0]);
		assertEquals(net.simulate(answers[1]), patterns[1]);

		assertEquals(net.getProgressBar().getValue(), net.getProgressBar().getMaximum());
	}
}


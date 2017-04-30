/*
 * MIT License
 *
 * Copyright (c) 2016 Federico Vera <https://github.com/dktcoding>
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
package libai.common.functions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class SincTest {
	@Test
	public void testEval() {
		Sinc sinc = new Sinc();
		assertEquals(1, sinc.eval(0), 0d);
		assertEquals(0, sinc.eval(Math.PI), 1e-12);
		assertEquals(0, sinc.eval(-Math.PI), 1e-12);
		assertEquals(0, sinc.eval(-2 * Math.PI), 1e-12);
		assertEquals(0, sinc.eval(2 * Math.PI), 1e-12);

		double x = Math.random();
		assertEquals(Math.sin(x) / x, sinc.eval(x), 1e-12);
	}

	@Test
	public void testEval2() {
		Sinc sinc = new Sinc();
		double accum = 0;
		for (int i = 1; i < 1000000; i++) {
			accum += sinc.eval(i);
		}
		assertEquals(accum, (Math.PI - 1) / 2, 1e-6);
	}

	@Test
	public void testEval3() {
		Sinc sinc = new Sinc();
		double accum = 0;
		for (int i = 1; i < 1000000; i++) {
			double res = sinc.eval(i);
			accum += res * res;
		}
		assertEquals(accum, (Math.PI - 1) / 2, 1e-6);
	}

	@Test
	public void testGetDerivate() {
		Function der = new Sinc().getDerivate();
		assertNotNull(der);
		assertEquals(0, der.eval(0), 0);
		double x = Math.random();
		assertEquals((x * Math.cos(x) - Math.sin(x)) / x / x, der.eval(x), 1e-12);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetGetDerivative() {
		new Sinc().getDerivate().getDerivate();
	}

}

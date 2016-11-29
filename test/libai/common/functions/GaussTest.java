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

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class GaussTest {

	@Test
	public void testEval() {
		Gauss gauss = new Gauss();
		assertEquals(1, gauss.eval(0), 1e-12);
		for (int i = 0; i < 100; i++) {
			double x = Math.random();
			assertEquals(gauss.eval(x), gauss.eval(-x), 1e-12);
			assertEquals(-(x*x), Math.log(gauss.eval(x)), 1e-12);
		}
	}

	@Test
	public void testGetDerivate() {
		Gauss gauss = new Gauss();
		Function der = gauss.getDerivate();
		assertNotNull(der);
		for (int i = 0; i < 100; i++) {
			double x = Math.random();
			assertEquals(-der.eval(x), der.eval(-x), 1e-12);
			assertEquals(-2 * x, der.eval(x) / gauss.eval(x), 1e-12);
		}
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testGetGetDerivative() {
		new Gauss().getDerivate().getDerivate();
	}
	
}

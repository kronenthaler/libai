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
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class ArcTangentTest {
	@Test
	public void testEval() {
		ArcTangent atan = new ArcTangent();
		double x = Math.random();
		assertEquals(Math.atan(x), atan.eval(x), 1e-12);
		x = Math.random();
		assertEquals(x, atan.eval(Math.tan(x)), 1e-12);
		x = Math.random();
		assertEquals(atan.eval(-x), -atan.eval(x), 1e-12);
		x = Math.random();
		assertEquals(atan.eval(1/x), Math.PI / 2 -atan.eval(x), 1e-12);
	}
	@Test
	public void testEval2() {
		ArcTangent atan = new ArcTangent();
		double x = Math.random();
		double accum = 0;
		for (int i = 0; i < 100000; i ++) {
			accum += (Math.pow(x, 2*i+1) * Math.pow(-1, i))/(2*i+1);
		}
		assertEquals(accum, atan.eval(x), 1e-6);
	}

	@Test
	public void testGetDerivate() {
		Function der = new ArcTangent().getDerivate();
		assertNotNull(der);
		double x = Math.random();
		assertEquals(der.eval(x), der.eval(-x), 1e-12);
		x = Math.random();
		
		double accum = 0;
		for (int i = 0; i < 10000; i++) {
			accum += Math.pow(-1, i) * Math.pow(x, 2*i);			
		}
		
		assertEquals(accum, der.eval(x), 1e-6);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testGetGetDerivative() {
		new ArcTangent().getDerivate().getDerivate();
	}
}

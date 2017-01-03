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
package libai.common.functions;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class IdentityTest {
	
	@Test
	public void testEval() {
		Identity eye = new Identity();
		for (int i = 0; i < 10; i++) {
			double x = Math.random() * 10  - 5;
			assertEquals(x, eye.eval(x), 0);
		}
	}

	@Test
	public void testGetDerivative() {
		Function der = new Identity().getDerivate();
		assertNotNull(der);
		for (int i = 0; i < 10; i++) {
			assertEquals(1, der.eval(Math.random() * 10  - 5), 0);
		}
	}

	@Test
	public void testGetGetDerivative() {
		assertNull(new Identity().getDerivate().getDerivate());
	}
}

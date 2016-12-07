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

import java.io.File;
import libai.common.Matrix;
import libai.common.MatrixIOTest;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static java.lang.Math.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class AdalineTest {
	
	@Test
	public void testTrainOr() {
		Adaline ada = new Adaline(2, 1);
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{0});
		out[1] = new Matrix(1, 1, new double[]{1});
		out[2] = new Matrix(1, 1, new double[]{1});
		out[3] = new Matrix(1, 1, new double[]{1});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
		Matrix res = new Matrix(1, 1);
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
		Adaline ada = new Adaline(2, 1);
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{0});
		out[1] = new Matrix(1, 1, new double[]{0});
		out[2] = new Matrix(1, 1, new double[]{0});
		out[3] = new Matrix(1, 1, new double[]{1});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
	}
	
	@Test
	public void testIO() {
        assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		Adaline ada = new Adaline(2, 1);
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{1});
		out[1] = new Matrix(1, 1, new double[]{1});
		out[2] = new Matrix(1, 1, new double[]{1});
		out[3] = new Matrix(1, 1, new double[]{0});
		ada.train(ins, out, 0.1, 1000);
		assertTrue(0.1 > ada.error(ins, out));
		assertEquals(1, round(ada.simulate(ins[0]).position(0, 0)));
		assertEquals(1, round(ada.simulate(ins[1]).position(0, 0)));
		assertEquals(1, round(ada.simulate(ins[2]).position(0, 0)));
		assertEquals(0, round(ada.simulate(ins[3]).position(0, 0)));
		
		String foo = System.getProperty("java.io.tmpdir")
				   + File.separator + "perceptron.tmp";
		new File(foo).deleteOnExit();
		
		assertTrue(ada.save(foo));
		Adaline a2 = Adaline.open(foo);
		assertNotNull(a2);
		assertTrue(ada != a2);
		
		assertEquals(ada.simulate(ins[0]), a2.simulate(ins[0]));
		assertEquals(ada.simulate(ins[1]), a2.simulate(ins[1]));
		assertEquals(ada.simulate(ins[2]), a2.simulate(ins[2]));
		assertEquals(ada.simulate(ins[3]), a2.simulate(ins[3]));
	}

}
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
package libai.nn.supervised;

import libai.common.matrix.Column;
import libai.common.MatrixIOTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class PerceptronTest {

	@Test
	public void testTrainOr() {
		Perceptron p = new Perceptron(2, 1, new Random(0));
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
		p.train(ins, out, 0.1, 1000);
		assertEquals(0, p.error(ins, out), 0);
		Column res = new Column(1);
		p.simulate(ins[0], res);
		assertEquals(out[0], res);
		p.simulate(ins[1], res);
		assertEquals(out[1], res);
		p.simulate(ins[2], res);
		assertEquals(out[2], res);
		p.simulate(ins[3], res);
		assertEquals(out[3], res);
	}

	@Test
	public void testTrainAnd() {
		Perceptron p = new Perceptron(2, 1, new Random(0));
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
		p.train(ins, out, 0.1, 1000);
		assertEquals(0, p.error(ins, out), 0);
		assertEquals(0, p.simulate(ins[0]).position(0, 0), 0);
		assertEquals(0, p.simulate(ins[1]).position(0, 0), 0);
		assertEquals(0, p.simulate(ins[2]).position(0, 0), 0);
		assertEquals(1, p.simulate(ins[3]).position(0, 0), 0);
		assertEquals(new Column(1, new double[]{0}), p.simulate(ins[0]));
		assertEquals(new Column(1, new double[]{0}), p.simulate(ins[1]));
		assertEquals(new Column(1, new double[]{0}), p.simulate(ins[2]));
		assertEquals(new Column(1, new double[]{1}), p.simulate(ins[3]));
	}

	@Test
	public void testIO() {
		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		Perceptron p = new Perceptron(2, 2, new Random(0));
		Column[] ins = new Column[4];
		ins[0] = new Column(2, new double[]{0, 0});
		ins[1] = new Column(2, new double[]{0, 1});
		ins[2] = new Column(2, new double[]{1, 0});
		ins[3] = new Column(2, new double[]{1, 1});
		Column[] out = new Column[4];
		out[0] = new Column(2, new double[]{1, 0});
		out[1] = new Column(2, new double[]{1, 0});
		out[2] = new Column(2, new double[]{1, 0});
		out[3] = new Column(2, new double[]{0, 1});
		p.train(ins, out, 0.1, 1000);
		assertEquals(1, p.simulate(ins[0]).position(0, 0), 0);
		assertEquals(1, p.simulate(ins[1]).position(0, 0), 0);
		assertEquals(1, p.simulate(ins[2]).position(0, 0), 0);
		assertEquals(0, p.simulate(ins[3]).position(0, 0), 0);
		assertEquals(0, p.simulate(ins[0]).position(1, 0), 0);
		assertEquals(0, p.simulate(ins[1]).position(1, 0), 0);
		assertEquals(0, p.simulate(ins[2]).position(1, 0), 0);
		assertEquals(1, p.simulate(ins[3]).position(1, 0), 0);

		String foo = System.getProperty("java.io.tmpdir")
				+ File.separator + "perceptron.tmp";
		new File(foo).deleteOnExit();

		assertTrue(p.save(foo));
		try {
			Perceptron p2 = Perceptron.open(foo);
			assertNotNull(p2);
			assertNotEquals(p, p2);

			assertEquals(p.simulate(ins[0]), p2.simulate(ins[0]));
			assertEquals(p.simulate(ins[1]), p2.simulate(ins[1]));
			assertEquals(p.simulate(ins[2]), p2.simulate(ins[2]));
			assertEquals(p.simulate(ins[3]), p2.simulate(ins[3]));
		} catch (IOException e) {
			fail();
		} catch (ClassNotFoundException e1) {
			fail();
		}
	}

}

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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import libai.common.*;
import libai.common.functions.Function;
import libai.common.functions.Identity;
import libai.common.functions.Sigmoid;
import libai.nn.supervised.backpropagation.MomentumBackpropagation;
import libai.nn.supervised.backpropagation.ResilientBackpropagation;
import org.junit.After;
import org.junit.Test;
import libai.common.functions.HyperbolicTangent;
import libai.common.functions.Sinc;

import static java.lang.Math.round;

import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class MultiLayerPerceptronTest {
	@Test
	public void testTrainXOrStandardBackProp() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 3, 1}, 
			new Function[]{
				new Identity(), 
				new Sigmoid(), 
				new Identity()
			}, new Random(0)
		);
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{0});
		out[1] = new Matrix(1, 1, new double[]{1});
		out[2] = new Matrix(1, 1, new double[]{1});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.05, 100000, 0, 4, 0.1);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.1 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));
	}
	
	@Test
	public void testTrainXOrMomentumBackProp() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 3, 1}, 
			new Function[]{
				new Identity(), 
				new Sigmoid(), 
				new Identity()
			}, new MomentumBackpropagation(0.5), new Random(0)
		);

		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{0});
		out[1] = new Matrix(1, 1, new double[]{1});
		out[2] = new Matrix(1, 1, new double[]{1});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.05, 100000, 0, 4, 0.1);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.1 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));
	}
	
	@Test
	public void testTrainNorRProp() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 16, 1}, 
			new Function[]{
				new Identity(), 
				new Sinc(), 
				new Identity()
			}, new ResilientBackpropagation(), new Random(0)
		);

		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{1});
		out[1] = new Matrix(1, 1, new double[]{0});
		out[2] = new Matrix(1, 1, new double[]{0});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.01, 1000000, 0, 4, 0.01);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.01 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));
	}
	
	@Test
	public void testIO() {
        assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 3, 3, 2}, 
			new Function[]{
				new Identity(), 
				new Sigmoid(), 
				new HyperbolicTangent(), 
				new Identity()
			}, new Random(0)
		);

		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(2, 1, new double[]{0, 1});
		out[1] = new Matrix(2, 1, new double[]{1, 0});
		out[2] = new Matrix(2, 1, new double[]{1, 0});
		out[3] = new Matrix(2, 1, new double[]{0, 1});
		mlp.train(ins, out, 0.01, 1000000, 0, 4, 0.01);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.01 > mlp.error(ins, out));
		assumeTrue(0.1 > mlp.error(ins, out));
		assertEquals(0, round(mlp.simulate(ins[0]).position(0, 0)));
		assertEquals(1, round(mlp.simulate(ins[1]).position(0, 0)));
		assertEquals(1, round(mlp.simulate(ins[2]).position(0, 0)));
		assertEquals(0, round(mlp.simulate(ins[3]).position(0, 0)));
		assertEquals(1, round(mlp.simulate(ins[0]).position(1, 0)));
		assertEquals(0, round(mlp.simulate(ins[1]).position(1, 0)));
		assertEquals(0, round(mlp.simulate(ins[2]).position(1, 0)));
		assertEquals(1, round(mlp.simulate(ins[3]).position(1, 0)));
		
		String foo = System.getProperty("java.io.tmpdir")
				   + File.separator + "perceptron.tmp";
		new File(foo).deleteOnExit();
		
		assertTrue(mlp.save(foo));
		try {
			MultiLayerPerceptron mlp2 = MultiLayerPerceptron.open(foo);
			assertNotNull(mlp2);
			assertEquals(mlp, mlp2);
			assertEquals(mlp, mlp2);

			assertEquals(mlp.simulate(ins[0]), mlp2.simulate(ins[0]));
			assertEquals(mlp.simulate(ins[1]), mlp2.simulate(ins[1]));
			assertEquals(mlp.simulate(ins[2]), mlp2.simulate(ins[2]));
			assertEquals(mlp.simulate(ins[3]), mlp2.simulate(ins[3]));
		} catch(IOException e) {
			fail();
		} catch(ClassNotFoundException e1) {
			fail();
		}
	}

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

		int nperlayer[] = {m, 4, l};
		MultiLayerPerceptron net = new MultiLayerPerceptron(
			nperlayer, 
			new Function[]{
				new Identity(), 
				new Sigmoid(), 
				new Identity()
			}, new Random(0)
		);

		net.train(p, t, 0.2, 50000, 0, n);

		assertTrue(1e-3 > net.error(p, t, 0, n));
		assertTrue(1e-3 > net.error(p, t, n, test));
		
		for (int i = n; i < p.length; i++) {
			double res = net.simulate(p[i]).position(0, 0);
			assertEquals(t[i].position(0, 0), res, 0.1);
		}
	}

	@Test(expected=NullPointerException.class)
	public void testNullPath() throws IOException, ClassNotFoundException{
		MultiLayerPerceptron.open((String)null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongBeta() {
		new MultiLayerPerceptron(new int[2], new Function[2], new MomentumBackpropagation(-1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongBeta2() {
		new MultiLayerPerceptron(new int[2], new Function[2], new MomentumBackpropagation(2));
	}

	@Test
	public void testWithProgressBarStandard() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 16, 1},
			new Function[]{
				new Identity(),
				new Sinc(),
				new Identity()
			}, new Random(0)
		);
		mlp.setProgressBar(progress);
		mlp.setPlotter(new SimplePlotter());
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{1});
		out[1] = new Matrix(1, 1, new double[]{0});
		out[2] = new Matrix(1, 1, new double[]{0});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.01, 1000000, 0, 4, 0.01);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.01 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));

		assertTrue(((SimplePlotter)mlp.getPlotter()).called);
	}

	@Test
	public void testWithProgressBarResilient() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 16, 1},
			new Function[]{
				new Identity(),
				new Sinc(),
				new Identity()
			}, new ResilientBackpropagation(), new Random(0)
		);
		mlp.setProgressBar(progress);
		mlp.setPlotter(new SimplePlotter());
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{1});
		out[1] = new Matrix(1, 1, new double[]{0});
		out[2] = new Matrix(1, 1, new double[]{0});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.01, 1000000, 0, 4, 0.01);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.01 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));

		assertTrue(((SimplePlotter)mlp.getPlotter()).called);
	}

	@Test
	public void testWithProgressBarMomentum() {
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(
			new int[]{2, 16, 1},
			new Function[]{
				new Identity(),
				new Sinc(),
				new Identity()
			}, new MomentumBackpropagation(0.5), new Random(0)
		);
		mlp.setProgressBar(progress);
		mlp.setPlotter(new SimplePlotter());
		Matrix[] ins = new Matrix[4];
		ins[0] = new Matrix(2, 1, new double[]{0, 0});
		ins[1] = new Matrix(2, 1, new double[]{0, 1});
		ins[2] = new Matrix(2, 1, new double[]{1, 0});
		ins[3] = new Matrix(2, 1, new double[]{1, 1});
		Matrix[] out = new Matrix[4];
		out[0] = new Matrix(1, 1, new double[]{1});
		out[1] = new Matrix(1, 1, new double[]{0});
		out[2] = new Matrix(1, 1, new double[]{0});
		out[3] = new Matrix(1, 1, new double[]{0});
		mlp.train(ins, out, 0.01, 1000000, 0, 4, 0.01);
		assumeTrue("MultiLayerPerceptron didn't converge, try again", 0.01 > mlp.error(ins, out));
		Matrix res = new Matrix(1, 1);
		mlp.simulate(ins[0], res);
		assertEquals(1, round(res.position(0, 0)));
		mlp.simulate(ins[1], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[2], res);
		assertEquals(0, round(res.position(0, 0)));
		mlp.simulate(ins[3], res);
		assertEquals(0, round(res.position(0, 0)));

		assertTrue(((SimplePlotter)mlp.getPlotter()).called);
	}

	private static final ProgressDisplay progress = new ProgressDisplay() {
		int min, max;
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
			assertTrue(v >= min);
			assertTrue(v <= max);
		}
	};

	class SimplePlotter implements Plotter {
		boolean called = false;

		@Override
		public void paint(Graphics g2) {

		}

		@Override
		public void update(Graphics g2) {

		}

		@Override
		public void setError(int epoch, double error) {
			called = true;
		}
	};

	private double f(double x) {
		return Math.sin(x) + Math.cos(x);
	}
}

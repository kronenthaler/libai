/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
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
package libai.fuzzy.sets;

import libai.fuzzy.Variable;

import java.util.ArrayList;

/**
 * Fuzzy set representing a triangular function. Only one point on the function
 * can take the max value of 1. The triangular function can take 3 variations:
 * <br>
 * <ul>
 * <li>right triangle to the left. a = b != c.</li>
 * <li>centered triangle a != b != c.</li>
 * <li>right triangle to the right a != b = c.</li>
 * </ul>
 *
 * In the three cases, the support of the set is the interval [a,c].<br>
 * The kernel is always the single value b.<br>
 *
 * @author kronenthaler
 */
public class Triangular implements FuzzySet {
	private Variable a, b, c;
	private ArrayList<Double> support;
	private ArrayList<Double> kernel;
	protected double DELTA = 0.1;

	public Triangular(double _a, double _b, double _c, double delta) {
		a = new Variable(_a);
		b = new Variable(_b);
		c = new Variable(_c);
		DELTA = delta;

		kernel = new ArrayList<>();
		kernel.add(b.getValue()); //middle point

		if (delta > 0) {
			support = new ArrayList<>();
			for (double i = Math.min(_a, Math.min(_b, _c)), max = Math.max(_a, Math.max(_b, _c)); i <= max; i += DELTA)
				support.add(i);
		}
	}

	public Triangular(double _a, double _b, double _c) {
		this(_a, _b, _c, .5);
	}

	@Override
	public double eval(Variable s) {
		return eval(s.getValue());
	}

	@Override
	public double eval(double s) {
		if (s <= a.getValue() || s >= c.getValue())
			return 0;
		if (s == b.getValue())
			return 1;

		if (s > a.getValue() && s < b.getValue())
			return (s - a.getValue()) / (b.getValue() - a.getValue());

		return (c.getValue() - s) / (c.getValue() - b.getValue());
	}

	@Override
	public ArrayList<Double> getSupport() {
		return support;
	}

	@Override
	public ArrayList<Double> getKernel() {
		return kernel;
	}

	@Override
	public String toString() {
		return "Triangle(" + a + "," + b + "," + c + ")";
	}
}

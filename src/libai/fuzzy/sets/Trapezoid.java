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
 * Fuzzy set representing a trapezoid function. The trapezoid function can take
 * 3 variations: <br>
 * <ul>
 * <li>right trapezoid to the left. a = b != c != d.</li>
 * <li>centered trapezoid a != b != c != d.</li>
 * <li>right trapezoid to the right a != b != c = d.</li>
 * </ul>
 *
 * In the three cases, the support of the set is the interval [a,d].<br>
 * The kernel is always the interval [b,c].<br>
 *
 * @author kronenthaler
 */
public class Trapezoid implements FuzzySet {
	private Variable a, b, c, d;
	private ArrayList<Double> support;
	private ArrayList<Double> kernel;
	protected double DELTA = 0.1;

	public Trapezoid(double _a, double _b, double _c, double _d) {
		this(_a, _b, _c, _d, 0.5);
	}

	public Trapezoid(double _a, double _b, double _c, double _d, double delta) {
		a = new Variable(_a);
		b = new Variable(_b);
		c = new Variable(_c);
		d = new Variable(_d);

		DELTA = delta;

		kernel = new ArrayList<Double>();
		support = new ArrayList<Double>();

		if (DELTA > 0) {
			for (double i = _a, max = _d; i <= max; i += DELTA) {
				support.add(i);
				if (i >= _b && i <= _c)
					kernel.add(i);
			}
		}
	}

	public double eval(Variable s) {
		return eval(s.getValue());
	}

	public double eval(double s) {
		if (s < a.getValue() || s > d.getValue())
			return 0;
		if (s > b.getValue() && s < c.getValue())
			return 1;

		if (s >= a.getValue() && s <= b.getValue())
			return (s - a.getValue()) / (b.getValue() - a.getValue());

		return (d.getValue() - s) / (d.getValue() - c.getValue());
	}

	public ArrayList<Double> getSupport() {
		return support;
	}

	public ArrayList<Double> getKernel() {
		return kernel;
	}

	public String toString() {
		return "Trapezoid(" + a + "," + b + "," + c + "," + d + ")";
	}
}

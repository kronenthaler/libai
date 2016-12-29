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

public interface FuzzySet {
	/**
	 * Evaluate the membership of the set with the specified value. Alias of
	 * {@code eval(s.getValue())}
	 *
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(Variable s);

	/**
	 * Evaluate the membership of the set with the especified value.
	 *
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(double s);

	/**
	 * Set of values where = {x e X | u(x) &gt; 0}
	 *
	 * @return The set of values where the membership is greater than zero.
	 */
	public ArrayList<Double> getSupport();

	/**
	 * Set of values where = {x e X | u(x) = 1}
	 *
	 * @return The set of values where the membership is exactly one.
	 */
	public ArrayList<Double> getKernel();
}

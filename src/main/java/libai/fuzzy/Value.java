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
package libai.fuzzy;

/**
 * Wrapper around a double value. This implementation allows, change the type of
 * the double, for other types more precises or maybe any type suitable for
 * embedded devices without change the structure of the rest of the engine.
 *
 * @author kronenthaler
 */
public class Value implements Comparable<Value> {
	/**
	 * Value of the variable
	 */
	private double value;

	/**
	 * Constructor. Creates a new variable with the value v.
	 *
	 * @param v The initial value for the variable.
	 */
	public Value(double v) {
		setValue(v);
	}

	/**
	 * Get the current value for the variable.
	 *
	 * @return The current value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set a new value v for the variable.
	 *
	 * @param v The new value for the variable.
	 */
	public void setValue(double v) {
		value = v;
	}

	/**
	 * Compare two variables.
	 *
	 * @param o the variable to compare.
	 * @return -1 if this is less than o, 0 if they are equal, 1 if this is
	 * grater than o
	 */
	@Override
	public int compareTo(Value o) {
		return (int) (value - o.value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}

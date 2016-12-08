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
package libai.common;

/**
 * Pair class allows to keep two elements of possibly different types in the
 * same structure and can be used for keep pairs key-value.
 *
 * @author kronenthaler
 */
public final class Pair<V extends Comparable, K extends Comparable> implements Comparable<Pair> {
	/**
	 * First element of the pair (the 'key').
	 */
	public V first;
	/**
	 * Second element of the pair (the 'value').
	 */
	public K second;

	/**
	 * Constructor.
	 *
	 * @param x the first element for the pair.
	 * @param y the second element for the pair.
	 */
	public Pair(V x, K y) {
		first = x;
		second = y;
	}

	/**
	 * Compare two pairs by the first element. The first element type must
	 * implement the Comparable interface.
	 *
	 * @param o The pair to compare with.
	 * @return -1 if this is less than o, 0 if are equals, 1 if this is greater
	 * than o.
	 */
	@Override
	public int compareTo(Pair o) {
		return first.compareTo(o.first);
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}
}

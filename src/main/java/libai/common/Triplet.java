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
 *
 * @author ignacio
 */
public final class Triplet <V extends Comparable, K extends Comparable, Z extends Comparable>{
	/**
	 * First element of the tuple.
	 */
	public V first;
	
	/**
	 * Second element of the tuple.
	 */
	public K second;
	
	/**
	 * Third element of the tuple.
	 */
	public Z third;

	/**
	 * Constructor.
	 *
	 * @param x the first element for the tuple.
	 * @param y the second element for the tuple.
	 * @param z the third element for the tuple.
	 */
	public Triplet(V x, K y, Z z) {
		first = x;
		second = y;
		third = z;
	}

	@Override
	public int hashCode() {
		int n = Integer.MAX_VALUE;
		int a = ((first.hashCode() % n) + (second.hashCode() % n)) % n;
		int b = ((a % n) + (third.hashCode() % n)) % n;
		return b;
	}
	
	@Override
	public boolean equals(Object o){
		if (o == null || !(o instanceof Triplet)) 
			return false;
		
		Triplet<V,K,Z> b = (Triplet<V, K, Z>)o;
		return first.equals(b.first) 
				&& second.equals(b.second) 
				&& third.equals(b.third);
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}

}

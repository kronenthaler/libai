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
package libai.search;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public abstract class State implements Comparable<State> {
	/**
	 * Get the cost of the current state
	 * @return cost of the current state
	 */
	public abstract double getCost();

	/**
	 * Get the heuristic cost of the current state
	 * @return heuristic cost of the current state
	 */
	public abstract double getHeuristicCost();

	/**
	 * Returns a list with all the possible candidates from this state
	 * @return list with all the possible candidates from this state
	 */
	public abstract ArrayList<State> getCandidates();

	/**
	 * Compare two states
	 */
	public abstract int compareTo(State o);

	/**
	 * Hash code of the state MUST BE implemented to keep track of the visited
	 * states
	 */
	public abstract int hashCode();

	/**
	 * Determines if two states are equals or equivalents
	 * @param o {@code o}
	 */
	public boolean equals(Object o) {
		return compareTo((State) o) == 0;
	}

	/**
	 * Determines if the current state is a solution or not
	 * @return {@code true} if the current state is a solution and 
	 * {@code false otherwise}
	 */
	public abstract boolean isSolution();
}
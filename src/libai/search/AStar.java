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
 * JUST USEFUL FOR MINIMIZATION PROBLEMS!!
 *
 * @author kronenthaler
 */
public class AStar implements Search {
	public State search(State init) {
		PriorityQueue<State> opened = new PriorityQueue<State>();
		HashMap<State, State> openedMirror = new HashMap<State, State>();
		HashMap<State, State> closed = new HashMap<State, State>();

		opened.add(init);
		openedMirror.put(init, init);

		while (!opened.isEmpty()) {
			State current = opened.poll();

			openedMirror.remove(current);
			closed.put(current, current);

			if (current.isSolution()) {
				return current;
			}

			for (State next : current.getCandidates()) {
				if (closed.containsKey(next)) {
					State prev = closed.get(next);
					if (prev.compareTo(next) <= 0)
						continue;

					closed.remove(next);
				}

				if (openedMirror.containsKey(next)) {
					State prev = openedMirror.get(next);
					if (prev.compareTo(next) <= 0)
						continue;

					opened.remove(next); //bottle neck, must be a way to reduce the time, try indexing
					openedMirror.remove(next);
				}

				opened.add(next);
				openedMirror.put(next, next);
			}
		}

		return null;
	}
}

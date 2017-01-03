/*
 * The MIT License
 *
 * Copyright 2017 Ignacio Calderon (http://github.com/kronenthaler).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package libai.search;

import java.util.ArrayList;

/**
 *
 * @author kronenthaler
 */
public class GemPuzzleState extends State {
		private final String table;
		private final GemPuzzleState parent;
		private final int stepsx[] = {0, 0, 1, -1};
		private final int stepsy[] = {1, -1, 0, 0};
		private final char dir[] = {'u', 'd', 'l', 'r'};
		//down, up, right, left
		private final char move;
		private final int point;
		public final String solution = "12345678.";
		private int cost, heutisticCost;

		GemPuzzleState(String t, GemPuzzleState p, char m) {
			this(t, p, m, 0);
		}
		
		GemPuzzleState(String t, GemPuzzleState p, char m, int cost){
			table = t;
			parent = p;
			move = m;
			point = table.indexOf('.');
			
			this.cost = cost;
			heutisticCost = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int c = table.charAt(i * 3 + j);
					if (c >= '1' && c <= '8') {
						c -= '0' - 1;
						int er = c / 3;
						int ec = c % 3;
						heutisticCost += Math.abs(i - er) + Math.abs(j - ec);
					} else {
						heutisticCost += Math.abs(i - 2) + Math.abs(j - 2);
					}
				}
			}
		}

		public void printSolution(StringBuilder out) {
			if (parent != null)
				parent.printSolution(out);
			out.append(table).append('\n');
		}

		public void printSolutionMoves(StringBuilder out) {
			if (parent != null)
				parent.printSolutionMoves(out);
			out.append(move);
		}

		@Override
		public double getHeuristicCost() {
			return heutisticCost;
		}

		@Override
		public double getCost() {
			return cost;
		}

		@Override
		public ArrayList<State> getCandidates() {
			//do until 4 moves.
			ArrayList<State> candidates = new ArrayList<>();
			final int row = point / 3;
			final int col = point % 3;
			for (int i = 0; i < 4; i++) {
				if (i == 0 && row == 2) continue;
				if (i == 1 && row == 0) continue;
				if (i == 3 && col == 0) continue;
				if (i == 2 && col == 2) continue;
				
				final char t = table.charAt((row + stepsy[i]) * 3 + (col + stepsx[i]));
				String newstate = table.replace('.', '#').replace(t, '.');
				newstate = newstate.replace('#', t);
				candidates.add(new GemPuzzleState(newstate, this, dir[i], cost + 1));
			}
			return candidates;
		}

		@Override
		public int compareTo(State o) {
			GemPuzzleState n = (GemPuzzleState) o;
			return (int) ((getCost() + getHeuristicCost()) - (n.getCost() + n.getHeuristicCost()));
		}

		@Override
		public String toString() {
			return table;
		}

		@Override
		public int hashCode() {
			return table.hashCode();
		}

		@Override 
		public boolean equals(Object o){
			return o != null && o instanceof GemPuzzleState && table.equals(((GemPuzzleState)o).table);
		}
		
		@Override
		public boolean isSolution() {
			return table.equals(solution);
		}
	}
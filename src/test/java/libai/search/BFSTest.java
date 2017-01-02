/*
 * MIT License
 *
 * Copyright (c) 2016 Federico Vera <https://github.com/dktcoding>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining ada copy
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

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class BFSTest {
	
	public BFSTest() {
	}

	@Test
	public void testDemo() {
		BFS bfs = new BFS();
		
		State init = new Node(".87654321", null, '\0');
		Node ans = (Node) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS.length());
		ans.printSolution(sb);
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS, sb.toString());
	}

	@Test
	public void testDemo2() {
		BFS bfs = new BFS();
		
		State init = new Node("1234567.8", null, '\0');
		Node ans = (Node) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS_2.length());
		ans.printSolution(sb);
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS_2, sb.toString());
		StringBuilder sb2 = new StringBuilder(2);
		ans.printSolutionMoves(sb2);
		assertEquals("\0l", sb2.toString());
	}
	
	@Test
	public void testDemo3() {
		BFS bfs = new BFS();
		
		State init = new Node("12345.786", null, '\0');
		Node ans = (Node) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS_3.length());
		ans.printSolution(sb);
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS_3, sb.toString());
		StringBuilder sb2 = new StringBuilder(2);
		ans.printSolutionMoves(sb2);
		assertEquals("\0u", sb2.toString());
	}
	
	@Test
	public void testImpossible(){
		BFS bfs = new BFS();

		State init = new Node("21345678.", null, '\0');
		Node ans = (Node)bfs.search(init);

		assertEquals(ans, null);
	}
        
	private final String DEMO_STEPS = 
		 ".87654321\n"
		+"687.54321\n"
		+"687354.21\n"
		+"6873542.1\n"
		+"68735421.\n"
		+"68735.214\n"
		+"68.357214\n"
		+"6.8357214\n"
		+".68357214\n"
		+"368.57214\n"
		+"368257.14\n"
		+"3682571.4\n"
		+"36825714.\n"
		+"36825.147\n"
		+"36.258147\n"
		+"3.6258147\n"
		+".36258147\n"
		+"236.58147\n"
		+"236158.47\n"
		+"2361584.7\n"
		+"23615847.\n"
		+"23615.478\n"
		+"23.156478\n"
		+"2.3156478\n"
		+".23156478\n"
		+"123.56478\n"
		+"123456.78\n"
		+"1234567.8\n"
		+"12345678.\n";
	private final String DEMO_STEPS_2 = "1234567.8\n12345678.\n";
	private final String DEMO_STEPS_3 = "12345.786\n12345678.\n";
	
	private final Node DEMO_TARGET = new Node("12345678.", null, '\0');

	private class Node extends State {
		private final String table;
		private final Node parent;
		private final int stepsx[] = {0, 0, 1, -1};
		private final int stepsy[] = {1, -1, 0, 0};
		private final char dir[] = {'u', 'd', 'l', 'r'};
		//down, up, right, left
		private final char move;
		private final int point;
		

		Node(String t, Node p, char m) {
			table = t;
			parent = p;
			move = m;
			point = table.indexOf('.');
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
			return 0;
		}

		@Override
		public double getCost() {
			Node current = this.parent;
			int cont = 0;
			while (current != null) {
				cont++;
				current = current.parent;
			}
			return cont;
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
				candidates.add(new Node(newstate, this, dir[i]));
			}
			return candidates;
		}

		@Override
		public int compareTo(State o) {
			return table.compareTo(((Node) o).table);
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
		public boolean isSolution() {
			return table.equals(DEMO_TARGET.table);
		}
	}
	
}

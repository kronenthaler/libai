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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class BFSTest {
	private final String DEMO_STEPS = "-uullddrruullddrruullddrruull";
	private final String DEMO_STEPS_2 = "1234567.8\n12345678.\n";
	private final String DEMO_STEPS_3 = "12345.786\n12345678.\n";

	@Test
	public void testDemo() {
		BFS bfs = new BFS();
		
		State init = new GemPuzzleState(".87654321", null, '-');
		GemPuzzleState ans = (GemPuzzleState) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS.length());
		ans.printSolutionMoves(sb);
		
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS, sb.toString());
	}

	@Test
	public void testDemo2() {
		BFS bfs = new BFS();
		
		State init = new GemPuzzleState("1234567.8", null, '-');
		GemPuzzleState ans = (GemPuzzleState) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS_2.length());
		ans.printSolution(sb);
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS_2, sb.toString());
		StringBuilder sb2 = new StringBuilder(2);
		ans.printSolutionMoves(sb2);
		assertEquals("-l", sb2.toString());
	}
	
	@Test
	public void testDemo3() {
		BFS bfs = new BFS();
		
		State init = new GemPuzzleState("12345.786", null, '-');
		GemPuzzleState ans = (GemPuzzleState) bfs.search(init);
		StringBuilder sb = new StringBuilder(DEMO_STEPS_3.length());
		ans.printSolution(sb);
		assertFalse(sb.length() == 0);
		assertEquals(DEMO_STEPS_3, sb.toString());
		StringBuilder sb2 = new StringBuilder(2);
		ans.printSolutionMoves(sb2);
		assertEquals("-u", sb2.toString());
	}
	
	@Test
	public void testImpossible() {
		BFS bfs = new BFS();

		State init = new GemPuzzleState("21345678.", null, '-');
		GemPuzzleState ans = (GemPuzzleState) bfs.search(init);

		assertEquals(ans, null);
	}
}

/*
 * MIT License
 *
 * Copyright (c) 2017 Federico Vera <https://github.com/dktcoding>
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

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class PairTest {
	@Test
	public void testSort() {
		Pair[] pairs = new Pair[] {
			new Pair(-10, 4),
			new Pair( 10, 6),
			new Pair(  3, 2),
			new Pair(  2, 4),
		};
		Pair[] pairsSorted = new Pair[] {
			new Pair(-10, 4),
			new Pair(  2, 4),
			new Pair(  3, 2),
			new Pair( 10, 6),
		};
		Arrays.sort(pairs);
		assertArrayEquals(pairs, pairsSorted);
	}

	@Test
	public void testHashCodeEquals() {
		HashMap<Pair, Pair> map = new HashMap<>();

		Pair[] pairs = new Pair[] {
			new Pair(-10, 4),
			new Pair( 10, 6),
			new Pair(  3, 2),
			new Pair(  2, 4),
		};

		for (Pair p : pairs) {
			map.put(p, p);
		}

		for (Pair p : pairs) {
			assertEquals(p, map.get(p));
			assertTrue(p == map.get(p));
		}
	}

	@Test
	public void testHashCode() {
		Pair p1 = new Pair(-10, 4);
		Pair p2 = new Pair(-10, 4);

		assertEquals(p1.hashCode(), p1.hashCode());
		assertEquals(p1.hashCode(), p2.hashCode());
		p2.first = "-10";
		assertNotEquals(p1.hashCode(), p2.hashCode());
		p2.first = -10;
		assertEquals(p1.hashCode(), p2.hashCode());
		p2.second = 4.0;
		assertNotEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void testToString() {
		Pair pair = new Pair("abc", "def");
		assertEquals("(abc,def)", pair.toString());
	}

	@Test
	public void testEquals() {
		Pair pair = new Pair("abc", "def");
		assertNotEquals(pair, null);
		assertNotEquals(new Object(), pair);
		assertNotEquals(4, pair);
	}
}

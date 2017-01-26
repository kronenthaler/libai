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

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class TripletTest {

	@Test
	public void testHashCode() {
		Triplet[] trips = new Triplet[] {
			new Triplet(-10, 4, -1),
			new Triplet( 10, 6, -1),
			new Triplet(  3, 2, -1),
			new Triplet(  2, 4, -1),
		};
		HashMap<Triplet, Triplet> map = new HashMap<>(4);
		for (Triplet t : trips) {
			map.put(t, t);
		}
		for (Triplet t : trips) {
			assertEquals(t, map.get(t));
			assertTrue(t == map.get(t));
		}
	}

	@Test
	public void testHashCode2() {
		Triplet t1 = new Triplet(-10, 4, -1);
		Triplet t2 = new Triplet(-10, 4, -1);
		assertEquals(t1.hashCode(), t1.hashCode());
		assertEquals(t1.hashCode(), t2.hashCode());
		t2.first = -9;
		assertNotEquals(t1.hashCode(), t2.hashCode());
		t2.first = -10;
		assertEquals(t1.hashCode(), t2.hashCode());
		t2.second = "";
		assertNotEquals(t1.hashCode(), t2.hashCode());
		t2.second = 4;
		assertEquals(t1.hashCode(), t2.hashCode());
		t2.third = -1.0;
		assertNotEquals(t1.hashCode(), t2.hashCode());
	}

	@Test
	public void testEquals() {
		Triplet triplet = new Triplet(  2, 4, -1);
		assertEquals(triplet, triplet);
		assertNotEquals(triplet, null);
		assertNotEquals(triplet, new Object());
		assertEquals(triplet, new Triplet(  2, 4, -1));
		assertNotEquals(triplet, new Triplet(  2, 4, 1));
		assertNotEquals(triplet, new Triplet(  2, 2, -1));
		assertNotEquals(triplet, new Triplet(  1, 4, -1));
		assertNotEquals(triplet, new Triplet("1", 4, -1));
	}

	@Test
	public void testToString() {
		Triplet triplet = new Triplet(  2, 4, -1);
		assertEquals("(2,4,-1)", triplet.toString());
		triplet = new Triplet(  2, 4, null);
		assertEquals("(2,4,null)", triplet.toString());
	}
}

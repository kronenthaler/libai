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
package libai.classifiers.dataset;

import libai.classifiers.Attribute;
import libai.classifiers.ContinuousAttribute;
import libai.classifiers.DiscreteAttribute;
import libai.common.MatrixIOTest;
import org.junit.Test;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class TextFileDataSetTest {
	private static final String toString =
			"[[[0]]=4.0, [[1]]=b, [[2]]=false, [[3]]=1.5, [[4]]=same]\n" +
					"[[[0]]=2.0, [[1]]=b, [[2]]=false, [[3]]=12.0, [[4]]=same]\n" +
					"[[[0]]=0.0, [[1]]=a, [[2]]=true, [[3]]=4.4, [[4]]=same]\n" +
					"[[[0]]=3.0, [[1]]=a, [[2]]=true, [[3]]=-12.0, [[4]]=same]\n" +
					"[[[0]]=1.0, [[1]]=f, [[2]]=true, [[3]]=4.4, [[4]]=same]\n" +
					"[[[0]]=5.0, [[1]]=d, [[2]]=true, [[3]]=8.0, [[4]]=same]\n";

	private static boolean writeDummyDataSet(String fname) {
		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + fname;
		new File(tmp).deleteOnExit();
		try (PrintStream ps = new PrintStream(tmp)) {
			Random r = ThreadLocalRandom.current();
			for (int i = 0; i < 100; i++) {
				ps.append("" + i).append(',');
				ps.append("" + (r.nextDouble() * 20 - 10)).append(',');
				ps.append("" + (r.nextGaussian())).append(',');
				ps.append("" + (r.nextBoolean()));
				if (i != 99) ps.append('\n');
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean writeDummyDataSetKnown(String fname) {
		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + fname;
		new File(tmp).deleteOnExit();
		try (PrintStream ps = new PrintStream(tmp)) {
			ps.append("0,a,true,4.4,same\n");
			ps.append("4,b,false,1.5,same\n");
			ps.append("3,a,true,-12.0,same\n");
			ps.append("1,f,true,4.4,same\n");
			ps.append("5,d,true,8,same\n");
			ps.append("2,b,false,12.0,same\n");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean writeDummyDataSetKnown2(String fname) {
		assumeTrue("Can't use temp dir...", MatrixIOTest.checkTemp());
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + fname;
		new File(tmp).deleteOnExit();
		try (PrintStream ps = new PrintStream(tmp)) {
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			ps.append("1,2,3,same\n");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Test
	public void testGetSubset() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSet("dummy.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 0);
		assertNotEquals(0, ds.getItemsCount());
		DataSet ds2 = ds.getSubset(10, 20);
		assertEquals(10, ds2.getItemsCount());

		double i = 10;
		for (List<Attribute> attrib : ds2) {
			assertEquals(i++, attrib.get(ds.getOutputIndex()).getValue());
		}
		ds.close();
	}

	@Test
	public void testGetOutputIndex() {
		TextFileDataSet ds = new TextFileDataSet(2);
		assertEquals(2, ds.getOutputIndex());
		DataSet ds2 = ds.getSubset(0, 0);
		assertEquals(2, ds2.getOutputIndex());
		ds.close();
	}

	@Test
	public void testGetMetaData() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSet("dummy.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 0);
		MetaData md = ds.getMetaData();
		assertEquals(4, md.getAttributeCount());
		assertFalse(md.isCategorical(0));
		assertFalse(md.isCategorical(1));
		assertFalse(md.isCategorical(2));
		assertTrue(md.isCategorical(3));
		assertEquals("[0]", md.getAttributeName(0));
		assertEquals("[1]", md.getAttributeName(1));
		assertEquals("[2]", md.getAttributeName(2));
		assertEquals("[3]", md.getAttributeName(3));
		ds.close();
	}

	@Test
	public void testSortOverInt() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy1.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy1.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 0);
		Iterator<List<Attribute>> attribs = ds.sortOver(0).iterator();
		assertEquals(0.0, attribs.next().get(0).getValue());
		assertEquals(1.0, attribs.next().get(0).getValue());
		assertEquals(2.0, attribs.next().get(0).getValue());
		assertEquals(3.0, attribs.next().get(0).getValue());
		assertEquals(4.0, attribs.next().get(0).getValue());
		assertEquals(5.0, attribs.next().get(0).getValue());
		attribs = ds.sortOver(1).iterator();
		assertEquals("a", attribs.next().get(1).getValue());
		assertEquals("a", attribs.next().get(1).getValue());
		assertEquals("b", attribs.next().get(1).getValue());
		assertEquals("b", attribs.next().get(1).getValue());
		assertEquals("d", attribs.next().get(1).getValue());
		assertEquals("f", attribs.next().get(1).getValue());
		attribs = ds.sortOver(2).iterator();
		assertEquals("false", attribs.next().get(2).getValue());
		assertEquals("false", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		attribs = ds.sortOver(3).iterator();
		assertEquals(-12.0, attribs.next().get(3).getValue());
		assertEquals(1.5, attribs.next().get(3).getValue());
		assertEquals(4.4, attribs.next().get(3).getValue());
		assertEquals(4.4, attribs.next().get(3).getValue());
		assertEquals(8.0, attribs.next().get(3).getValue());
		assertEquals(12.0, attribs.next().get(3).getValue());
		ds.close();
	}

	@Test
	public void testSortOver2() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy2.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy2.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 0);
		Iterator<List<Attribute>> attribs = ds.sortOver(2, 5, 0).iterator();
		assertEquals(2.0, attribs.next().get(0).getValue());
		assertEquals(3.0, attribs.next().get(0).getValue());
		attribs = ds.sortOver(2, 5, 2).iterator();
		assertEquals("true", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		ds.close();
	}

	@Test
	public void testSplitKeepingRelation() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy3.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy3.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 2);
		DataSet[] dss = ds.splitKeepingRelation(0.5);
		assertEquals(3, dss[0].getItemsCount());
		assertEquals(3, dss[1].getItemsCount());
		dss = ds.splitKeepingRelation(0.3);
		assertEquals(1, dss[0].getItemsCount());
		assertEquals(5, dss[1].getItemsCount());
		ds.close();
	}

	@Test
	public void testToString() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy4.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy4.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 2);
		assertEquals(toString, ds.toString());
	}

	@Test
	public void testGetFrequencies() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy5.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy5.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 2);
		HashMap<Attribute, Integer> map = ds.getFrequencies(0, ds.getItemsCount(), 2);
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(Integer.valueOf(4), map.get(new DiscreteAttribute("true")));
		map = ds.getFrequencies(0, ds.getItemsCount(), 2); //Test cache
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(Integer.valueOf(4), map.get(new DiscreteAttribute("true")));
	}

	@Test
	public void testGetFrequencies2() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy6.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy6.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 2);
		ds.sortOver(2);
		HashMap<Attribute, Integer> map = ds.getFrequencies(0, 2, 2);
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(null, map.get(new DiscreteAttribute("true")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFrequencies3() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy7.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy7.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 1);
		ds.getFrequencies(0, 0, 0); //Non categorical
	}

	@Test
	public void testAllTheSameOutput() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy8.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy8.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 2);
		assertFalse(ds.allTheSameOutput());
	}

	@Test
	public void testAllTheSameOutput2() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy9.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy9.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 4);
		assertTrue(ds.allTheSameOutput());
	}

	@Test
	public void testAllTheSame() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy10.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy10.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 4);
		assertNull(ds.allTheSame());
	}

	@Test
	public void testAllTheSame2() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown2("dummy11.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy11.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 3);
		assertEquals(new DiscreteAttribute("3", "same"), ds.allTheSame());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllTheSame3() {
		assumeTrue("Couldn't create dummy dataset", writeDummyDataSetKnown("dummy12.csv"));
		String tmp = System.getProperty("java.io.tmpdir") + File.separator + "dummy12.csv";
		TextFileDataSet ds = new TextFileDataSet(new File(tmp), 0);
		ds.allTheSame();
	}

	@Test
	public void testAttributtesWithNames() {
		ContinuousAttribute ca = new ContinuousAttribute("name", 3.4);
		assertEquals("[name]=3.4", ca.toString());
	}

}

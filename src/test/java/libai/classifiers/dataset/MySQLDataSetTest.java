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
import libai.classifiers.DiscreteAttribute;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNotNull;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class MySQLDataSetTest {

	@BeforeClass
	public static void setUp() throws Exception {
		// create the database test_libai
		Connection conn = getConnection("");
		PreparedStatement stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS test_libai;");
		stmt.execute();
		stmt.close();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		// drop the database test_libai
		Connection conn = getConnection();
		PreparedStatement stmt = conn.prepareStatement("DROP DATABASE IF EXISTS test_libai;");
		stmt.execute();
		stmt.close();
	}

	@Test
	public void testGetSubset() {
		Connection c = writeDummyDataSet();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl1", 0);
		assertNotEquals(0, ds.getItemsCount());
		DataSet ds2 = ds.getSubset(10, 20);
		assertEquals(10, ds2.getItemsCount());
		assertEquals(0, ds.getOutputIndex());
		assertEquals(0, ds2.getOutputIndex());

		double i = 10;
		for (List<Attribute> attrib : ds2) {
			assertEquals(i++, attrib.get(ds.getOutputIndex()).getValue());
		}
		ds.close();
	}

	@Test
	public void testGetItemsCount() {
	}

	@Test
	public void testGetMetaData() {
		Connection c = writeDummyDataSet();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl1", 0);
		MetaData md = ds.getMetaData();
		assertEquals(4, md.getAttributeCount());
		assertFalse(md.isCategorical(0));
		assertFalse(md.isCategorical(1));
		assertFalse(md.isCategorical(2));
		assertTrue (md.isCategorical(3));
		assertEquals("col1", md.getAttributeName(0));
		assertEquals("col2", md.getAttributeName(1));
		assertEquals("col3", md.getAttributeName(2));
		assertEquals("col4", md.getAttributeName(3));
		ds.close();
	}

	@Test
	public void testSortOver_int() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 0);
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
		assertEquals(  1.5, attribs.next().get(3).getValue());
		assertEquals(  4.4, attribs.next().get(3).getValue());
		assertEquals(  4.4, attribs.next().get(3).getValue());
		assertEquals(  8.0, attribs.next().get(3).getValue());
		assertEquals( 12.0, attribs.next().get(3).getValue());
		ds.close();
	}

	@Test
	public void testSortOver2() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 0);
		Iterator<List<Attribute>> attribs = ds.sortOver(2, 5, 0).iterator();
		assertEquals(2.0, attribs.next().get(0).getValue());
		assertEquals(3.0, attribs.next().get(0).getValue());
		attribs = ds.sortOver(2, 5, 2).iterator();
		assertEquals("true", attribs.next().get(2).getValue());
		assertEquals("true", attribs.next().get(2).getValue());
		ds.close();
	}

	@Test
	public void testSplitKeepingRelation() throws SQLException {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 2);
		DataSet[] dss = ds.splitKeepingRelation(0.5);
		assertEquals(3, dss[0].getItemsCount());
		assertEquals(3, dss[1].getItemsCount());
		dss = ds.splitKeepingRelation(0.3);
		assertEquals(1, dss[0].getItemsCount());
		assertEquals(5, dss[1].getItemsCount());
		ds.close();
		c.close();
	}

	@Test
	public void testAllTheSameOutput() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 2);
		assertFalse(ds.allTheSameOutput());
	}

	@Test
	public void testAllTheSameOutput2() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 4);
		assertTrue(ds.allTheSameOutput());
	}

	@Test
	public void testAllTheSame() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 4);
		assertNull(ds.allTheSame());
	}

	@Test
	public void testAllTheSame2() {
		Connection c = writeDummyDataSetKnown2();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl3", 3);
		assertEquals(new DiscreteAttribute("col3", "same"), ds.allTheSame());
		ds.clean();
	}

	@Test
	public void testGetFrequencies() throws SQLException {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 2);
		HashMap<Attribute, Integer> map = ds.getFrequencies(0, ds.getItemsCount(), 2);
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(Integer.valueOf(4), map.get(new DiscreteAttribute("true")));
		map = ds.getFrequencies(0, ds.getItemsCount(), 2); //Test cache
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(Integer.valueOf(4), map.get(new DiscreteAttribute("true")));
		c.close();
	}

	@Test
	public void testGetFrequencies2() throws SQLException {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 2);
		ds.sortOver(2);
		HashMap<Attribute, Integer> map = ds.getFrequencies(0, 2, 2);
		assertEquals(Integer.valueOf(2), map.get(new DiscreteAttribute("false")));
		assertEquals(null, map.get(new DiscreteAttribute("true")));
		c.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFrequencies3() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 1);
		ds.getFrequencies(0, 0, 0); //Non categorical
	}

	private static final String toString =
		"[[col1]=4.0, [col2]=b, [col3]=false, [col4]=1.5, [col5]=same]\n" +
		"[[col1]=2.0, [col2]=b, [col3]=false, [col4]=12.0, [col5]=same]\n" +
		"[[col1]=0.0, [col2]=a, [col3]=true, [col4]=4.4, [col5]=same]\n" +
		"[[col1]=3.0, [col2]=a, [col3]=true, [col4]=-12.0, [col5]=same]\n" +
		"[[col1]=1.0, [col2]=f, [col3]=true, [col4]=4.4, [col5]=same]\n" +
		"[[col1]=5.0, [col2]=d, [col3]=true, [col4]=8.0, [col5]=same]\n";

	@Test
	public void testToString() {
		Connection c = writeDummyDataSetKnown();
		assumeNotNull(c);
		MySQLDataSet ds = new MySQLDataSet(c, "tbl2", 2);
		assertEquals(toString, ds.toString());
	}

	private static Connection getConnection() {
		return getConnection("test_libai");
	}

	private static Connection getConnection(String database) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/"+database, "root", ""
			);
		} catch (ClassNotFoundException |
				 InstantiationException |
				 IllegalAccessException |
				 SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static Connection writeDummyDataSet() {
		Connection conn = getConnection();
		try (Statement s = conn.createStatement();
			 PreparedStatement ps = conn.prepareStatement(
					 "INSERT INTO `tbl1`(`col1`,`col2`,`col3`,`col4`)VALUES(?,?,?,?);")) {
			s.execute("DROP TABLE IF EXISTS `tbl1`;");
			s.execute("CREATE TABLE `tbl1` ("
					+ "`col1` INT, "
					+ "`col2` DOUBLE,\n"
					+ "`col3` DOUBLE,\n"
					+ "`col4` VARCHAR(45));");

			Random r = ThreadLocalRandom.current();
			for (int i = 0; i < 100; i++) {
				ps.setInt(1, i);
				ps.setDouble(2, r.nextDouble() * 20 - 10);
				ps.setDouble(3, r.nextGaussian());
				ps.setString(4, Boolean.toString(r.nextBoolean()));
				ps.addBatch();
			}
			ps.executeBatch();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Connection writeDummyDataSetKnown() {
		Connection conn = getConnection();
		try (Statement s = conn.createStatement()) {
			s.execute("DROP TABLE IF EXISTS `tbl2`;");
			s.execute("CREATE TABLE `tbl2` ("
					+ "`col1` INT, "
					+ "`col2` VARCHAR(45),\n"
					+ "`col3` VARCHAR(45),\n"
					+ "`col4` DOUBLE,\n"
					+ "`col5` VARCHAR(45));");

			String query = "INSERT INTO `tbl2`(`col1`,`col2`,`col3`,`col4`, `col5`)VALUES(%s);";
			s.execute(String.format(query, "0,'a','true',4.4,'same'"));
			s.execute(String.format(query, "4,'b','false',1.5,'same'"));
			s.execute(String.format(query, "3,'a','true',-12.0,'same'"));
			s.execute(String.format(query, "1,'f','true',4.4,'same'"));
			s.execute(String.format(query, "5,'d','true',8,'same'"));
			s.execute(String.format(query, "2,'b','false',12.0,'same'"));
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Connection writeDummyDataSetKnown2() {
		Connection conn = getConnection();
		try (Statement s = conn.createStatement()) {
			s.execute("DROP TABLE IF EXISTS `tbl3`;");
			s.execute("CREATE TABLE `tbl3` ("
					+ "`col1` INT, "
					+ "`col2` INT,\n"
					+ "`col3` INT,\n"
					+ "`col4` VARCHAR(45));");

			String query = "INSERT INTO `tbl3`(`col1`,`col2`,`col3`,`col4`)VALUES(%s);";
			s.execute(String.format(query, "1,2,3,'same'"));
			s.execute(String.format(query, "1,2,3,'same'"));
			s.execute(String.format(query, "1,2,3,'same'"));
			s.execute(String.format(query, "1,2,3,'same'"));
			s.execute(String.format(query, "1,2,3,'same'"));
			s.execute(String.format(query, "1,2,3,'same'"));
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

package libai.fuzzy2.sets;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public class TriangularTest extends TestCase {
	@Test
	public void testXMLGeneration(){
		Triangular set = new Triangular(0, 5, 10);

		assertEquals("<TriangularShape Param1=\"0.000000\" Param2=\"5.000000\" Param3=\"10.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testEvalBeforeA(){
		Triangular set = new Triangular(0, 5, 10);

		assertTrue(set.eval(-1) == 0);
	}

	@Test
	public void testEvalAfterC(){
		Triangular set = new Triangular(0, 5, 10);

		assertTrue(set.eval(11) == 0);
	}

	@Test
	public void testEvalBetweenAB(){
		Triangular set = new Triangular(0, 5, 10);

		assertTrue(set.eval(2.5) == 0.5);
	}

	@Test
	public void testEvalBetweenBC(){
		Triangular set = new Triangular(0, 5, 10);

		assertTrue(set.eval(7.5) == 0.5);
	}

	@Test
	public void testEvalAtC(){
		Triangular set = new Triangular(0, 5, 10);

		assertTrue(set.eval(5) == 1);
	}
}

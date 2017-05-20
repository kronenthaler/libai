package libai.fuzzy2.defuzzifiers;

import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 17/05/2017.
 */
public class CenterOfAreaTest {
	@Test
	public void testCompoundTriangles(){
		TriangularShape a = new TriangularShape(0, 4, 4);
		TriangularShape b = new TriangularShape(4, 5, 10);
		ArrayList<Point.Double> points = new ArrayList<>();
		for(double x=0; x < 10; x+=0.001){
			points.add(new Point.Double(x, Math.max(a.eval(x), b.eval(x))));
		}

		// area of first triangle = 2,
		// area of first part of second triangle = 0.5
		// area of second part of second triangle = 2.5
		// total area = 5, half point at 2.5 area accumulated = center of second triangle
		assertEquals(5, Defuzzifier.COA.getValue(points), 1.e-3);
	}

	@Test
	public void testSimpleTriangle(){
		TriangularShape a = new TriangularShape(0, 3, 6);
		ArrayList<Point.Double> points = new ArrayList<>();
		for(double x=0; x < 10; x+=0.001){
			points.add(new Point.Double(x, a.eval(x)));
		}

		assertEquals(3, Defuzzifier.COA.getValue(points), 1.e-3);
	}
}

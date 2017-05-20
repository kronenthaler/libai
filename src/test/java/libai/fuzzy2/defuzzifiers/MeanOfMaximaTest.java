package libai.fuzzy2.defuzzifiers;

import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 15/05/2017.
 */
public class MeanOfMaximaTest {
	@Test
	public void testSimpleTriangle(){
		TriangularShape set = new TriangularShape(0, 3, 6);
		ArrayList<Point.Double> points = new ArrayList<>();
		for(double x=0; x < 6; x+=0.1){
			points.add(new Point.Double(x, set.eval(x)));
		}

		assertEquals(3, Defuzzifier.MOM.getValue(points), 1.e-5);
	}

	@Test
	public void testCompoundedTriangles(){
		TriangularShape a = new TriangularShape(0, 3, 6);
		TriangularShape b = new TriangularShape(4, 7, 10);
		ArrayList<Point.Double> points = new ArrayList<>();
		for(double x=0; x < 10; x+=0.1){
			points.add(new Point.Double(x, Math.max(0.5*a.eval(x), 0.75*b.eval(x))));
		}

		assertEquals(7, Defuzzifier.MOM.getValue(points), 1.e-5);
	}
}

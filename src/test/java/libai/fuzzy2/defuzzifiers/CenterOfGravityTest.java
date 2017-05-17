package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 15/05/2017.
 */
public class CenterOfGravityTest {
	@Test
	public void testRiemannSumSimpleTriangle(){
		TriangularShape set = new TriangularShape(0, 3, 4);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 6; x+=0.1){
			points.add(new Pair<>(x, set.eval(x)));
		}

		assertEquals(2, new CenterOfGravity().riemmanSum(points), 1.e-5);
	}

	@Test
	public void testRiemannSumCompoundedTriangle(){
		TriangularShape a = new TriangularShape(0, 3, 6);
		TriangularShape b = new TriangularShape(7, 10, 13);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 13; x+=0.1){
			points.add(new Pair<>(x, Math.max(Math.min(0.5, a.eval(x)), Math.min(0.75, b.eval(x)))));
		}

		assertEquals(2.25 + 2.8125, new CenterOfGravity().riemmanSum(points), 1.e-3);
	}

	@Test
	public void testRiemannSumToMidPoint(){
		TriangularShape a = new TriangularShape(0, 3, 6);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 13; x+=0.1){
			points.add(new Pair<>(x, a.eval(x)));
		}

		assertEquals((1.23*a.eval(1.23)) / 2., new CenterOfGravity().riemmanSum(points, 1.23), 1.e-3);
	}

	@Test
	public void testRiemannTriangle(){
		TriangularShape a = new TriangularShape(0, 1, 2);
		TriangularShape b = new TriangularShape(1, 2, 3);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 4; x+=0.01){
			points.add(new Pair<>(x, Math.max(Math.min(0.25, a.eval(x)), Math.min(0.5, b.eval(x)))));
		}

		assertEquals(1, new CenterOfGravity().riemmanSum(points), 1.e-8);
	}

	@Test
	public void testCompoundedTriangle(){
		TriangularShape a = new TriangularShape(0, 1, 2);
		TriangularShape b = new TriangularShape(1, 2, 3);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 4; x+=0.01){
			points.add(new Pair<>(x, Math.max(Math.min(0.25, a.eval(x)), Math.min(0.5, b.eval(x)))));
		}

		assertEquals(1.656, Defuzzifier.COG.getValue(points), 1.e-3);
	}

	@Test
	public void testImmutableCopy(){
		TriangularShape a = new TriangularShape(0, 1, 2);
		TriangularShape b = new TriangularShape(1, 2, 3);
		ArrayList<Pair<Double, Double>> points = new ArrayList<>();
		for(double x=0; x < 4; x+=0.01){
			points.add(new Pair<>(x, Math.max(Math.min(0.25, a.eval(x)), Math.min(0.5, b.eval(x)))));
		}

		double control = points.get(points.size() / 2).second;

		assertEquals(1.656, Defuzzifier.COG.getValue(points), 1.e-3);

		assertEquals(control, points.get(points.size() / 2).second, 1.e-3);
	}
}

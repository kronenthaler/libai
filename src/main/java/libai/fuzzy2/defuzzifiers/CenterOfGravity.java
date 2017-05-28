package libai.fuzzy2.defuzzifiers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Defuzzifier method that calculates the center of gravity of given function.
 * Formally the center of gravity is Integral(x.f(x)) / Integral(f(x)) for the whole interval.
 * This implementation uses Riemann's sums to calculate the integrals of both functions.
 *
 * @author kronenthaler
 */
public class CenterOfGravity extends Defuzzifier {
	/**
	 * Implements the center of gravity by calculating the Riemann's sums of the area using the trapezoidal rule.
	 * It assumes the x points are equally spaced across the whole domain of the function.
	 * @param function the list of points [x, f(x)] representing the function to analyze.
	 * @return the center of gravity of the function.
	 **/
	@Override
	public double getValue(List<Point.Double> function) {
		Collections.sort(function, new Comparator<Point.Double>() {
			@Override
			public int compare(Point.Double o1, Point.Double o2) {
				return (int)(o1.x - o2.x);
			}
		});

		double denominator = riemmanSum(function);

		// copy the points but applying th x.f(x)
		List<Point.Double> xFunction = new ArrayList<>();
		for(Point.Double point : function) {
			xFunction.add(new Point.Double(point.x, point.x * point.y));
		}
		double nominator = riemmanSum(xFunction);

		return nominator / denominator;
	}

	protected double riemmanSum(List<Point.Double> function){
		return riemmanSum(function, function.get(function.size() - 1).x);
	}

	protected double riemmanSum(List<Point.Double> function, double maxX){
		double result = 0;
		for(int i=0;i<function.size() - 1;i++){
			Point.Double point =  function.get(i);
			Point.Double point1 =  function.get(i+1);
			double delta = point1.x - point.x;

			if(point.x < maxX && point1.x > maxX){
				double t = (maxX - point.x) / delta;
				double y = point.y + (t * delta);
				result += ((point.y + y)/2.) * Math.abs(maxX - point.x);
				break;
			}

			result += ((point.y + point1.y)/2.) * delta;
		}
		return result;
	}

	@Override
	public String toString(){ return "COG"; }
}

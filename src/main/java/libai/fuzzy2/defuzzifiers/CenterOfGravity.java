package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class CenterOfGravity extends Defuzzifier {
	/**
	 * Implements the center of gravity by calculating the Riemann's sums of the area using the trapezoidal rule.
	 * It assumes the x points are equally spaced across the whole domain of the function.
	 **/
	@Override
	public double getValue(List<Pair<Double, Double>> function) {
		Collections.sort(function);

		double denominator = riemmanSum(function);

		// copy the points but applying th x.f(x)
		List<Pair<Double, Double>> xFunction = new ArrayList<>();
		for(Pair<Double, Double> point : function) {
			xFunction.add(new Pair<>(point.first, point.first * point.second));
		}
		double nominator = riemmanSum(xFunction);

		return nominator / denominator;
	}

	protected double riemmanSum(List<Pair<Double, Double>> function){
		return riemmanSum(function, function.get(function.size() - 1).first);
	}

	protected double riemmanSum(List<Pair<Double, Double>> function, double maxX){
		double result = 0;
		for(int i=0;i<function.size() - 1;i++){
			Pair<Double, Double> point =  function.get(i);
			Pair<Double, Double> point1 =  function.get(i+1);
			double delta = point1.first - point.first;

			if(point.first < maxX && point1.first > maxX){
				double t = (maxX - point.first) / delta;
				double y = point.second + (t * delta);
				result += ((point.second + y)/2.) * Math.abs(maxX - point.first);
				break;
			}

			result += ((point.second + point1.second)/2.) * delta;
		}
		return result;
	}

	@Override
	public String toString(){ return "COG"; }
}

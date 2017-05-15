package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class MeanOfMaxima extends Defuzzifier {
	public double getValue(List<Pair<Double, Double>> function){
		double max = Double.MIN_VALUE;
		double left = Double.MAX_VALUE;
		double right = Double.MIN_VALUE;

		for(Pair<Double, Double> point : function){
			if (point.second > max) {
				max = point.second;
				left = point.first;
				right = point.first;
			}

			if (point.second == max){
				left = Math.min(left, point.first);
				right = Math.max(right, point.first);
			}
		}

		return (left + right)/2.;
	}

	@Override
	public String toString(){ return "MOM"; }
}

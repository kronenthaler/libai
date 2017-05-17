package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;

import java.util.Collections;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class CenterOfArea extends CenterOfGravity {
	@Override
	public double getValue(List<Pair<Double, Double>> function) {
		Collections.sort(function);

		double total = riemmanSum(function);
		double lo = function.get(0).first;
		double hi = function.get(function.size()-1).first;


		while(lo < hi){
			double mid = (lo + hi) / 2;
			double areaToMid = riemmanSum(function, mid);
			double remain = total - areaToMid;

			if (Math.abs(remain - areaToMid) < 1.e-8) return mid;
			if (remain > areaToMid)
				lo = mid;
			else
				hi = mid;
		}

		return (lo + hi) / 2;
	}

	@Override
	public String toString(){ return "COA"; }
}

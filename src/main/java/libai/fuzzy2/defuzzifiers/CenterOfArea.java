package libai.fuzzy2.defuzzifiers;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class CenterOfArea extends CenterOfGravity {
	@Override
	public double getValue(List<Point.Double> function) {
		Collections.sort(function, new Comparator<Point.Double>() {
			@Override
			public int compare(Point.Double o1, Point.Double o2) {
				return (int)(o1.x - o2.x);
			}
		});

		double total = riemmanSum(function);
		double lo = function.get(0).x;
		double hi = function.get(function.size()-1).x;


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

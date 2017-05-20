package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class MeanOfMaxima extends Defuzzifier {
	public double getValue(List<Point.Double> function){
		Collections.sort(function, new Comparator<Point.Double>() {
			@Override
			public int compare(Point.Double o1, Point.Double o2) {
				return (int)(o1.x - o2.x);
			}
		});

		double max = -(Double.MAX_VALUE-1);
		double left = Double.MAX_VALUE;
		double right = Double.MIN_VALUE;

		for(Point.Double point : function){
			if (point.y > max) {
				max = point.y;
				left = point.x;
				right = point.x;
			}

			if (point.y == max){
				left = Math.min(left, point.x);
				right = Math.max(right, point.x);
			}
		}

		return (left + right)/2.;
	}

	@Override
	public String toString(){ return "MOM"; }
}

package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;

import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class CenterOfGravity extends Defuzzifier {
	@Override
	public double getValue(List<Pair<Double, Double>> function) {
		return 0;
	}

	@Override
	public String toString(){ return "COG";}
}

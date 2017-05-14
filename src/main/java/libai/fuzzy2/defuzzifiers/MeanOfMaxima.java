package libai.fuzzy2.defuzzifiers;

import libai.common.Pair;
import libai.fuzzy2.Clause;
import libai.fuzzy2.KnowledgeBase;
import libai.fuzzy2.operators.activation.ActivationMethod;

import java.util.List;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class MeanOfMaxima extends Defuzzifier {
	public double getValue(List<Pair<Double, Double>> function){
		return 0;
	}

	@Override
	public String toString(){ return "MOM"; }
}

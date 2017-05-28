package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class PIShape extends TwoParameterSet {
	public PIShape(Node xmlNode) {
		load(xmlNode);
	}

	public PIShape(double a, double b){
		this.a = a; // center
		this.b = b; // offset, ~stddev.
	}

	@Override
	public double eval(double x) {
		if (x < a)
			return new SShape(a - b , a).eval(x);
		return new ZShape(a, a + b).eval(x);
	}
}
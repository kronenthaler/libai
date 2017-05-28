package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class GaussianShape extends TwoParameterSet {
	public GaussianShape(Node xmlNode) {
		load(xmlNode);
	}

	public GaussianShape(double a, double b){
		this.a = a; // mean
		this.b = b; // sigma
	}

	@Override
	public double eval(double x) {
		return Math.exp(-Math.pow(x - a, 2) / (2 * b * b));
	}
}

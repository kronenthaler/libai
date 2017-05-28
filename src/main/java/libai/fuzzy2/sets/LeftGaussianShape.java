package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class LeftGaussianShape  extends TwoParameterSet {
	public LeftGaussianShape(Node xmlNode) {
		load(xmlNode);
	}

	public LeftGaussianShape(double a, double b){
		this.a = a; // mean
		this.b = b; // sigma
	}

	@Override
	public double eval(double x) {
		if (x >= a)
			return 1;
		return new GaussianShape(a, b).eval(x);
	}
}
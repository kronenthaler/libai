package libai.fuzzy.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a gaussian function. Opposed to the normal gaussian function, this function is not symmetrical.
 * All values to the left of the mean, will be equivalent to 1.
 *
 * @author kronenthaler
 */
public class LeftGaussianShape  extends TwoParameterSet {
	public LeftGaussianShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Mean value of the Gaussian function.
	 * @param b Standard deviation (a.k.a. sigma) of the Gaussian function.
	 **/
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
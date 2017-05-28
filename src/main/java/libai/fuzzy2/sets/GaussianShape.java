package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a gaussian function. The gaussian function requires 2 parameters, mean (center value) and
 * standard deviation (sigma value). This function is symmetrical around the mean.
 *
 * @author kronenthaler
 */
public class GaussianShape extends TwoParameterSet {
	public GaussianShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Mean value of the Gaussian function.
	 * @param b Standard deviation (a.k.a. sigma) of the Gaussian function.
	 **/
	public GaussianShape(double a, double b){
		this.a = a; // mean
		this.b = b; // sigma
	}

	@Override
	public double eval(double x) {
		return Math.exp(-Math.pow(x - a, 2) / (2 * b * b));
	}
}

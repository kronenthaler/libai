package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a increasing linear function, in the [a, b] interval. Anything to the left of a will be 0, and
 * anything to the right of b will be 1.
 *
 * @author kronenthaler
 */
public class LeftLinearShape extends TwoParameterSet {
	public LeftLinearShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left value of the line.
	 * @param b Right value of the line.
	 **/
	public LeftLinearShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		if (x >= b)
			return 1;

		if (x <= a)
			return 0;

		return (x - a) / (b - a);
	}
}
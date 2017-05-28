package libai.fuzzy.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a decreasing linear function, in the [a, b] interval. Anything to the left of a will be 1, and
 * anything to the right of b will be 0.
 *
 * @author kronenthaler
 */
public class RightLinearShape extends TwoParameterSet {
	public RightLinearShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left value of the line.
	 * @param b Right value of the line.
	 **/
	public RightLinearShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		if (x >= b)
			return 0;

		if (x <= a)
			return 1;

		return (b - x) / (b - a);
	}
}
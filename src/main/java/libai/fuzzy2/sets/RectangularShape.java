package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a constant function, in the [a, b] interval. This function becomes 1 only during the [a, b]
 * interval.
 *
 * @author kronenthaler
 */
public class RectangularShape extends TwoParameterSet {
	public RectangularShape(Node xmlNode){
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left vertex of the rectangle.
	 * @param b Right vertex of the rectangle.
	 **/
	public RectangularShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		return a <= x && x <= b ? 1 : 0;
	}
}

package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a Pi-shaped function. This function behaves as a S-shape for any value to the left of a.
 * And behaves as a Z-shape to any value to the right of a (including a).
 *
 * @author kronenthaler
 */
public class PIShape extends TwoParameterSet {
	public PIShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Center point of the Pi-shape.
	 * @param b Offset relative to the center of the Pi-shape.
	 **/
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
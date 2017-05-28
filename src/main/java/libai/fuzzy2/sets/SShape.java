package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Fuzzy set representing a S-shape function. This function will equal 0 for any value to the left of a, and 1 for any
 * value to the right of b. This function is a mirror of the Z-shape.
 *
 * @author kronenthaler
 */
public class SShape extends TwoParameterSet{
	public SShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left value of the S-shape.
	 * @param b Right value of the S-shape.
	 **/
	public SShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		if (x <= a) return 0;
		if (x >= b) return 1;

		if(x >= a && x <= (a + b) / 2)
			return 2 * Math.pow((x - a) / (b - a), 2);

		return 1 - (2 * Math.pow((x - b) / (b - a), 2));
	}
}

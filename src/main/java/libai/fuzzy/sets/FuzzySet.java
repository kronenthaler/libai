package libai.fuzzy.sets;

import libai.fuzzy.XMLSerializer;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public interface FuzzySet extends XMLSerializer {
	/**
	 * Evaluate the membership of the set with the especified value.
	 *
	 * @param x Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(double x);
}
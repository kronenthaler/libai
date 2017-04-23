package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;

import java.util.ArrayList;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public interface FuzzySet extends XMLSerializer {
	/**
	 * Evaluate the membership of the set with the especified value.
	 *
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(double s);
}

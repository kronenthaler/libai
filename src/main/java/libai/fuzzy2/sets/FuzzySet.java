package libai.fuzzy2.sets;

import java.util.ArrayList;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public interface FuzzySet {
	/**
	 * Evaluate the membership of the set with the especified value.
	 *
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(double s);

	/**
	 * Returns the XML representation of this Fuzzy set according with the FML schema definition.
	 * @return XML representation of this Fuzzy Set.
	 **/
	public String toXMLString(String indent);
}

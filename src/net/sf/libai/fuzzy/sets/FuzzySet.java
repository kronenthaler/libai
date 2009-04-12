package net.sf.libai.fuzzy.sets;

import net.sf.libai.fuzzy.*;
import java.util.ArrayList;

public interface FuzzySet {
	/**
	 * Evaluate the membership of the set with the especified value.
	 * Alias of eval(s.getValue()).
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(Variable s);

	/**
	 * Evaluate the membership of the set with the especified value.
	 * @param s Value to evaluate.
	 * @return The membership value for the input.
	 */
	public double eval(double s);

	/**
	 * Set of values where = {x e X | u(x) > 0}
	 * @return The set of values where the membership is greater than zero.
	 */
	public ArrayList<Double> getSupport();

	/**
	 * Set of values where = {x e X | u(x) = 1}
	 * @return The set of values where the membership is exactly one.
	 */
	public ArrayList<Double> getKernel();
}

package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public interface Function {
	/**
	 * Function to evaluate.
	 */
	public double eval(double x);

	/**
	 * Evaluate the first derivate of this function f'(x)
	 */
	public Function getDerivate();
}

package net.sf.libai.common;
import java.io.Serializable;

/**
 * Interface for real fuctions F(x): R -> R.
 * If the function has a derivate, the method getDerivate() MUST be implemented and return a new
 * object to handle this derivate.
 * @author kronenthaler
 */
public interface Function extends Serializable{
	/**
	 * Function to evaluate.
	 * @param x the input value
	 * @return f(x)
	 */
	public double eval(double x);

	/**
	 * Evaluate the first derivate of this function f'(x)
	 * @return a new Function object with the derivate function.
	 */
	public Function getDerivate();
}

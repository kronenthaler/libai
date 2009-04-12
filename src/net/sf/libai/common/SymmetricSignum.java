package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public class SymmetricSignum implements Function{
	public double eval(double x) {
		return x<0 ? -1 : 1;
	}

	public Function getDerivate() {
		return null;
	}
}

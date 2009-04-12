package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public class Signum implements Function{
	public double eval(double x) {
		return x<0 ? 0 : 1;
	}

	public Function getDerivate() {
		return null;
	}
}

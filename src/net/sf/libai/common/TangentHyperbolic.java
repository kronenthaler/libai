package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public class TangentHyperbolic implements Function{
	private static Function derivate;

	public double eval(double x) {
		return Math.tanh(x);
	}

	public Function getDerivate(){
		if(derivate == null){
			derivate = new Function(){
				public double eval(double x){
					double a = Math.tanh(x);
					return (1.0-(a*a));
				}
				public Function getDerivate(){ return null; }
			};
		}

		return derivate;
	}
}

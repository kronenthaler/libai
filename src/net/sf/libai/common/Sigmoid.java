package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public class Sigmoid implements Function{
	private static Function derivate;
	
	public double eval(double x) {
		return (1.0 / (1.0 + Math.exp(-x)));
	}

	public Function getDerivate(){
		if(derivate == null){
			final Sigmoid me =this;
			derivate = new Function(){
				public double eval(double x){
					double a=me.eval(x);
					return a*(1.0-a);
				}
				public Function getDerivate(){ return null; }
			};
		}

		return derivate;
	}
}

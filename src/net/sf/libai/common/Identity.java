package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public class Identity implements Function{
	private static Function derivate;
	public double eval(double x) {
		return x;
	}

	public Function getDerivate(){
		if(derivate == null){
			derivate = new Function(){
				public double eval(double x){ return 1; }
				public Function getDerivate(){ return null; }
			};
		}

		return derivate;
	}
}

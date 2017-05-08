package libai.fuzzy2.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public class MinMethod extends AndMethod {

	@Override
	public double eval(double a, double b) {
		return Math.min(a, b);
	}

	@Override
	public String toString(){ return "MIN"; }
}

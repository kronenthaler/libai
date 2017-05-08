package libai.fuzzy2.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public class MaxMethod extends OrMethod {
	@Override
	public double eval(double a, double b) {
		return Math.max(a, b);
	}

	@Override
	public String toString(){ return "MAX"; }
}

package libai.fuzzy.operators.activation;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class MamdaniActivation extends ActivationMethod {
	@Override
	public double eval(double a, double b) {
		return Math.min(a, b);
	}

	@Override
	public String toString() { return "MIN"; }
}

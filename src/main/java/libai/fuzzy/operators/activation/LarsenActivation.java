package libai.fuzzy.operators.activation;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class LarsenActivation extends ActivationMethod {
	@Override
	public double eval(double a, double b) {
		return a * b;
	}

	@Override
	public String toString() { return "PROD"; }
}

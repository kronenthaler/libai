package libai.fuzzy.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public class ProdMethod extends AndMethod {
	@Override
	public double eval(double a, double b) {
		return a * b;
	}

	@Override
	public double neutral() { return 1; }

	@Override
	public String toString(){ return "PROD"; }
}

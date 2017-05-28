package libai.fuzzy.operators;

/**
 * Created by kronenthaler on 08/05/2017.
 */
public class ProbOrMethod extends OrMethod {
	@Override
	public double eval(double a, double b) {
		return (a+b) - (a*b);
	}

	@Override
	public double neutral() { return 0; }

	@Override
	public String toString(){ return "PROBOR"; }
}

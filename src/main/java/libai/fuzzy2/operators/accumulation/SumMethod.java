package libai.fuzzy2.operators.accumulation;

/**
 * Created by kronenthaler on 14/05/2017.
 */
public class SumMethod extends Accumulation {
	@Override
	public double eval(double a, double b) {
		return a + b;
	}

	@Override
	public String toString(){ return "SUM"; }
}

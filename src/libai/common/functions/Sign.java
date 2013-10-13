package libai.common.functions;

/**
 * Signum function F(x) = 0 if x &lt; 0 or 1 if x &gt;= 1 This function is not
 * derivable.
 *
 * @author kronenthaler
 */
public class Sign implements Function {
	public double eval(double x) {
		return x > 0 ? 1 : 0;
	}

	public Function getDerivate() {
		return null;
	}
}

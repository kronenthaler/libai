package libai.common;

/**
 *
 * @author kronenthaler
 */
public class PolynomialKernel implements Kernel {
	private double a, b;

	public PolynomialKernel(double _a, double _b) {
		a = _a;
		b = _b;
	}

	public double eval(Matrix A, Matrix B) {
		return Math.pow(A.dotProduct(B) - a, b);
	}

	public double eval(double dotProduct) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

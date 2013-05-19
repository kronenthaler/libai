package libai.common;

/**
 *
 * @author kronenthaler
 */
public class SigmoidalKernel implements Kernel {
	private double a, b;

	public SigmoidalKernel(double _a, double _b) {
		a = _a;
		b = _b;
	}

	public double eval(Matrix A, Matrix B) {
		return Math.tanh(a * A.dotProduct(B) - b);
	}

	public double eval(double dotProduct) {
		return Math.tanh(a * dotProduct - b);
	}
}
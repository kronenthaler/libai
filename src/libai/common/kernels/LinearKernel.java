package libai.common.kernels;

import libai.common.Matrix;

/**
 * Implements a linear kernel based on the dot product.
 *
 * @author kronenthaler
 */
public class LinearKernel implements Kernel {
	public double eval(Matrix A, Matrix B) {
		return A.dotProduct(B);
	}

	public double eval(double dotProduct) {
		return dotProduct;
	}
}

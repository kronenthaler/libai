package libai.common.kernels;

import libai.common.Matrix;

/**
 * Implements a Gaussian kernel.
 *
 * @author kronenthaler
 */
public class GaussianKernel implements Kernel {
    private double sigma;

    public GaussianKernel(double _sigma) {
        sigma = _sigma * _sigma * 2;
    }

    public double eval(Matrix A, Matrix B) {
        double AB = A.dotProduct(B);
        double AA = A.dotProduct(A);
        double BB = B.dotProduct(B);

        double s = -2 * AB + AA + BB;
        return Math.exp((-s / sigma));
    }

    public double eval(double dotProduct) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

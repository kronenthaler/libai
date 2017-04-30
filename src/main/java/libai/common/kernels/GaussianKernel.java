/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.common.kernels;

import libai.common.matrix.Matrix;

/**
 * Gaussian Kernel or RBF kernel. Follows the form: K(x,y) = e(||x-y||^2 / (2*s^2)), where s is a parameter of this
 * kernel, aka as sigma or standard deviation.
 *
 * @author kronenthaler
 */
public class GaussianKernel implements Kernel {
	private static final long serialVersionUID = 7002651958563140173L;

	private double gamma; // = 1 / (2 * sigma ^ 2)

	public GaussianKernel(double sigma) {
		this.gamma = 1.0 / (sigma * sigma * 2);
	}

	@Override
	public double eval(Matrix A, Matrix B) {
		double AB = A.dotProduct(B);
		double AA = A.dotProduct(A);
		double BB = B.dotProduct(B);

		double s = -2 * AB + AA + BB;
		return Math.exp(-s * gamma);
	}
}

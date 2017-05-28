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
 * Polynomial Kernel. Follows the form: K(x,y) = (a * xy + c)^p, where a, b & p are parameters of this kernel.
 *
 * @author kronenthaler
 */
public class PolynomialKernel implements Kernel {
	private static final long serialVersionUID = 5132845207274843125L;

	private double a;
	private double c;
	private double p;

	public PolynomialKernel(double a, double c, double p) {
		this.a = a;
		this.c = c;
		this.p = p;
	}

	@Override
	public double eval(Matrix A, Matrix B) {
		double dotProduct = A.dotProduct(B);
		return Math.pow(a * dotProduct + c, p);
	}
}

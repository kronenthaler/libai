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

import java.io.Serializable;
import libai.common.Matrix;

/**
 *
 * @author kronenthaler
 */
public interface Kernel extends Serializable {
	/**
	 * Evaluates a kernel between the two Vectors .
	 *
	 * @param A The first pattern to calculate the kernel
	 * @param B The second pattern to calculate the kernel
	 * @return A double with the value of the calculated kernel
	 */
	public double eval(Matrix A, Matrix B);

	public double eval(double dotProduct);
}
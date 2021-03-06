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
package libai.common.functions;

/**
 * Sigmoid function: f(x) = 1/(1+e^-x) The first derivate of the sigmoid
 * function S(x) S'(x) = S(x)*(1-S(x))
 *
 * @author kronenthaler
 */
public class Sigmoid implements Function {
	private static final long serialVersionUID = 910726042653348547L;

	private static final Function derivate = new Function() {
		@Override
		public double eval(double x) {
			double a = (1.0 / (1.0 + Math.exp(-x)));
			return a * (1.0 - a);
		}

		@Override
		public Function getDerivate() {
			return null;
		}
	};

	@Override
	public double eval(double x) {
		return (1.0 / (1.0 + Math.exp(-x)));
	}

	@Override
	public Function getDerivate() {
		return derivate;
	}
}

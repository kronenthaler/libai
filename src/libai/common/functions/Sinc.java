/*
 * MIT License
 *
 * Copyright (c) 2016 Federico Vera <https://github.com/dktcoding>
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
 * Function Sinc.
 * <pre> 
 * Sinc(x)  = {1         for x  = 0 
 *             sin(x)/x  for x != 0}.
 * 
 * Sinc'(x) = {0                    for x  = 0
 *             cos(x)/x-sin(x)/x^2  for x != 0}
 * </pre>
 * 
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class Sinc implements Function {
	private static Function derivate;

	@Override
	public double eval(double x) {
		if (x == 0) {
			return 1;
		}
		return Math.sin(x) / x;
	}

	@Override
	public Function getDerivate() {
		if (derivate == null) {
			derivate = new Function() {
				@Override
				public double eval(double x) {
					if (x == 0) {
						return 0;
					}
					return (Math.cos(x) / x) - (Math.sin(x) / (x * x));
				}

				@Override
				public Function getDerivate() {
					String msg = "Second derivative not implemented for 'Sinc(x)'";
					throw new UnsupportedOperationException(msg);
				}
			};
		}

		return derivate;
	}
}

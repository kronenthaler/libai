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
 * Function ArcTan.
 * <pre>
 * ArcTan(x)  = tan^-1(x)
 *
 * ArcTan'(x) = 1 / (x^2 + 1)
 * </pre>
 *
 * @author Federico Vera {@literal <fedevera at unc.edu.ar>}
 */
public class ArcTangent implements Function {
	private static final long serialVersionUID = 1967026062364806506L;

	private static final Function derivate = new Function() {
		@Override
		public double eval(double x) {
			return 1. / (x * x + 1.);
		}

		@Override
		public Function getDerivate() {
			String msg = "Second derivative not implemented for 'ArcTan(x)'";
			throw new UnsupportedOperationException(msg);
		}
	};

	@Override
	public double eval(double x) {
		return Math.atan(x);
	}

	@Override
	public Function getDerivate() {
		return derivate;
	}
}

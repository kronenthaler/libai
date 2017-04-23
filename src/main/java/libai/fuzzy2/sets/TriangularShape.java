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
package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.Node;

/**
 * Fuzzy set representing a triangular function. Only one point on the function
 * can take the max value of 1. The triangular function can take 3 variations:
 * <br>
 * <ul>
 * <li>right triangle to the left. a = b != c.</li>
 * <li>centered triangle a != b != c.</li>
 * <li>right triangle to the right a != b = c.</li>
 * </ul>
 * <p>
 * In the three cases, the support of the set is the interval [a,c].<br>
 * The kernel is always the single value b.<br>
 *
 * @author kronenthaler
 */
public class TriangularShape implements FuzzySet, XMLSerializer {
	private double a, b, c;

	public TriangularShape(Node xmlNode) throws Exception {
		load(xmlNode);
	}

	public TriangularShape(double _a, double _b, double _c) {
		a = _a;
		b = _b;
		c = _c;
	}

	@Override
	public double eval(double s) {
		if (s <= a || s >= c)
			return 0;

		if (s == b)
			return 1;

		if (s > a && s < b)
			return (s - a) / (b - a);

		return (c - s) / (c - b);
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<TriangularShape Param1=\"%f\" Param2=\"%f\" Param3=\"%f\"/>", indent, a, b, c);
	}

	@Override
	public void load(Node xmlNode) throws Exception {
		a = Double.parseDouble(xmlNode.getAttributes().getNamedItem("Param1").getNodeValue());
		b = Double.parseDouble(xmlNode.getAttributes().getNamedItem("Param2").getNodeValue());
		c = Double.parseDouble(xmlNode.getAttributes().getNamedItem("Param3").getNodeValue());
	}
}

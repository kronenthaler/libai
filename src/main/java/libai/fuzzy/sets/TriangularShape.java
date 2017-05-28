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
package libai.fuzzy.sets;

import org.w3c.dom.NamedNodeMap;
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
 *
 * @author kronenthaler
 */
public class TriangularShape implements FuzzySet {
	private double a;
	private double b;
	private double c;

	public TriangularShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left vertex of the triangle.
	 * @param b Middle vertex of the triangle.
	 * @param c Right vertex of the triangle.
	 **/
	public TriangularShape(double a, double b, double c){
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public double eval(double x) {
		if ((a == b && x == a) || (b == c && x == b))
			return 1;

		if (x <= a || x >= c)
			return 0;

		if (x == b)
			return 1;

		if (x > a && x < b)
			return (x - a) / (b - a);

		return (c - x) / (c - b);
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<TriangularShape Param1=\"%f\" Param2=\"%f\" Param3=\"%f\"/>", indent, a, b, c);
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		a = Double.parseDouble(attributes.getNamedItem("Param1").getTextContent());
		b = Double.parseDouble(attributes.getNamedItem("Param2").getTextContent());
		c = Double.parseDouble(attributes.getNamedItem("Param3").getTextContent());
	}
}

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
package libai.classifiers;

import org.w3c.dom.Node;

/**
 * @author kronenthaler
 */
public abstract class Attribute implements Comparable<Attribute> {
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public abstract Object getValue();

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Attribute && this.compareTo(((Attribute) o)) == 0;
	}

	public boolean isCategorical() {
		return this instanceof DiscreteAttribute;
	}

	public static Attribute load(Node root) {
		Attribute att = null;
		String type = root.getAttributes().getNamedItem("type").getTextContent();

		if (type.equals(DiscreteAttribute.class.getName()))
			att = new DiscreteAttribute(root.getAttributes().getNamedItem("name").getTextContent(), root.getTextContent());
		else if (type.equals(ContinuousAttribute.class.getName()))
			att = new ContinuousAttribute(root.getAttributes().getNamedItem("name").getTextContent(), Double.parseDouble(root.getTextContent()));

		return att;
	}

	public static Attribute getInstance(String value, String name) {
		Attribute attr = getInstance(value);
		attr.setName(name);
		return attr;
	}

	public static Attribute getInstance(String value) {
		try {
			return new ContinuousAttribute(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			return new DiscreteAttribute(value);
		}
	}

	public static Attribute getInstance(double value, String name) {
		Attribute attr = getInstance(value);
		attr.setName(name);
		return attr;
	}

	public static Attribute getInstance(double value) {
		return new ContinuousAttribute(value);
	}


	public static Attribute getInstance(int value, String name) {
		Attribute attr = getInstance(value);
		attr.setName(name);
		return attr;
	}

	public static Attribute getInstance(int value) {
		return new ContinuousAttribute(value);
	}
}
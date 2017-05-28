package libai.fuzzy.sets;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Fuzzy set representing a trapezoid function. The trapezoid function can take
 * 3 variations: <br>
 * <ul>
 * <li>right trapezoid to the left. a = b != c != d.</li>
 * <li>centered trapezoid a != b != c != d.</li>
 * <li>right trapezoid to the right a != b != c = d.</li>
 * </ul>
 *
 * @author kronenthaler
 */
public class TrapezoidShape implements FuzzySet {
	private double a;
	private double b;
	private double c;
	private double d;

	public TrapezoidShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Left vertex of the trapezoid.
	 * @param b Middle-left vertex of the trapezoid.
	 * @param c Middle-right vertex of the trapezoid.
	 * @param d Right vertex of the trapezoid.
	 **/
	public TrapezoidShape(double a, double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public double eval(double x) {
		if ((a == b && x == a) || (d == c && x == c) || (b <= x && x <= c))
			return 1;

		if (x <= a || x >= d)
			return 0;

		if (a < x && x < b)
			return (x - a) / (b - a);

		return (d - x) / (d - c);
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<TrapezoidShape Param1=\"%f\" Param2=\"%f\" Param3=\"%f\" Param4=\"%f\"/>", indent, a, b, c, d);
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		a = Double.parseDouble(attributes.getNamedItem("Param1").getTextContent());
		b = Double.parseDouble(attributes.getNamedItem("Param2").getTextContent());
		c = Double.parseDouble(attributes.getNamedItem("Param3").getTextContent());
		d = Double.parseDouble(attributes.getNamedItem("Param4").getTextContent());
	}
}

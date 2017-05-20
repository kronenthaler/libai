package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 20/05/2017.
 */
public class TrapezoidShape implements FuzzySet, XMLSerializer {
	private double a;
	private double b;
	private double c;
	private double d;

	public TrapezoidShape(Node xmlNode) {
		load(xmlNode);
	}

	public TrapezoidShape(double a, double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public double eval(double s) {
		if ((a == b && s == a) || (d == c && s == c) || (b <= s && s <= c))
			return 1;

		if (s <= a || s >= d)
			return 0;

		if (a < s && s < b)
			return (s - a) / (b - a);

		return (d - s) / (d - c);
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

package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class RightLinearShape implements FuzzySet, XMLSerializer {
	private double a;
	private double b;

	public RightLinearShape(Node xmlNode) {
		load(xmlNode);
	}

	public RightLinearShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double s) {
		if (s >= b)
			return 0;

		if (s <= a)
			return 1;

		return (b - s) / (b - a);
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<RightLinearShape Param1=\"%f\" Param2=\"%f\"/>", indent, a, b);
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		a = Double.parseDouble(attributes.getNamedItem("Param1").getTextContent());
		b = Double.parseDouble(attributes.getNamedItem("Param2").getTextContent());
	}
}
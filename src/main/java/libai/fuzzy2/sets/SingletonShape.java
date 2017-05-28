package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 20/05/2017.
 */
public class SingletonShape implements FuzzySet {
	private double a;

	public SingletonShape(Node xmlNode) {
		load(xmlNode);
	}

	public SingletonShape(double a) {
		this.a = a;
	}

	@Override
	public double eval(double x) {
		return x == a ? 1 : 0;
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<SingletonShape Param1=\"%f\"/>", indent, a);
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		a = Double.parseDouble(attributes.getNamedItem("Param1").getTextContent());
	}
}

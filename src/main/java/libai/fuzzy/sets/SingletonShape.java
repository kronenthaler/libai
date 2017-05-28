package libai.fuzzy.sets;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Fuzzy set with only one value not equal to zero.
 *
 * @author kronenthaler
 */
public class SingletonShape implements FuzzySet {
	private double a;

	public SingletonShape(Node xmlNode) {
		load(xmlNode);
	}

	/**
	 * Constructor.
	 * @param a Point where the singleton is non-zero.
	 **/
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

package libai.fuzzy2.sets;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public abstract class TwoParameterSet implements FuzzySet {
	protected double a;
	protected double b;

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<%s Param1=\"%f\" Param2=\"%f\"/>", indent, this.getClass().getSimpleName(), a, b);
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		a = Double.parseDouble(attributes.getNamedItem("Param1").getTextContent());
		b = Double.parseDouble(attributes.getNamedItem("Param2").getTextContent());
	}
}

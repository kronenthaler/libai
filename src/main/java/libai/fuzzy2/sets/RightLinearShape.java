package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class RightLinearShape extends TwoParameterSet {
	public RightLinearShape(Node xmlNode) {
		load(xmlNode);
	}

	public RightLinearShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		if (x >= b)
			return 0;

		if (x <= a)
			return 1;

		return (b - x) / (b - a);
	}
}
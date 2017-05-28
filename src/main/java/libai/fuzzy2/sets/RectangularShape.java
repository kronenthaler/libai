package libai.fuzzy2.sets;

import libai.fuzzy2.XMLSerializer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 20/05/2017.
 */
public class RectangularShape extends TwoParameterSet {

	public RectangularShape(Node xmlNode){
		load(xmlNode);
	}

	public RectangularShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		return a <= x && x <= b ? 1 : 0;
	}
}

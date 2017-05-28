package libai.fuzzy2.sets;

import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class SShape extends TwoParameterSet{
	public SShape(Node xmlNode) {
		load(xmlNode);
	}

	public SShape(double a, double b){
		this.a = a;
		this.b = b;
	}

	@Override
	public double eval(double x) {
		if (x <= a) return 0;
		if (x >= b) return 1;

		if(x >= a && x <= (a + b) / 2)
			return 2 * Math.pow((x - a) / (b - a), 2);

		return 1 - (2 * Math.pow((x - b) / (b - a), 2));
	}
}

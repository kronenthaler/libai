package libai.fuzzy2;

import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public class FuzzyVariable implements XMLSerializer {
	private List<FuzzyTerm> terms; // group of fuzzy terms that this variable can take.
	private String name; //name of the linguistic variable that represents
	private String scale; //label
	private double domainLeft;
	private double domainRight;
	private double defaultValue = 0;
	private boolean type; //input/output
	private String accumulation = "MAX";
	private String defuzzifier = "COG"; //defuzzifier interface?

	public FuzzyVariable(Node xmlNode) throws Exception {
		load(xmlNode);
	}

	@Override
	public String toXMLString(String indent) {
		StringBuffer str = new StringBuffer();
		str.append(String.format("%s<FuzzyVariable name=\"%s\" domainLeft=\"%f\" domainRight=\"%f\">\n", indent, name, domainLeft, domainRight));
		for (FuzzyTerm t : terms) {
			str.append(String.format("%s\n", t.toXMLString(indent + "\t")));
		}
		str.append(String.format("%s</FuzzyVariable>", indent, name));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) throws Exception {
		//parse
	}
}

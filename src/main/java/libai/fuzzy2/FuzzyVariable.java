package libai.fuzzy2;

import libai.common.Pair;
import libai.fuzzy2.defuzzifiers.Defuzzifier;
import libai.fuzzy2.operators.accumulation.Accumulation;
import libai.fuzzy2.operators.activation.ActivationMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public class FuzzyVariable implements XMLSerializer {
	protected List<FuzzyTerm> terms; // group of fuzzy terms that this variable can take.
	protected String name; //name of the linguistic variable that represents
	protected double domainLeft;
	protected double domainRight;
	protected String scale; //label
	protected Type type = Type.INPUT;
	// for output variables
	protected double defaultValue = 0;
	protected Accumulation accumulation = Accumulation.MAX;
	protected Defuzzifier defuzzifier = Defuzzifier.COG; //defuzzifier interface?

	public FuzzyVariable(Node xmlNode) {
		load(xmlNode);
	}
	public FuzzyVariable(String name, double domainLeft, double domainRight, String scale, FuzzyTerm... terms) {
		this.name = name;
		this.domainLeft = domainLeft;
		this.domainRight = domainRight;
		this.scale = scale;
		this.terms = Arrays.asList(terms);
	}
	public FuzzyVariable(String name, double domainLeft, double domainRight, double defaultValue, String scale, Accumulation accumulation, Defuzzifier defuzzifier, FuzzyTerm... terms) {
		this(name, domainLeft, domainRight, scale, terms);
		this.type = Type.OUTPUT;
		this.defaultValue = defaultValue;
		this.accumulation = accumulation;
		this.defuzzifier = defuzzifier;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuffer str = new StringBuffer();
		str.append(String.format("%s<FuzzyVariable name=\"%s\" domainLeft=\"%f\" domainRight=\"%f\" scale=\"%s\" type=\"%s\"", indent, name, domainLeft, domainRight, scale, type.getText()));

		if (type == Type.OUTPUT)
			str.append(String.format(" defaultValue=\"%f\" defuzzifier=\"%s\" accumulation=\"%s\"", defaultValue, defuzzifier, accumulation));

		str.append(">\n"); // close tag
		for (FuzzyTerm t : terms) {
			str.append(String.format("%s\n", t.toXMLString(indent + "\t")));
		}
		str.append(String.format("%s</FuzzyVariable>", indent, name));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		domainLeft = Double.parseDouble(attributes.getNamedItem("domainLeft").getTextContent());
		domainRight = Double.parseDouble(attributes.getNamedItem("domainRight").getTextContent());

		// load optional parameters
		if (attributes.getNamedItem("defaultValue") != null)
			defaultValue = Double.parseDouble(attributes.getNamedItem("defaultValue").getTextContent());

		if (attributes.getNamedItem("scale") != null)
			scale = attributes.getNamedItem("scale").getTextContent();

		if (attributes.getNamedItem("type") != null)
			type = Type.fromString(attributes.getNamedItem("type").getTextContent());

		if (attributes.getNamedItem("accumulation") != null)
			accumulation = Accumulation.fromString(attributes.getNamedItem("accumulation").getTextContent());

		if (attributes.getNamedItem("defuzzifier") != null)
			defuzzifier = Defuzzifier.fromString(attributes.getNamedItem("defuzzifier").getTextContent());

		terms = new ArrayList<>();
		NodeList children = ((Element) xmlNode).getElementsByTagName("FuzzyTerm");
		for (int i = 0; i < children.getLength(); i++) {
			terms.add(new FuzzyTerm(children.item(i)));
		}
	}

	public FuzzyTerm getTerm(String name){
		for(FuzzyTerm term : terms)
			if (term.getName().equals(name))
				return term;
		return null;
	}

	public double defuzzify(ActivationMethod activationMethod, KnowledgeBase knowledgeBase, List<Pair<Double, Clause>> terms){
		// iterate over the variable domain, with small intervals.
		// evaluate each term using the activation method with the Double
		// take the value and accumulate it on a list of points (x, y) to be passed to the defuzzify operator

		return 0;
	}

	enum Type {
		INPUT("input"), OUTPUT("output");

		private String text;

		Type(String text) {
			this.text = text;
		}

		public static Type fromString(String text) {
			Type result = null;
			for (Type b : Type.values()) {
				if (b.text.equalsIgnoreCase(text)) {
					result = b;
					break;
				}
			}
			return result;
		}

		public String getText() {
			return this.text;
		}
	}
}

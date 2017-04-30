package libai.fuzzy2;

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
	enum Accumulation {
		MAX("MAX"), SUM("SUM");
		private String text;

		Accumulation(String text) { this.text = text; }
		public String getText() { return this.text; }

		public static Accumulation fromString(String text) {
			Accumulation result = null;
			for (Accumulation b : Accumulation.values()) {
				if (b.text.equalsIgnoreCase(text)) {
					result = b;
					break;
				}
			}
			return result;
		}
	}
	enum Defuzzifier {
		MOM("MOM"), COG("COG"), COA("COA"), WA("WA"), Custom("Custom");
		private String text;

		Defuzzifier(String text) { this.text = text; }
		public String getText() { return this.text; }

		public static Defuzzifier fromString(String text) {
			Defuzzifier result = null;
			for (Defuzzifier b : Defuzzifier.values()) {
				if (b.text.equalsIgnoreCase(text)) {
					result = b;
					break;
				}
			}
			return result;
		}
	}
	enum Type {
		INPUT("input"), OUTPUT("output");

		private String text;

		Type(String text) { this.text = text; }
		public String getText() { return this.text; }

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
	}

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


	public FuzzyVariable(Node xmlNode) throws Exception {
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

		if(type == Type.OUTPUT)
			str.append(String.format(" defaultValue=\"%f\" defuzzifier=\"%s\" accumulation=\"%s\"", defaultValue, defuzzifier.getText(), accumulation.getText()));

		str.append(">\n"); // close tag
		for (FuzzyTerm t : terms) {
			str.append(String.format("%s\n", t.toXMLString(indent + "\t")));
		}
		str.append(String.format("%s</FuzzyVariable>", indent, name));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) throws Exception {
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		domainLeft = Double.parseDouble(attributes.getNamedItem("domainLeft").getTextContent());
		domainRight = Double.parseDouble(attributes.getNamedItem("domainRight").getTextContent());

		// load optional parameters
		if(attributes.getNamedItem("defaultValue") != null)
			defaultValue = Double.parseDouble(attributes.getNamedItem("defaultValue").getTextContent());

		if(attributes.getNamedItem("scale") != null)
			scale = attributes.getNamedItem("scale").getTextContent();

		if(attributes.getNamedItem("type") != null)
			type = Type.fromString(attributes.getNamedItem("type").getTextContent());

		if (attributes.getNamedItem("accumulation") != null)
			accumulation = Accumulation.fromString(attributes.getNamedItem("accumulation").getTextContent());

		if (attributes.getNamedItem("defuzzifier") != null)
			defuzzifier = Defuzzifier.fromString(attributes.getNamedItem("defuzzifier").getTextContent());

		terms = new ArrayList<>();
		NodeList children = ((Element)xmlNode).getElementsByTagName("FuzzyTerm");
		for(int i=0;i<children.getLength();i++){
			terms.add(new FuzzyTerm(children.item(i)));
		}
	}
}
package libai.fuzzy2;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class Rule implements XMLSerializer {
	enum Operator {
		PROD("PROD"), MIN("MIN"), PROBOR("PROBOR"), MAX("MAX");
		private String text;

		Operator(String text) { this.text = text; }
		public String getText() { return this.text; }

		public static Operator fromString(String text) {
			Operator result = null;
			for (Operator b : Operator.values()) {
				if (b.text.equalsIgnoreCase(text)) {
					result = b;
					break;
				}
			}
			return result;
		}
	}
	enum Connector {
		AND("AND"), OR("OR");
		private String text;

		Connector(String text) { this.text = text; }
		public String getText() { return this.text; }

		public static Connector fromString(String text) {
			Connector result = null;
			for (Connector b : Connector.values()) {
				if (b.text.equalsIgnoreCase(text)) {
					result = b;
					break;
				}
			}
			return result;
		}
	}

	protected Operator operator;
	protected String name;
	protected double weight;
	protected Connector connector = Connector.AND;
	protected Antecedent antecedent;
	protected Consequent consequent;

	public Rule(Node xmlNode) throws Exception {
		load(xmlNode);
	}

	public Rule(String name, double weight, Operator operator, Antecedent antecedent, Consequent consequent){
		this.name = name;
		this.weight = weight;
		this.operator = operator;
		this.antecedent = antecedent;
		this.consequent = consequent;
	}

	public Rule(String name, double weight, Operator operator, Connector connector, Antecedent antecedent, Consequent consequent){
		this(name, weight, operator, antecedent, consequent);
		this.connector = connector;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<Rule name=\"%s\" weight=\"%f\" operator=\"%s\" connector=\"%s\">\n", indent, name, weight, operator.getText(), connector.getText()));
		str.append(String.format("%s\n",antecedent.toXMLString(indent+"\t")));
		str.append(String.format("%s\n",consequent.toXMLString(indent+"\t")));
		str.append(String.format("%s</Rule>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) throws Exception {
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		weight = Double.parseDouble(attributes.getNamedItem("weight").getTextContent());
		operator = Operator.fromString(attributes.getNamedItem("operator").getTextContent());

		if (attributes.getNamedItem("connector") != null)
			connector = Connector.fromString(attributes.getNamedItem("connector").getTextContent());

		antecedent = new Antecedent(((Element)xmlNode).getElementsByTagName("Antecedent").item(0));
		consequent = new Consequent(((Element)xmlNode).getElementsByTagName("Consequent").item(0));
	}
}

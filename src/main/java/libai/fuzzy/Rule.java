package libai.fuzzy;

import libai.fuzzy.operators.AndMethod;
import libai.fuzzy.operators.Operator;
import libai.fuzzy.operators.OrMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class Rule implements XMLSerializer {
	private Operator operator;
	private String name;
	private double weight;
	private Connector connector = Connector.AND;
	private Antecedent antecedent;
	private Consequent consequent;

	public Rule(Node xmlNode) {
		load(xmlNode);
	}
	public Rule(String name, double weight, Operator operator, Antecedent antecedent, Consequent consequent) {
		this(name, weight, operator, Connector.AND, antecedent, consequent);
	}

	public Rule(String name, double weight, Operator operator, Connector connector, Antecedent antecedent, Consequent consequent) {
		if(connector == Connector.AND && !(operator instanceof AndMethod))
			throw new IllegalArgumentException("Operator must be an instance of AndMethod");

		if(connector == Connector.OR && !(operator instanceof OrMethod))
			throw new IllegalArgumentException("Operator must be an instance of OrMethod");

		this.name = name;
		this.weight = weight;
		this.operator = operator;
		this.antecedent = antecedent;
		this.consequent = consequent;
		this.connector = connector;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<Rule name=\"%s\" weight=\"%f\" operator=\"%s\" connector=\"%s\">\n", indent, name, weight, operator, connector.getText()));
		str.append(String.format("%s\n", antecedent.toXMLString(indent + "\t")));
		str.append(String.format("%s\n", consequent.toXMLString(indent + "\t")));
		str.append(String.format("%s</Rule>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		weight = Double.parseDouble(attributes.getNamedItem("weight").getTextContent());
		operator = Operator.fromString(attributes.getNamedItem("operator").getTextContent());

		if (attributes.getNamedItem("connector") != null)
			connector = Connector.fromString(attributes.getNamedItem("connector").getTextContent());

		antecedent = new Antecedent(((Element) xmlNode).getElementsByTagName("Antecedent").item(0));
		consequent = new Consequent(((Element) xmlNode).getElementsByTagName("Consequent").item(0));
	}

	public double getActivationValue(Map<String, Double> variables, KnowledgeBase knowledgeBase){
		return antecedent.activate(variables, knowledgeBase, operator);
	}

	public Iterable<Clause> getConsequentClauses() {
		return consequent;
	}

	enum Connector {
		AND("AND"), OR("OR");
		private String text;

		Connector(String text) {
			this.text = text;
		}

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

		public String getText() {
			return this.text;
		}
	}
}

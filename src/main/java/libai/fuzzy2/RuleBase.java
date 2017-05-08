package libai.fuzzy2;

import libai.fuzzy2.operators.ActivationMethod;
import libai.fuzzy2.operators.AndMethod;
import libai.fuzzy2.operators.Operator;
import libai.fuzzy2.operators.OrMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class RuleBase implements XMLSerializer {
	protected String name;
	// TODO: what's the point of the and/or methods if the rules define what operator to be used?
	protected Operator activationMethod = ActivationMethod.MIN;
	protected Operator andMethod = AndMethod.MIN;
	protected Operator orMethod = OrMethod.MAX;
	protected List<Rule> rules = new ArrayList<>();

	public RuleBase(Node xmlNode) {
		load(xmlNode);
	}
	public RuleBase(String name, Rule... rules) {
		this.name = name;
		this.rules = Arrays.asList(rules);
	}
	public RuleBase(String name, Operator activationMethod, Rule... rules) {
		this(name, rules);
		this.activationMethod = activationMethod;
	}

	public RuleBase(String name, Operator activationMethod, Operator andMethod, Operator orMethod, Rule... rules) {
		this(name, activationMethod, rules);
		this.andMethod = andMethod;
		this.orMethod = orMethod;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<RuleBase name=\"%s\" type=\"mamdani\" activationMethod=\"%s\" andMethod=\"%s\" orMethod=\"%s\">\n", indent, name, activationMethod, andMethod, orMethod));
		for (Rule r : rules) {
			str.append(String.format("%s\n", r.toXMLString(indent + "\t")));
		}
		str.append(String.format("%s</RuleBase>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		//throw unsupported exception if type is not mamdani
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();
		String type = attributes.getNamedItem("type").getTextContent();
		if ("tsk".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TSK is not supported yet");

		if (attributes.getNamedItem("activationMethod") != null)
			activationMethod = ActivationMethod.fromString(attributes.getNamedItem("activationMethod").getTextContent());

		if (attributes.getNamedItem("andMethod") != null)
			andMethod = AndMethod.fromString(attributes.getNamedItem("andMethod").getTextContent());

		if (attributes.getNamedItem("orMethod") != null)
			orMethod = OrMethod.fromString(attributes.getNamedItem("orMethod").getTextContent());

		NodeList children = ((Element) xmlNode).getElementsByTagName("Rule");
		for (int i = 0; i < children.getLength(); i++) {
			rules.add(new Rule(children.item(i)));
		}
	}

	public Map<String, Double> fire(Map<String, Double> variables, KnowledgeBase knowledgeBase){
		//has to pass down both operator implementations and the activation method.
		return new HashMap<>();
	}
}

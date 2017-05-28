package libai.fuzzy;

import libai.common.Pair;
import libai.fuzzy.operators.activation.ActivationMethod;
import libai.fuzzy.operators.AndMethod;
import libai.fuzzy.operators.Operator;
import libai.fuzzy.operators.OrMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class RuleBase implements XMLSerializer {
	private String name;
	private ActivationMethod activationMethod = ActivationMethod.MIN; // implication method
	private Operator andMethod = AndMethod.MIN; //operator to be used as default (mostly for systems that edit the files)
	private Operator orMethod = OrMethod.MAX; //operator to be used as default (mostly for systems that edit the files)
	private List<Rule> rules = new ArrayList<>();

	public RuleBase(Node xmlNode) {
		load(xmlNode);
	}
	public RuleBase(String name, Rule... rules) {
		this.name = name;
		this.rules = Arrays.asList(rules);
	}
	public RuleBase(String name, ActivationMethod activationMethod, Rule... rules) {
		this(name, rules);
		this.activationMethod = activationMethod;
	}

	public RuleBase(String name, ActivationMethod activationMethod, Operator andMethod, Operator orMethod, Rule... rules) {
		this(name, activationMethod, rules);
		this.andMethod = andMethod;
		this.orMethod = orMethod;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<RuleBase name=\"%s\" type=\"mamdani\" activationMethod=\"%s\" andMethod=\"%s\" orMethod=\"%s\">\n", indent, getName(), activationMethod, andMethod, orMethod));
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

	public Map<String, Double> fire(Map<String, Double> variables, KnowledgeBase knowledgeBase, double delta){
		Map<String, List<Pair<Double, Clause>>> outputVariables = new HashMap<>();

		for(Rule r : rules){
			double tau = r.getActivationValue(variables, knowledgeBase);

			for(Clause clause : r.getConsequentClauses()){
				String variableName = clause.getVariableName();
				if (outputVariables.get(variableName) == null)
					outputVariables.put(variableName, new ArrayList<>());
				outputVariables.get(variableName).add(new Pair<>(tau, clause));
			}
		}

		Map<String, Double> result = new HashMap<>();
		for(String variableName : outputVariables.keySet()){
			FuzzyVariable variable = knowledgeBase.getVariable(variableName);
			double value = variable.defuzzify(activationMethod, knowledgeBase, delta, outputVariables.get(variableName));
			result.put(variableName, value);
		}

		return result;
	}

	public String getName() {
		return name;
	}
}

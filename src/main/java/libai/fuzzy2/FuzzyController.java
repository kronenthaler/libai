package libai.fuzzy2;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class FuzzyController implements XMLSerializer {
	private String name;
	private String ip = "127.0.0.1"; // ip address
	private KnowledgeBase knowledgeBase;
	private RuleBase ruleBase;

	public FuzzyController(Node xmlNode){
		load(xmlNode);
	}

	public FuzzyController(String name, KnowledgeBase kb, RuleBase rb){
		this.name = name;
		this.knowledgeBase = kb;
		this.ruleBase = rb;
	}

	public FuzzyController(String name, String ip, KnowledgeBase kb, RuleBase rb){
		this(name, kb, rb);
		this.ip = ip;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<FuzzyController name=\"%s\" ip=\"%s\">\n", indent, getName(), getIpAddress()));
		str.append(String.format("%s\n", knowledgeBase.toXMLString(indent + "\t")));
		str.append(String.format("%s\n", ruleBase.toXMLString(indent + "\t")));
		str.append(String.format("%s</FuzzyController>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NamedNodeMap attributes = xmlNode.getAttributes();
		name = attributes.getNamedItem("name").getTextContent();

		if(attributes.getNamedItem("ip")!=null)
			ip = attributes.getNamedItem("ip").getTextContent();

		knowledgeBase = new KnowledgeBase(((Element)xmlNode).getElementsByTagName("KnowledgeBase").item(0));
		ruleBase = new RuleBase(((Element)xmlNode).getElementsByTagName("RuleBase").item(0));
	}

	/**
	 * Fires the rule's antecedents, with the given variable values.
	 * @param variables Map containing the variable name and its current value at the moment the rules are going to be evaluated.
	 * @return A map with all output variables activated and their corresponding value.
	 **/
	public Map<String, Double> fire(Map<String, Double> variables){
		return ruleBase.fire(variables, knowledgeBase);
	}

	public String getName() {
		return name;
	}

	public String getIpAddress() {
		return ip;
	}
}

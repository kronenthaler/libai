package libai.fuzzy2;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class FuzzyController implements XMLSerializer {
	protected String name;
	protected String ip = "127.0.0.1"; // ip address
	protected KnowledgeBase knowledgeBase;
	protected RuleBase ruleBase;

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
		str.append(String.format("%s<FuzzyController name=\"%s\" ip=\"%s\">\n", indent, name, ip));
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
}

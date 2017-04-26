package libai.fuzzy2;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kronenthaler on 26/04/2017.
 */
public class KnowledgeBase implements XMLSerializer {
	protected Map<String, FuzzyVariable> variables = new HashMap<>();

	public KnowledgeBase(Node xmlNode) throws Exception {
		load(xmlNode);
	}

	public KnowledgeBase(FuzzyVariable... variables){
		for(FuzzyVariable var : variables) {
			this.variables.put(var.name, var);
		}
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<KnowledgeBase>\n", indent));

		for(FuzzyVariable var : variables.values()){
			str.append(String.format("%s\n", var.toXMLString(indent+"\t")));
		}

		str.append(String.format("%s</KnowledgeBase>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) throws Exception {
		NodeList children = xmlNode.getChildNodes();
		for(int i=0; i < children.getLength(); i++){
			Node current = children.item(i);
			if (current.getNodeType() != Node.ELEMENT_NODE)
				continue;

			FuzzyVariable var = new FuzzyVariable(current);
			variables.put(var.name, var);
		}
	}
}

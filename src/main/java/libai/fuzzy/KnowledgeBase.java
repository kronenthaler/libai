package libai.fuzzy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kronenthaler on 26/04/2017.
 */
public class KnowledgeBase implements XMLSerializer {
	private Map<String, FuzzyVariable> variables = new HashMap<>();

	public KnowledgeBase(Node xmlNode) {
		load(xmlNode);
	}

	public KnowledgeBase(FuzzyVariable... variables) {
		for (FuzzyVariable var : variables) {
			this.variables.put(var.name, var);
		}
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<KnowledgeBase>\n", indent));

		for (FuzzyVariable var : variables.values()) {
			str.append(String.format("%s\n", var.toXMLString(indent + "\t")));
		}

		str.append(String.format("%s</KnowledgeBase>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NodeList children = ((Element) xmlNode).getElementsByTagName("FuzzyVariable");
		for (int i = 0; i < children.getLength(); i++) {
			FuzzyVariable var = new FuzzyVariable(children.item(i));
			variables.put(var.name, var);
		}
	}

	public FuzzyVariable getVariable(String name) {
		return variables.get(name);
	}

	public FuzzyTerm getTerm(String variableName, String termName){
		FuzzyVariable variable = getVariable(variableName);
		return variable.getTerm(termName);
	}
}

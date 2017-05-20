package libai.fuzzy2;

import libai.fuzzy2.operators.Operator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class Antecedent implements XMLSerializer {
	private List<Clause> clauses = new ArrayList<>();

	public Antecedent(Node xmlNode) {
		load(xmlNode);
	}

	public Antecedent(Clause... clauses) {
		this.clauses = Arrays.asList(clauses);
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<Antecedent>\n", indent));

		for (Clause var : clauses) {
			str.append(String.format("%s\n", var.toXMLString(indent + "\t")));
		}

		str.append(String.format("%s</Antecedent>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NodeList children = ((Element) xmlNode).getElementsByTagName("Clause");
		for (int i = 0; i < children.getLength(); i++) {
			clauses.add(new Clause(children.item(i)));
		}
	}

	public double activate(Map<String, Double> variables, KnowledgeBase knowledgeBase, Operator connector){
		double result = connector.neutral();

		for(Clause clause : clauses) {
			String variableName = clause.getVariableName();
			double value = clause.eval(variables.get(variableName), knowledgeBase);
			result = connector.eval(result, value);
		}

		return result;
	}
}

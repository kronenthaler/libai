package libai.fuzzy;

import libai.fuzzy.modifiers.Modifier;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 27/04/2017.
 */
public class Clause implements XMLSerializer {
	private Modifier modifier;
	private String variable; //variable name
	private String term; //term name

	public Clause(Node xmlNode) {
		load(xmlNode);
	}

	public Clause(String variable, String term) {
		this.variable = variable;
		this.term = term;
	}

	public Clause(String variable, String term, Modifier modifier) {
		this(variable, term);
		this.modifier = modifier;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuffer str = new StringBuffer();
		str.append(String.format("%s<Clause%s>\n", indent, modifier == null ? "" : " modifier=\"" + modifier + "\""));
		str.append(String.format("%s\t<Variable>%s</Variable>\n", indent, variable));
		str.append(String.format("%s\t<Term>%s</Term>\n", indent, term));
		str.append(String.format("%s</Clause>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		if (xmlNode.getAttributes().getNamedItem("modifier") != null)
			modifier = Modifier.fromString(xmlNode.getAttributes().getNamedItem("modifier").getTextContent());

		if (xmlNode instanceof Element) {
			variable = ((Element) xmlNode).getElementsByTagName("Variable").item(0).getTextContent();
			term = ((Element) xmlNode).getElementsByTagName("Term").item(0).getTextContent();
		}
	}

	public String getVariableName() {
		return variable;
	}

	public String getTermName() {
		return term;
	}

	public double eval(double input, KnowledgeBase knowledgeBase){
		FuzzyTerm term = knowledgeBase.getTerm(getVariableName(), getTermName());

		double value = term.eval(input);
		if (modifier != null)
			value = modifier.eval(value);

		return  value;
	}
}

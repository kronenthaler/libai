package libai.fuzzy2;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 27/04/2017.
 */
public class Clause implements XMLSerializer {
	protected Modifier modifier;
	protected String variable; //variable name
	protected String term; //term name
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
		str.append(String.format("%s<Clause%s>\n", indent, modifier == null ? "" : " modifier=\"" + modifier.getText() + "\""));
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

	enum Modifier {
		ABOVE("above"), BELOW("below"), EXTREMELY("extremely"), INTENSIFY("intensify"), MORE_OR_LESS("more_or_less"),
		NORM("norm"), NOT("not"), PLUS("plus"), SLIGHTLY("slightly"), SOMEWHAT("somewhat"), VERY("very");

		private String text;

		Modifier(String text) {
			this.text = text;
		}

		public static Modifier fromString(String text) {
			Modifier result = null;
			for (Modifier b : Modifier.values()) {
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

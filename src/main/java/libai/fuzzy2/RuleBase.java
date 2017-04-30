package libai.fuzzy2;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class RuleBase implements XMLSerializer {
	protected String name;
	protected ActivationMethod activationMethod = ActivationMethod.MIN;
	protected AndMethod andMethod = AndMethod.MIN;
	protected OrMethod orMethod = OrMethod.MAX;
	protected List<Rule> rules = new ArrayList<>();
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

	public RuleBase(String name, ActivationMethod activationMethod, AndMethod andMethod, OrMethod orMethod, Rule... rules) {
		this(name, activationMethod, rules);
		this.andMethod = andMethod;
		this.orMethod = orMethod;
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<RuleBase name=\"%s\" type=\"mandani\" activationMethod=\"%s\" andMethod=\"%s\" orMethod=\"%s\">\n", indent, name, activationMethod.getText(), andMethod.getText(), orMethod.getText()));
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

	enum ActivationMethod {
		PROD("PROD"), MIN("MIN");
		private String text;

		ActivationMethod(String text) {
			this.text = text;
		}

		public static ActivationMethod fromString(String text) {
			ActivationMethod result = null;
			for (ActivationMethod b : ActivationMethod.values()) {
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

	enum AndMethod {
		PROD("PROD"), MIN("MIN");
		private String text;

		AndMethod(String text) {
			this.text = text;
		}

		public static AndMethod fromString(String text) {
			AndMethod result = null;
			for (AndMethod b : AndMethod.values()) {
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

	enum OrMethod {
		PROBOR("PROBOR"), MAX("MAX");
		private String text;

		OrMethod(String text) {
			this.text = text;
		}

		public static OrMethod fromString(String text) {
			OrMethod result = null;
			for (OrMethod b : OrMethod.values()) {
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

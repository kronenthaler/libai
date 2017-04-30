package libai.fuzzy2;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class RuleBaseTest {
	@Test
	public void testXMLGeneration() {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule ruleA = new Rule("tipper", 1, Rule.Operator.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, Rule.Operator.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase ruleBase = new RuleBase("rulebase", RuleBase.ActivationMethod.MIN, RuleBase.AndMethod.PROD, RuleBase.OrMethod.PROBOR, ruleA, ruleB);

		assertEquals("<RuleBase name=\"rulebase\" type=\"mandani\" activationMethod=\"MIN\" andMethod=\"PROD\" orMethod=\"PROBOR\">\n" +
				"\t<Rule name=\"tipper\" weight=\"1.000000\" operator=\"PROBOR\" connector=\"OR\">\n" +
				"\t\t<Antecedent>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable1</Variable>\n" +
				"\t\t\t\t<Term>good</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable2</Variable>\n" +
				"\t\t\t\t<Term>big</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t</Antecedent>\n" +
				"\t\t<Consequent>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable3</Variable>\n" +
				"\t\t\t\t<Term>bad</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable4</Variable>\n" +
				"\t\t\t\t<Term>small</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t</Consequent>\n" +
				"\t</Rule>\n" +
				"\t<Rule name=\"whatever\" weight=\"1.000000\" operator=\"MIN\" connector=\"AND\">\n" +
				"\t\t<Antecedent>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable1</Variable>\n" +
				"\t\t\t\t<Term>good</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable2</Variable>\n" +
				"\t\t\t\t<Term>big</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t</Antecedent>\n" +
				"\t\t<Consequent>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable3</Variable>\n" +
				"\t\t\t\t<Term>bad</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t\t<Clause>\n" +
				"\t\t\t\t<Variable>variable4</Variable>\n" +
				"\t\t\t\t<Term>small</Term>\n" +
				"\t\t\t</Clause>\n" +
				"\t\t</Consequent>\n" +
				"\t</Rule>\n" +
				"</RuleBase>", ruleBase.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule ruleA = new Rule("tipper", 1, Rule.Operator.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, Rule.Operator.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase ruleBase = new RuleBase("rulebase", RuleBase.ActivationMethod.MIN, RuleBase.AndMethod.PROD, RuleBase.OrMethod.PROBOR, ruleA, ruleB);
		String xml = ruleBase.toXMLString("");

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		RuleBase newRuleBase = new RuleBase(root);
		assertEquals(ruleBase.toXMLString(""), newRuleBase.toXMLString(""));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testTSKSupport() throws ParserConfigurationException, IOException, SAXException {
		String xml = "<RuleBase name=\"x\" type=\"tsk\"/>";

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		RuleBase newRuleBase = new RuleBase(root);
	}
}

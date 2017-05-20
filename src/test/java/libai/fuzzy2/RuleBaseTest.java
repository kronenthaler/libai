package libai.fuzzy2;

import libai.fuzzy2.defuzzifiers.Defuzzifier;
import libai.fuzzy2.operators.Operator;
import libai.fuzzy2.operators.accumulation.Accumulation;
import libai.fuzzy2.operators.activation.ActivationMethod;
import libai.fuzzy2.operators.AndMethod;
import libai.fuzzy2.operators.OrMethod;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

		Rule ruleA = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, AndMethod.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase ruleBase = new RuleBase("rulebase", ActivationMethod.MIN, AndMethod.PROD, OrMethod.PROBOR, ruleA, ruleB);

		assertEquals("<RuleBase name=\"rulebase\" type=\"mamdani\" activationMethod=\"MIN\" andMethod=\"PROD\" orMethod=\"PROBOR\">\n" +
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

		Rule ruleA = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, AndMethod.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase ruleBase = new RuleBase("rulebase", ActivationMethod.MIN, AndMethod.PROD, OrMethod.PROBOR, ruleA, ruleB);
		String xml = ruleBase.toXMLString("");

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

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		new RuleBase(root);
	}

	@Test
	public void testFireRules(){
		FuzzyTerm dry = new FuzzyTerm(new TriangularShape(0,2.5, 5), "dry");
		FuzzyTerm normal = new FuzzyTerm(new TriangularShape(2.5,5, 7.5), "normal");
		FuzzyTerm wet = new FuzzyTerm(new TriangularShape(5,7.5, 10), "wet");
		FuzzyVariable dryness = new FuzzyVariable("dryness", 0, 10, "", dry, normal, wet);

		FuzzyTerm dark = new FuzzyTerm(new TriangularShape(2,4, 6), "dark");
		FuzzyTerm light = new FuzzyTerm(new TriangularShape(0,2,4), "light");
		FuzzyVariable lighting = new FuzzyVariable("lighting", 0, 6, "", dark, light);

		FuzzyTerm off = new FuzzyTerm(new TriangularShape(1,2,3), "off");
		FuzzyTerm on = new FuzzyTerm(new TriangularShape(0,1,2), "on");
		FuzzyVariable alarm = new FuzzyVariable("alarm", 0, 3, 0, "", Accumulation.SUM, Defuzzifier.COG, on, off);

		FuzzyTerm _long = new FuzzyTerm(new TriangularShape(2,4,6), "long");
		FuzzyTerm none = new FuzzyTerm(new TriangularShape(0,0,3), "none");
		FuzzyTerm _short = new FuzzyTerm(new TriangularShape(0,2,4), "short");
		FuzzyVariable sprinkles = new FuzzyVariable("sprinkles", 0, 6, 0, "", Accumulation.MAX, Defuzzifier.MOM, _long, none, _short);

		KnowledgeBase kb = new KnowledgeBase(dryness, lighting, alarm, sprinkles);

		Antecedent a1 = new Antecedent(new Clause("lighting", "dark"), new Clause("dryness", "normal"));
		Consequent c1 = new Consequent(new Clause("sprinkles", "short"), new Clause("alarm", "off"));
		Rule r1 = new Rule("rule1", 1, AndMethod.PROD, a1, c1);

		Antecedent a2 = new Antecedent(new Clause("lighting", "dark"), new Clause("dryness", "dry"));
		Consequent c2 = new Consequent(new Clause("sprinkles", "long"), new Clause("alarm", "on"));
		Rule r2 = new Rule("rule2", 1, AndMethod.MIN, a2, c2);

		Antecedent a3 = new Antecedent(new Clause("lighting", "light"), new Clause("dryness", "wet"));
		Consequent c3 = new Consequent(new Clause("sprinkles", "none"), new Clause("alarm", "off"));
		Rule r3 = new Rule("rule3", 1, OrMethod.MAX, Rule.Connector.OR, a3, c3);

		Antecedent a4 = new Antecedent(new Clause("lighting", "light"), new Clause("dryness", "dry"));
		Consequent c4 = new Consequent(new Clause("sprinkles", "long"), new Clause("alarm", "on"));
		Rule r4 = new Rule("rule4", 1, AndMethod.MIN, a4, c4);

		RuleBase rb = new RuleBase("RB1", ActivationMethod.PROD, r1, r2, r3, r4);

		Map<String, Double> vars = new HashMap<>();
		vars.put("lighting", 3.);
		vars.put("dryness", 4.35);
		Map<String, Double> adjusment = rb.fire(vars, kb);

		assertEquals(1.625, adjusment.get("alarm"), 1.e-3);
		assertEquals(0, adjusment.get("sprinkles"), 1.e-3);
	}
}

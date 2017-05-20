package libai.fuzzy2;

import libai.fuzzy2.defuzzifiers.Defuzzifier;
import libai.fuzzy2.operators.activation.ActivationMethod;
import libai.fuzzy2.operators.AndMethod;
import libai.fuzzy2.operators.OrMethod;
import libai.fuzzy2.operators.accumulation.Accumulation;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class FuzzyControllerTest {

	@Test
	public void testXMLGeneration(){
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", Accumulation.SUM, Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);

		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule ruleA = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, AndMethod.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase rb = new RuleBase("rulebase", ActivationMethod.MIN, AndMethod.PROD, OrMethod.PROBOR, ruleA, ruleB);
		FuzzyController fc = new FuzzyController("deController", "home.localhost", kb, rb);

		assertEquals("<FuzzyController name=\"deController\" ip=\"home.localhost\">\n" +
				"\t<KnowledgeBase>\n" +
				"\t\t<FuzzyVariable name=\"tip\" domainLeft=\"0.000000\" domainRight=\"10.000000\" scale=\"percentage\" type=\"output\" defaultValue=\"5.000000\" defuzzifier=\"MOM\" accumulation=\"SUM\">\n" +
				"\t\t\t<FuzzyTerm name=\"cheap\" complement=\"false\">\n" +
				"\t\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"3.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t\t</FuzzyTerm>\n" +
				"\t\t\t<FuzzyTerm name=\"generous\" complement=\"false\">\n" +
				"\t\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"7.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t\t</FuzzyTerm>\n" +
				"\t\t</FuzzyVariable>\n" +
				"\t\t<FuzzyVariable name=\"quality\" domainLeft=\"0.000000\" domainRight=\"10.000000\" scale=\"stars\" type=\"input\">\n" +
				"\t\t\t<FuzzyTerm name=\"bad\" complement=\"false\">\n" +
				"\t\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"3.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t\t</FuzzyTerm>\n" +
				"\t\t\t<FuzzyTerm name=\"good\" complement=\"false\">\n" +
				"\t\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"7.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t\t</FuzzyTerm>\n" +
				"\t\t</FuzzyVariable>\n" +
				"\t</KnowledgeBase>\n" +
				"\t<RuleBase name=\"rulebase\" type=\"mamdani\" activationMethod=\"MIN\" andMethod=\"PROD\" orMethod=\"PROBOR\">\n" +
				"\t\t<Rule name=\"tipper\" weight=\"1.000000\" operator=\"PROBOR\" connector=\"OR\">\n" +
				"\t\t\t<Antecedent>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable1</Variable>\n" +
				"\t\t\t\t\t<Term>good</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable2</Variable>\n" +
				"\t\t\t\t\t<Term>big</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t</Antecedent>\n" +
				"\t\t\t<Consequent>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable3</Variable>\n" +
				"\t\t\t\t\t<Term>bad</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable4</Variable>\n" +
				"\t\t\t\t\t<Term>small</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t</Consequent>\n" +
				"\t\t</Rule>\n" +
				"\t\t<Rule name=\"whatever\" weight=\"1.000000\" operator=\"MIN\" connector=\"AND\">\n" +
				"\t\t\t<Antecedent>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable1</Variable>\n" +
				"\t\t\t\t\t<Term>good</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable2</Variable>\n" +
				"\t\t\t\t\t<Term>big</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t</Antecedent>\n" +
				"\t\t\t<Consequent>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable3</Variable>\n" +
				"\t\t\t\t\t<Term>bad</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t\t<Clause>\n" +
				"\t\t\t\t\t<Variable>variable4</Variable>\n" +
				"\t\t\t\t\t<Term>small</Term>\n" +
				"\t\t\t\t</Clause>\n" +
				"\t\t\t</Consequent>\n" +
				"\t\t</Rule>\n" +
				"\t</RuleBase>\n" +
				"</FuzzyController>", fc.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception{
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", Accumulation.SUM, Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);

		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule ruleA = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);
		Rule ruleB = new Rule("whatever", 1, AndMethod.MIN, Rule.Connector.AND, antecedent, consequent);

		RuleBase rb = new RuleBase("rulebase", ActivationMethod.MIN, AndMethod.PROD, OrMethod.PROBOR, ruleA, ruleB);
		FuzzyController fc = new FuzzyController("deController", "home.localhost", kb, rb);
		String xml = fc.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzyController newFc = new FuzzyController(root);
		assertEquals(fc.toXMLString(""), newFc.toXMLString(""));
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

		FuzzyController controller = new FuzzyController("system", kb, rb);

		Map<String, Double> vars = new HashMap<>();
		vars.put("lighting", 3.);
		vars.put("dryness", 4.35);
		Map<String, Double> adjusment = controller.fire(vars);

		assertEquals(1.625, adjusment.get("alarm"), 1.e-3);
		assertEquals(0, adjusment.get("sprinkles"), 1.e-3);
	}
}

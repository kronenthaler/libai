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
}

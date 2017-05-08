package libai.fuzzy2;

import libai.fuzzy2.operators.Operator;
import libai.fuzzy2.operators.OrMethod;
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
public class RuleTest {
	@Test
	public void testXMLGeneration() {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule rule = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);

		assertEquals("<Rule name=\"tipper\" weight=\"1.000000\" operator=\"PROBOR\" connector=\"OR\">\n" +
				"\t<Antecedent>\n" +
				"\t\t<Clause>\n" +
				"\t\t\t<Variable>variable1</Variable>\n" +
				"\t\t\t<Term>good</Term>\n" +
				"\t\t</Clause>\n" +
				"\t\t<Clause>\n" +
				"\t\t\t<Variable>variable2</Variable>\n" +
				"\t\t\t<Term>big</Term>\n" +
				"\t\t</Clause>\n" +
				"\t</Antecedent>\n" +
				"\t<Consequent>\n" +
				"\t\t<Clause>\n" +
				"\t\t\t<Variable>variable3</Variable>\n" +
				"\t\t\t<Term>bad</Term>\n" +
				"\t\t</Clause>\n" +
				"\t\t<Clause>\n" +
				"\t\t\t<Variable>variable4</Variable>\n" +
				"\t\t\t<Term>small</Term>\n" +
				"\t\t</Clause>\n" +
				"\t</Consequent>\n" +
				"</Rule>", rule.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		Clause c = new Clause("variable3", "bad");
		Clause d = new Clause("variable4", "small");
		Consequent consequent = new Consequent(c, d);

		Rule rule = new Rule("tipper", 1, OrMethod.PROBOR, Rule.Connector.OR, antecedent, consequent);
		String xml = rule.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		Rule newRule = new Rule(root);
		assertEquals(rule.toXMLString(""), newRule.toXMLString(""));
	}
}

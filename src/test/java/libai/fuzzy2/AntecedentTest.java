package libai.fuzzy2;

import libai.fuzzy2.defuzzifiers.Defuzzifier;
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
public class AntecedentTest {
	@Test
	public void testXMLGeneration() {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);

		assertEquals("<Antecedent>\n" +
				"\t<Clause>\n" +
				"\t\t<Variable>variable1</Variable>\n" +
				"\t\t<Term>good</Term>\n" +
				"\t</Clause>\n" +
				"\t<Clause>\n" +
				"\t\t<Variable>variable2</Variable>\n" +
				"\t\t<Term>big</Term>\n" +
				"\t</Clause>\n" +
				"</Antecedent>", antecedent.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Antecedent antecedent = new Antecedent(a, b);
		String xml = antecedent.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		Antecedent newAntecedent = new Antecedent(root);
		assertEquals(antecedent.toXMLString(""), newAntecedent.toXMLString(""));
	}

	@Test
	public void testActivationWithSingleClause(){
		Clause a = new Clause("quality", "good");
		Antecedent antecedent = new Antecedent(a);

		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", Accumulation.SUM, Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);

		Map<String, Double> vars = new HashMap<>();
		vars.put("quality", 3.);
		vars.put("tip", 6.);

		assertEquals(3/7., antecedent.activate(vars, kb, AndMethod.MIN), 1.e-5);
		assertEquals(3/7., antecedent.activate(vars, kb, AndMethod.PROD),1.e-5);
		assertEquals(3/7., antecedent.activate(vars, kb, OrMethod.MAX), 1.e-5);
		assertEquals(3/7., antecedent.activate(vars, kb, OrMethod.PROBOR), 1.e-5);
	}

	@Test
	public void testActivationWithMultipleClause(){
		Clause a = new Clause("quality", "good");
		Clause b = new Clause("tip", "cheap");
		Antecedent antecedent = new Antecedent(a, b);

		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", Accumulation.SUM, Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);

		Map<String, Double> vars = new HashMap<>();
		vars.put("quality", 3.);
		vars.put("tip", 6.); // 10/13.

		assertEquals(Math.min(3/7., 4/7.), antecedent.activate(vars, kb, AndMethod.MIN), 1.e-5);
		assertEquals((3/7. * 4/7.), antecedent.activate(vars, kb, AndMethod.PROD),1.e-5);
		assertEquals(Math.max(3/7., 4/7.), antecedent.activate(vars, kb, OrMethod.MAX), 1.e-5);
		assertEquals(((3/7.+4/7.)-(3/7.*4/7.)), antecedent.activate(vars, kb, OrMethod.PROBOR), 1.e-5);
	}
}
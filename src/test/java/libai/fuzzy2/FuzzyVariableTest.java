package libai.fuzzy2;

import libai.common.Pair;
import libai.fuzzy2.defuzzifiers.Defuzzifier;
import libai.fuzzy2.operators.accumulation.Accumulation;
import libai.fuzzy2.operators.activation.ActivationMethod;
import libai.fuzzy2.sets.FuzzySet;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 26/04/2017.
 */
public class FuzzyVariableTest {
	@Test
	public void testXMLGeneration() {
		FuzzyTerm bad = new FuzzyTerm(new FuzzySet() {
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return String.format("%s<SingletonShape Param1=\"%d\"/>", indent, 3);
			}

			@Override
			public void load(Node xml) {
				throw new UnsupportedOperationException("");
			}
		}, "bad");

		FuzzyTerm good = new FuzzyTerm(new FuzzySet() {
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return String.format("%s<SingletonShape Param1=\"%d\"/>", indent, 7);
			}

			@Override
			public void load(Node xml) {
				throw new UnsupportedOperationException("");
			}
		}, "good");

		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		assertEquals("<FuzzyVariable name=\"quality\" domainLeft=\"0.000000\" domainRight=\"10.000000\" scale=\"stars\" type=\"input\">\n" +
				"\t<FuzzyTerm name=\"bad\" complement=\"false\">\n" +
				"\t\t<SingletonShape Param1=\"3\"/>\n" +
				"\t</FuzzyTerm>\n" +
				"\t<FuzzyTerm name=\"good\" complement=\"false\">\n" +
				"\t\t<SingletonShape Param1=\"7\"/>\n" +
				"\t</FuzzyTerm>\n" +
				"</FuzzyVariable>", var.toXMLString(""));
	}

	@Test
	public void testXMLConstructorInput() throws Exception {
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);
		String xml = var.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzyVariable newVar = new FuzzyVariable(root);
		assertEquals(var.toXMLString(""), newVar.toXMLString(""));
	}

	@Test
	public void testXMLConstructorOutput() throws Exception {
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, 5, "stars", Accumulation.SUM, Defuzzifier.MOM, bad, good);
		String xml = var.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzyVariable newVar = new FuzzyVariable(root);
		assertEquals(var.toXMLString(""), newVar.toXMLString(""));
	}

	@Test
	public void testDeffuzify(){
		FuzzyTerm off = new FuzzyTerm(new TriangularShape(1,2,3), "off");
		FuzzyTerm on = new FuzzyTerm(new TriangularShape(0,1,2), "on");
		FuzzyVariable alarm = new FuzzyVariable("alarm", 0, 3, 0, "", Accumulation.SUM, Defuzzifier.COG, on, off);

		FuzzyTerm _long = new FuzzyTerm(new TriangularShape(2,4,6), "long");
		FuzzyTerm none = new FuzzyTerm(new TriangularShape(0,0,3), "none");
		FuzzyTerm _short = new FuzzyTerm(new TriangularShape(0,2,4), "short");
		FuzzyVariable sprinkles = new FuzzyVariable("sprinkles", 0, 6, 0, "", Accumulation.MAX, Defuzzifier.MOM, _long, none, _short);

		KnowledgeBase kb = new KnowledgeBase(alarm, sprinkles);

		List<Pair<Double, Clause>> clauses = new ArrayList<>();
		clauses.add(new Pair<>(0.25, new Clause("alarm", "on")));
		clauses.add(new Pair<>(0.5, new Clause("alarm", "off")));
		clauses.add(new Pair<>(0.25, new Clause("alarm", "on")));
		clauses.add(new Pair<>(0.375, new Clause("alarm", "off")));

		double alarmValue = alarm.defuzzify(ActivationMethod.MIN, kb, clauses);
		assertEquals(1.608, alarmValue, 1.e-3);

		clauses = new ArrayList<>();
		clauses.add(new Pair<>(0.25, new Clause("sprinkles", "long")));
		clauses.add(new Pair<>(0.5, new Clause("sprinkles", "none")));
		clauses.add(new Pair<>(0.25, new Clause("sprinkles", "long")));
		clauses.add(new Pair<>(0.375, new Clause("sprinkles", "short")));

		double sprinklesValue = sprinkles.defuzzify(ActivationMethod.MIN, kb, clauses);
		assertEquals(0.745, sprinklesValue, 1.e-3);
	}
}

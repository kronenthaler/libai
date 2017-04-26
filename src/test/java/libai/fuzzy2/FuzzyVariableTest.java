package libai.fuzzy2;

import junit.framework.TestCase;
import libai.fuzzy2.sets.FuzzySet;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by kronenthaler on 26/04/2017.
 */
public class FuzzyVariableTest extends TestCase {
	@Test
	public void testXMLGeneration() {
		FuzzyTerm bad = new FuzzyTerm(new FuzzySet(){
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return String.format("%s<SingletonShape Param1=\"%d\"/>", indent, 3);
			}

			@Override
			public void load(Node xml){}
		}, "bad");

		FuzzyTerm good = new FuzzyTerm(new FuzzySet(){
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return String.format("%s<SingletonShape Param1=\"%d\"/>", indent, 7);
			}

			@Override
			public void load(Node xml){}
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

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
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
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, 5, "stars", FuzzyVariable.Accumulation.SUM, FuzzyVariable.Defuzzifier.MOM, bad, good);
		String xml = var.toXMLString("");

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzyVariable newVar = new FuzzyVariable(root);
		assertEquals(var.toXMLString(""), newVar.toXMLString(""));
	}
}

package libai.fuzzy2;

import libai.fuzzy2.sets.FuzzySet;
import junit.framework.TestCase;
import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public class FuzzyTermTest extends TestCase {
	@Test
	public void testEvalWithComplement(){
		FuzzyTerm term = new FuzzyTerm(new FuzzySet(){
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return null;
			}

			@Override
			public void load(Node xml){}
		}, "term", true);

		assertTrue(term.eval(1) == 1);
	}

	@Test
	public void testEvalWithoutComplement(){
		FuzzyTerm term = new FuzzyTerm(new FuzzySet(){
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return null;
			}

			@Override
			public void load(Node xml){}
		}, "term");

		assertTrue(term.eval(1) == 0);
	}

	@Test
	public void testXMLGeneration(){
		FuzzyTerm term = new FuzzyTerm(new FuzzySet(){
			@Override
			public double eval(double s) {
				return 0;
			}

			@Override
			public String toXMLString(String indent) {
				return String.format("%s<SingletonShape Param1=\"%d\"/>", indent, 10);
			}

			@Override
			public void load(Node xml){}
		}, "term");

		assertEquals("<FuzzyTerm name=\"term\" complement=\"false\">\n" +
				"\t<SingletonShape Param1=\"10\"/>\n" +
				"</FuzzyTerm>", term.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		FuzzyTerm term = new FuzzyTerm(new TriangularShape(0, 5, 10), "term", true);
		String xml = term.toXMLString("");

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzyTerm newTerm = new FuzzyTerm(root);
		assertEquals(term.toXMLString(""), newTerm.toXMLString(""));
	}
}

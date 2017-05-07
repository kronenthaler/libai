package libai.fuzzy2;

import libai.fuzzy2.sets.TriangularShape;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 26/04/2017.
 */
public class KnowledgeBaseTest {
	@Test
	public void testXMLGeneration() {
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", FuzzyVariable.Accumulation.SUM, FuzzyVariable.Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);
		assertEquals("<KnowledgeBase>\n" +
				"\t<FuzzyVariable name=\"tip\" domainLeft=\"0.000000\" domainRight=\"10.000000\" scale=\"percentage\" type=\"output\" defaultValue=\"5.000000\" defuzzifier=\"MOM\" accumulation=\"SUM\">\n" +
				"\t\t<FuzzyTerm name=\"cheap\" complement=\"false\">\n" +
				"\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"3.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t</FuzzyTerm>\n" +
				"\t\t<FuzzyTerm name=\"generous\" complement=\"false\">\n" +
				"\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"7.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t</FuzzyTerm>\n" +
				"\t</FuzzyVariable>\n" +
				"\t<FuzzyVariable name=\"quality\" domainLeft=\"0.000000\" domainRight=\"10.000000\" scale=\"stars\" type=\"input\">\n" +
				"\t\t<FuzzyTerm name=\"bad\" complement=\"false\">\n" +
				"\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"3.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t</FuzzyTerm>\n" +
				"\t\t<FuzzyTerm name=\"good\" complement=\"false\">\n" +
				"\t\t\t<TriangularShape Param1=\"0.000000\" Param2=\"7.000000\" Param3=\"10.000000\"/>\n" +
				"\t\t</FuzzyTerm>\n" +
				"\t</FuzzyVariable>\n" +
				"</KnowledgeBase>", kb.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		FuzzyTerm bad = new FuzzyTerm(new TriangularShape(0, 3, 10), "bad");
		FuzzyTerm good = new FuzzyTerm(new TriangularShape(0, 7, 10), "good");
		FuzzyVariable var = new FuzzyVariable("quality", 0, 10, "stars", bad, good);

		FuzzyTerm cheap = new FuzzyTerm(new TriangularShape(0, 3, 10), "cheap");
		FuzzyTerm generous = new FuzzyTerm(new TriangularShape(0, 7, 10), "generous");
		FuzzyVariable tip = new FuzzyVariable("tip", 0, 10, 5, "percentage", FuzzyVariable.Accumulation.SUM, FuzzyVariable.Defuzzifier.MOM, cheap, generous);

		KnowledgeBase kb = new KnowledgeBase(var, tip);
		String xml = kb.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		KnowledgeBase newKb = new KnowledgeBase(root);
		assertEquals(kb.toXMLString(""), newKb.toXMLString(""));
	}
}

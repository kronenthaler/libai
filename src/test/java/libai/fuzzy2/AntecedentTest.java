package libai.fuzzy2;

import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class AntecedentTest extends TestCase {
	@Test
	public void testXMLGeneration(){
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

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		Antecedent newAntecedent = new Antecedent(root);
		assertEquals(antecedent.toXMLString(""), newAntecedent.toXMLString(""));
	}
}
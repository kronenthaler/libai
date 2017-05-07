package libai.fuzzy2;

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
public class ConsequentTest {
	@Test
	public void testXMLGeneration() {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Consequent consequent = new Consequent(a, b);

		assertEquals("<Consequent>\n" +
				"\t<Clause>\n" +
				"\t\t<Variable>variable1</Variable>\n" +
				"\t\t<Term>good</Term>\n" +
				"\t</Clause>\n" +
				"\t<Clause>\n" +
				"\t\t<Variable>variable2</Variable>\n" +
				"\t\t<Term>big</Term>\n" +
				"\t</Clause>\n" +
				"</Consequent>", consequent.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		Clause a = new Clause("variable1", "good");
		Clause b = new Clause("variable2", "big");
		Consequent consequent = new Consequent(a, b);
		String xml = consequent.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		Consequent newConsequent = new Consequent(root);
		assertEquals(consequent.toXMLString(""), newConsequent.toXMLString(""));
	}
}
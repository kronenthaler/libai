package libai.fuzzy;

import libai.fuzzy.modifiers.Modifier;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 27/04/2017.
 */
public class ClauseTest {
	@Test
	public void testXMLGeneration() {
		Clause clause = new Clause("tip", "good", Modifier.VERY);
		assertEquals("<Clause modifier=\"very\">\n" +
				"\t<Variable>tip</Variable>\n" +
				"\t<Term>good</Term>\n" +
				"</Clause>", clause.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		Clause clause = new Clause("tip", "good", Modifier.VERY);
		String xml = clause.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		Clause newClause = new Clause(root);
		assertEquals(clause.toXMLString(""), newClause.toXMLString(""));
	}

}

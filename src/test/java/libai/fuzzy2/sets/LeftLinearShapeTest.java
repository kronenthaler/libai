package libai.fuzzy2.sets;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 28/05/2017.
 */
public class LeftLinearShapeTest {
	@Test
	public void testXMLGeneration() {
		LeftLinearShape set = new LeftLinearShape(0, 5);

		assertEquals("<LeftLinearShape Param1=\"0.000000\" Param2=\"5.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		LeftLinearShape set = new LeftLinearShape(0, 5);
		String xml = set.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		LeftLinearShape newSet = new LeftLinearShape(root);
		assertEquals(set.toXMLString(""), newSet.toXMLString(""));
	}

	@Test
	public void testBeforeA(){
		LeftLinearShape set = new LeftLinearShape(0,5);

		assertEquals(0, set.eval(-1), 1.e-5);
	}

	@Test
	public void testAfterB(){
		LeftLinearShape set = new LeftLinearShape(0,5);

		assertEquals(1, set.eval(6), 1.e-5);
	}

	@Test
	public void testBetweenAB(){
		LeftLinearShape set = new LeftLinearShape(0,5);

		assertEquals(0.25, set.eval(5/4.), 1.e-5);
	}
}

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
public class RightLinearShapeTest {
	@Test
	public void testXMLGeneration() {
		RightLinearShape set = new RightLinearShape(0, 5);

		assertEquals("<RightLinearShape Param1=\"0.000000\" Param2=\"5.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		RightLinearShape set = new RightLinearShape(0, 5);
		String xml = set.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		RightLinearShape newSet = new RightLinearShape(root);
		assertEquals(set.toXMLString(""), newSet.toXMLString(""));
	}

	@Test
	public void testBeforeA(){
		RightLinearShape set = new RightLinearShape(0,5);

		assertEquals(1, set.eval(-1), 1.e-5);
	}

	@Test
	public void testAfterB(){
		RightLinearShape set = new RightLinearShape(0,5);

		assertEquals(0, set.eval(6), 1.e-5);
	}

	@Test
	public void testBetweenAB(){
		RightLinearShape set = new RightLinearShape(0,5);

		assertEquals(0.75, set.eval(5/4.), 1.e-5);
	}
}

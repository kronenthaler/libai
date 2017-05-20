package libai.fuzzy2.sets;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by kronenthaler on 20/05/2017.
 */
public class RectangularShapeTest {
	@Test
	public void testXMLGeneration() {
		RectangularShape set = new RectangularShape(3, 5);

		assertEquals("<RectangularShape Param1=\"3.000000\" Param2=\"5.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		RectangularShape set = new RectangularShape(3, 5);
		String xml = set.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		RectangularShape newSet = new RectangularShape(root);
		assertEquals(set.toXMLString(""), newSet.toXMLString(""));
	}

	@Test
	public void testBetweenAB(){
		RectangularShape set = new RectangularShape(3, 5);
		assertEquals(1, set.eval(3), 1.e-3);
		assertEquals(1, set.eval(3.5), 1.e-3);
		assertEquals(1, set.eval(5), 1.e-3);
	}

	@Test
	public void testOutsideAB(){
		RectangularShape set = new RectangularShape(3, 5);
		assertEquals(0, set.eval(2.999999), 1.e-3);
		assertEquals(0, set.eval(5.000001), 1.e-3);
	}
}

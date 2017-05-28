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
public class ZShapeTest {
	@Test
	public void testXMLGeneration() {
		FuzzySet set = new ZShape(0, 5);

		assertEquals("<ZShape Param1=\"0.000000\" Param2=\"5.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		FuzzySet set = new ZShape(0, 5);
		String xml = set.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		FuzzySet newSet = new ZShape(root);
		assertEquals(set.toXMLString(""), newSet.toXMLString(""));
	}

	@Test
	public void testBeforeA(){
		FuzzySet set = new ZShape(0, 5);

		assertEquals(1, set.eval(-1), 1.e-5);
	}

	@Test
	public void testAfterB(){
		FuzzySet set = new ZShape(0, 5);

		assertEquals(0, set.eval(6), 1.e-5);
	}

	@Test
	public void testBetweenABLowerHalf(){
		FuzzySet set = new ZShape(0, 5);

		assertEquals(0.875, set.eval(5/4.), 1.e-5);
	}

	@Test
	public void testBetweenABUpperHalf(){
		FuzzySet set = new ZShape(0, 5);

		assertEquals(0.125, set.eval(15/4.), 1.e-5);
	}
}

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
public class TrapezoidShapeTest {
	@Test
	public void testXMLGeneration() {
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);

		assertEquals("<TrapezoidShape Param1=\"0.000000\" Param2=\"5.000000\" Param3=\"10.000000\" Param4=\"15.000000\"/>", set.toXMLString(""));
	}

	@Test
	public void testXMLConstructor() throws Exception {
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		String xml = set.toXMLString("");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		Element root = doc.getDocumentElement();

		TrapezoidShape newSet = new TrapezoidShape(root);
		assertEquals(set.toXMLString(""), newSet.toXMLString(""));
	}

	@Test
	public void testBeforeA(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		assertEquals(0, set.eval(-1), 1.e-3);
	}

	@Test
	public void testAfterD(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		assertEquals(0, set.eval(15), 1.e-3);
	}

	@Test
	public void testBetweenBC(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		assertEquals(1, set.eval(7), 1.e-3);
	}

	@Test
	public void testBeforeAB(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		assertEquals(0.5, set.eval(2.5), 1.e-3);
	}

	@Test
	public void testBeforeCD(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 15);
		assertEquals(0.5, set.eval(12.5), 1.e-3);
	}

	@Test
	public void testRightLeftTrapezoid(){
		TrapezoidShape set = new TrapezoidShape(5, 5, 10, 15);
		assertEquals(1, set.eval(5), 1.e-3);
	}

	@Test
	public void testRightRightTrapezoid(){
		TrapezoidShape set = new TrapezoidShape(0, 5, 10, 10);
		assertEquals(1, set.eval(10), 1.e-3);
	}

}

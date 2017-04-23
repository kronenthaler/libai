package libai.fuzzy2;

import libai.fuzzy2.sets.FuzzySet;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

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
		}, "term");

		assertEquals("<FuzzyTerm name=\"term\" complement=\"false\">\n" +
				"\t<SingletonShape Param1=\"10\"/>\n" +
				"</FuzzyTerm>", term.toXMLString(""));
	}
}

package libai.fuzzy2;

import libai.fuzzy2.sets.FuzzySet;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public class FuzzyTerm implements FuzzySet {
	protected FuzzySet set;
	protected String name;
	protected boolean complement;

	public FuzzyTerm(FuzzySet set, String name) {
		this(set, name, false);
	}

	public FuzzyTerm(FuzzySet set, String name, boolean complement) {
		this.set = set;
		this.name = name;
		this.complement = complement;
	}

	@Override
	public double eval(double x) {
		if (complement)
			return 1 - set.eval(x);
		return set.eval(x);
	}

	@Override
	public String toXMLString(String indent) {
		return String.format("%s<FuzzyTerm name=\"%s\" complement=\"%s\">\n%s\n%s</FuzzyTerm>", indent, name, Boolean.toString(complement), set.toXMLString(indent + "\t"), indent);
	}
}

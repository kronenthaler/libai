package libai.classifiers;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class DataRecord {
	private Vector<Attribute> attributes;

	public DataRecord(String[] names, Attribute... atts) {
		this(atts);

		if (names.length != atts.length)
			throw new IllegalArgumentException("The number of names must match with the number of attributes");

		for (int i = 0; i < names.length; i++)
			attributes.get(i).setName(names[i]);
	}

	public DataRecord(Attribute... atts) {
		attributes = new Vector<Attribute>();
		for (Attribute a : atts)
			attributes.add(a);
	}

	public int getAttributeCount() {
		return attributes.size();
	}

	public Attribute getAttribute(int a) {
		return attributes.get(a);
	}

	public boolean contains(Attribute a) {
		for (Attribute att : attributes) {
			if (att.getClass() == a.getClass()) {
				if (att.getName().equals(a.getName())) {
					if (att.compareTo(a) == 0)
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}
}

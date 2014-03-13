package libai.classifiers;

import org.w3c.dom.*;

/**
 *
 * @author kronenthaler
 */
public abstract class Attribute implements Comparable<Attribute> {
	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}
    
    public abstract Object getValue();

    @Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Attribute && this.compareTo(((Attribute) o)) == 0;
	}

	public boolean isCategorical() {
		return this instanceof DiscreteAttribute;
	}

	public static Attribute load(Node root) {
		Attribute att = null;
		String type = root.getAttributes().getNamedItem("type").getTextContent();

		if (type.equals(DiscreteAttribute.class.getName()))
			att = new DiscreteAttribute(root.getAttributes().getNamedItem("name").getTextContent(), root.getTextContent());
		else if (type.equals(ContinuousAttribute.class.getName()))
			att = new ContinuousAttribute(root.getAttributes().getNamedItem("name").getTextContent(), Double.parseDouble(root.getTextContent()));

		return att;
	}
    
    public static Attribute getInstance(String value){
        try {
            return new ContinuousAttribute(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return new DiscreteAttribute(value);
        }
    }
}
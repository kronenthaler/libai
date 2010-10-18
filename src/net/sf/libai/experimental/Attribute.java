package net.sf.libai.experimental;

/**
 *
 * @author kronenthaler
 */
public abstract class Attribute implements Comparable<Attribute>{
	protected String name;

	public String getName(){
		return name; 
	}

	@Override
	public boolean equals(Object o){
		return o instanceof Attribute && this.compareTo(((Attribute)o)) == 0;
	}

	public boolean isCategorical(){
		return this instanceof DiscreteAttribute;
	}
}
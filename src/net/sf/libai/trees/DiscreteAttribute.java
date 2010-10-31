package net.sf.libai.trees;

/**
 *
 * @author kronenthaler
 */
public class DiscreteAttribute extends Attribute{
	private String value;

	public DiscreteAttribute(String v){
		value = v;
	}

	public DiscreteAttribute(String name, String v){
		this.name = name;
		value = v;
	}
	
	@Override
	public String getValue(){
		return value;
	}

	@Override
	public int compareTo(Attribute o) {
		return value.compareTo(((DiscreteAttribute)o).value);
	}

	@Override
	public String toString(){
		return value;
	}
}

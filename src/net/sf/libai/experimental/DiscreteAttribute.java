package net.sf.libai.experimental;

/**
 *
 * @author kronenthaler
 */
public class DiscreteAttribute extends Attribute{
	private String value;

	public DiscreteAttribute(String name, String v){
		this.name = name;
		value = v;
	}
	
	public String getValue(){
		return value;
	}

	@Override
	public int compareTo(Attribute o) {
		return value.compareTo(((DiscreteAttribute)o).value);
	}

	public String toString(){
		return value;
	}
}

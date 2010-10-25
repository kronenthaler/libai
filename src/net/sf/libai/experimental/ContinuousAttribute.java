package net.sf.libai.experimental;

/**
 *
 * @author kronenthaler
 */
public class ContinuousAttribute extends Attribute{
	private double value;

	public ContinuousAttribute(String name, double v){
		this.name = name;
		value = v;
	}

	@Override
	public int compareTo(Attribute o) {
		if(value > ((ContinuousAttribute) o).value) return 1;
		if(value < ((ContinuousAttribute) o).value) return -1;
		return 0;
		//return value - ((ContinuousAttribute) o).value;
	}

	@Override
	public Double getValue(){ return value; }

	@Override
	public String toString(){
		return ""+value;
	}
}

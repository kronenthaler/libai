package net.sf.libai.fuzzy;

/**
 * Wrapper around a double value. This implementation allows, change the type of the double, for other
 * types more precises or maybe any type suitable for embebbed devices without change the structure of
 * the rest of the engine.
 * @author kronenthaler
 */
public class Variable implements Comparable<Variable>{
	private double value;
	
	public Variable(double v){
		setValue(v);
	}
	
	public void setValue(double v){
		value = v;
	}
	
	public double getValue(){
		return value;
	}

	public int compareTo(Variable o){
		return (int)(value - o.value);
	}

	public String toString(){
		return String.valueOf(value);
	}
}

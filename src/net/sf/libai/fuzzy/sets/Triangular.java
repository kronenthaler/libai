package net.sf.libai.fuzzy.sets;

import net.sf.libai.fuzzy.*;
import java.util.ArrayList;

/**
 * Fuzzy set representing a triangular function. 
 * Only one point on the function can take the max value of 1.
 * The triangular function can take 3 variations: <br/>
 * <ul>
 * <li>right triangle to the left. a = b != c.</li>
 * <li>centered triangle a != b != c.</li>
 * <li>right triangle to the right a != b = c.</li>
 * </ul>
 *
 * In the three cases, the support of the set is the interval [a,c].<br/>
 * The kernel is always the single value b.<br/>
 * 
 * @author kronenthaler
 */
public class Triangular implements FuzzySet{
	private Variable a,b,c;
	private ArrayList<Double> support;
	private ArrayList<Double> kernel;
	protected double DELTA = 0.1;

	public Triangular(double _a, double _b, double _c, double delta){
		a=new Variable(_a);
		b=new Variable(_b);
		c=new Variable(_c);
		DELTA = delta;

		kernel = new ArrayList<Double>();
		kernel.add(b.getValue()); //middle point

		if(delta > 0){
			support = new ArrayList<Double>();
			for(double i = Math.min(_a, Math.min(_b,_c)),max=Math.max(_a, Math.max(_b,_c)); i <= max; i+=DELTA)
				support.add(i);
		}
	}

	public Triangular(double _a, double _b, double _c){
		this(_a,_b,_c,.5);
	}
	
	public double eval(Variable s){
		return eval(s.getValue());
	}

	public double eval(double s){
		if(s <= a.getValue() || s >= c.getValue()) return 0;
		if(s == b.getValue()) return 1;

		if(s > a.getValue() && s < b.getValue())
			return (s-a.getValue())/(b.getValue()-a.getValue());

		return (c.getValue()-s)/(c.getValue()-b.getValue());
	}

	public ArrayList<Double> getSupport(){
		return support;
	}

	public ArrayList<Double> getKernel(){
		return kernel;
	}

	public String toString(){
		return "Triangle("+a+","+b+","+c+")";
	}
}

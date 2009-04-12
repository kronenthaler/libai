package net.sf.libai.fuzzy.sets;

import net.sf.libai.fuzzy.*;
import java.util.ArrayList;

/**
 * Fuzzy set representing a trapezoid function.
 * The trapezoid function can take 3 variations: <br/>
 * <ul>
 * <li>right trapezoid to the left. a = b != c != d.</li>
 * <li>centered trapezoid a != b != c != d.</li>
 * <li>right trapezoid to the right a != b != c = d.</li>
 * </ul>
 *
 * In the three cases, the support of the set is the interval [a,d].<br/>
 * The kernel is always the interval [b,c].<br/>
 *
 * @author kronenthaler
 */
public class Trapezoid implements FuzzySet{
	public Variable a,b,c,d;
	public ArrayList<Double> support;
	public ArrayList<Double> kernel;
	protected double DELTA = 0.1;

	public Trapezoid(double _a,double _b,double _c,double _d){
		this(_a, _b, _c, _d, 0.5);
	}

	public Trapezoid(double _a,double _b,double _c,double _d, double delta){
		a = new Variable(_a);
		b = new Variable(_b);
		c = new Variable(_c);
		d = new Variable(_d);

		DELTA = delta;

		kernel = new ArrayList<Double>();
		support = new ArrayList<Double>();

		if(DELTA > 0){
			for(double i = _a,max=_d; i <= max; i+=DELTA){
				support.add(i);
				if(i >= _b && i <= _c)
					kernel.add(i);
			}
		}
	}

	public double eval(Variable s) {
		return eval(s.getValue());
	}

	public double eval(double s) {
		if(s < a.getValue() || s > d.getValue()) return 0;
		if(s > b.getValue() && s < c.getValue()) return 1;

		if(s >= a.getValue() && s <= b.getValue())
			return (s - a.getValue()) / (b.getValue() - a.getValue());

		return (d.getValue() - s) / (d.getValue() - c.getValue());
	}

	public ArrayList<Double> getSupport() {
		return support;
	}

	public ArrayList<Double> getKernel() {
		return kernel;
	}

	public String toString(){
		return "Trapezoid("+a+","+b+","+c+","+d+")";
	}
}

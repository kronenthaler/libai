package net.sf.libai.fuzzy;

import net.sf.libai.fuzzy.sets.FuzzySet;

/**
 *
 * @author kronenthaler
 */
public class Condition {
	public static final int AND = 0;
	public static final int OR = 1;
	public static final int NOT = 2;

	private FuzzySet root =null;
	private Variable variable = null;
	private int operator=-1;
	private Condition left = null,right = null;

	
	public Condition(FuzzySet x, Variable v){
		root = x;
		variable = v;
	}
	
	private Condition(int op, Condition l, Condition r){
		operator = op;
		left = l;
		right = r;
	}
	
	public Condition and(FuzzySet x,Variable v){
		return new Condition(AND, this, new Condition(x,v));
	}
	
	public Condition and(FuzzySet[] x, Variable[] v){
		if(x.length != v.length)
			throw new IllegalArgumentException("Dimmension mismatch");

		Condition r = this;
		for(int i=0;i<x.length;i++)
			r = r.and(x[i],v[i]);
		return r;
	}
	
	public Condition or(FuzzySet x, Variable v){
		return new Condition(OR, this, new Condition(x,v));
	}
	
	public Condition or(FuzzySet[] x, Variable[] v){
		if(x.length != v.length)
			throw new IllegalArgumentException("Dimmension mismatch");

		Condition r = this;
		for(int i=0;i<x.length;i++)
			r = r.or(x[i],v[i]);
		return r;
	}

	public Condition not(){
		return new Condition(NOT, this, null);
	}
	
	public double eval(){ //retorna el TAU de esta regla
		if(left != null && right!=null){
			if(operator==AND)
				return Math.min(left.eval(), right.eval());
			else if(operator == OR)
				return Math.max(left.eval(), right.eval());
			else //NOT
				return 1.0-left.eval();
		}else{
			return root.eval(variable);
		}
	}
	
	public String toString(){
		if(left == null && right == null)
			return root.toString()+"@"+variable.toString();
		else
			if(right == null){
				return "!"+left.toString();
			}else
				return "("+left.toString()+" "+(operator==AND?"&":"|")+" "+right.toString()+")";
	}
}
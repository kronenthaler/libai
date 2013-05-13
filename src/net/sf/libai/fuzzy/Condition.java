package net.sf.libai.fuzzy;

import net.sf.libai.fuzzy.sets.FuzzySet;

/**
 *	Binary tree that evaluates the condition of the fuzzy sets.
 *	Each node of the condition need a pair FuzzySet-Variable. The variable to evaluate 
 *	on the set.
 *	The root nodes could be: FuzzySet or a binary operator (AND, OR, NOT)
 *	If the root node is a fuzzy set the evaluation of that node is just get the membership
 *	value for the associated variable.
 *	If the root node is the operator NOT, the evaluation of the node is 1 minus evaluate the left branch
 *	of the tree.
 *	If the root node is the operator AND, the evaluation of the node is the minimum between the evaluation
 *  of the left and right branches.
 *	If the root node is the operator OR, the evaluation of the node is the maximum between the evaluation
 *  of the left and right branches.
 *	@author kronenthaler
 */
public class Condition {
	public static final int AND = 0;
	public static final int OR = 1;
	public static final int NOT = 2;

	/** The fuzzy set to evaluate. Is not-null only in the leaves. */
	private FuzzySet root =null;

	/** The variable associated with the fuzzy set. Is not-null only in the leaves. */
	private Variable variable = null;

	/** Kind of operator {AND, OR, NOT} */
	private int operator=-1;
	private Condition left = null,right = null;

	/**
	 *	Constructor. Creates a leaf node.
	 *	@param x The fuzzy set for the leaf.
	 *	@param v The variable for the leaf.
	 */
	public Condition(FuzzySet x, Variable v){
		root = x;
		variable = v;
	}

	/**
	 *	Constructor. Creates and internal node.
	 *	Just usable through the function and(), or() &amp; not();
	 *	@param op The operator type.
	 *	@param l Left branch condition.
	 *	@param r Right branch condition.
	 */
	private Condition(int op, Condition l, Condition r){
		operator = op;
		left = l;
		right = r;
	}

	/**
	 *	Creates a new condition introducing a new node.
	 *	@param x The fuzzy set to append.
	 *	@param v The variable for the fuzzy set.
	 *	@return A new condition tree (this &amp; C(x,v))
	 */
	public Condition and(FuzzySet x,Variable v){
		return new Condition(AND, this, new Condition(x,v));
	}

	/**
	 *	Creates a new condition introducing many new nodes.
	 *	@param x The fuzzy sets to append.
	 *	@param v The variables for the fuzzy sets.
	 *	@return A new condition tree (this &amp; C(x[0],v[0]) &amp; ... &amp; C(x[length-1], v[length-1]))
	 */
	public Condition and(FuzzySet[] x, Variable[] v){
		if(x.length != v.length)
			throw new IllegalArgumentException("Dimmension mismatch");

		Condition r = this;
		for(int i=0;i<x.length;i++)
			r = r.and(x[i],v[i]);
		return r;
	}

	/**
	 *	Creates a new condition introducing a new node.
	 *	@param x The fuzzy set to append.
	 *	@param v The variable for the fuzzy set.
	 *	@return A new condition tree (this | C(x,v))
	 */
	public Condition or(FuzzySet x, Variable v){
		return new Condition(OR, this, new Condition(x,v));
	}

	/**
	 *	Creates a new condition introducing many new nodes.
	 *	@param x The fuzzy sets to append.
	 *	@param v The variables for the fuzzy sets.
	 *	@return A new condition tree (this | C(x[0],v[0]) | ... | C(x[length-1], v[length-1]))
	 */
	public Condition or(FuzzySet[] x, Variable[] v){
		if(x.length != v.length)
			throw new IllegalArgumentException("Dimmension mismatch");

		Condition r = this;
		for(int i=0;i<x.length;i++)
			r = r.or(x[i],v[i]);
		return r;
	}

	/**
	 *	Creates a new condition introducing a new node.
	 *	@return A new condition tree !(this)
	 */
	public Condition not(){
		return new Condition(NOT, this, null);
	}

	/**
	 *	Evaluates recursively the condition tree and returns the Tau value for the
	 *	activation of this rule.
	 *	@return The tau value of the membershio for this rule.
	 */
	public double eval(){ //return the TAU value of this rule
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
	
	@Override
	public String toString(){
		if(left == null && right == null)
			return "["+root.toString()+" w/"+variable.toString()+"]";
		else
			if(right == null){
				return "!"+left.toString();
			}else
				return "("+left.toString()+" "+(operator==AND?"&":"|")+" "+right.toString()+")";
	}
}

/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.fuzzy;

import libai.fuzzy.sets.FuzzySet;

/**
 * Binary tree that evaluates the condition of the fuzzy sets. Each node of the
 * condition need a pair FuzzySet-Value. The value to evaluate on the set.
 * The root nodes could be: FuzzySet or a binary operator (AND, OR, NOT) If the
 * root node is a fuzzy set the evaluation of that node is just get the
 * membership value for the associated value. If the root node is the
 * operator NOT, the evaluation of the node is 1 minus evaluate the left branch
 * of the tree. If the root node is the operator AND, the evaluation of the node
 * is the minimum between the evaluation of the left and right branches. If the
 * root node is the operator OR, the evaluation of the node is the maximum
 * between the evaluation of the left and right branches.
 *
 * @author kronenthaler
 */
// TODO: refactor to pair explicitly the FuzzySet and the value (the fuzzyset can be bound to the value already!)
public class Condition {
	/**
	 * The fuzzy set to evaluate. Is not-null only in the leaves.
	 */
	private FuzzySet root = null;
	/**
	 * The value associated with the fuzzy set. Is not-null only in the
	 * leaves.
	 */
	private Value value = null;
	/**
	 * Kind of operator {AND, OR, NOT}
	 */
	private Operator operator = Operator.None;
	private Condition left = null, right = null;
	/**
	 * Constructor. Creates a leaf node.
	 *
	 * @param x The fuzzy set for the leaf.
	 * @param v The value for the leaf.
	 */
	public Condition(FuzzySet x, Value v) {
		root = x;
		value = v;
	}

	/**
	 * Constructor. Creates and internal node. Just usable through the function
	 * and(), or() &amp; not();
	 *
	 * @param op The operator type.
	 * @param l  Left branch condition.
	 * @param r  Right branch condition.
	 */
	private Condition(Operator op, Condition l, Condition r) {
		operator = op;
		left = l;
		right = r;
	}

	/**
	 * Creates a new condition introducing a new node.
	 *
	 * @param x The fuzzy set to append.
	 * @param v The value for the fuzzy set.
	 * @return A new condition tree (this &amp; C(x,v))
	 */
	public Condition and(FuzzySet x, Value v) {
		return new Condition(Operator.AND, this, new Condition(x, v));
	}

	/**
	 * Creates a new condition introducing many new nodes.
	 *
	 * @param x The fuzzy sets to append.
	 * @param v The variables for the fuzzy sets.
	 * @return A new condition tree (this &amp; C(x[0],v[0]) &amp; ... &amp;
	 * C(x[length-1], v[length-1]))
	 */
	public Condition and(FuzzySet[] x, Value[] v) {
		if (x.length != v.length)
			throw new IllegalArgumentException("Dimension mismatch");

		Condition r = this;
		for (int i = 0; i < x.length; i++)
			r = r.and(x[i], v[i]);
		return r;
	}

	/**
	 * Creates a new condition introducing a new node.
	 *
	 * @param x The fuzzy set to append.
	 * @param v The value for the fuzzy set.
	 * @return A new condition tree (this | C(x,v))
	 */
	public Condition or(FuzzySet x, Value v) {
		return new Condition(Operator.OR, this, new Condition(x, v));
	}

	/**
	 * Creates a new condition introducing many new nodes.
	 *
	 * @param x The fuzzy sets to append.
	 * @param v The variables for the fuzzy sets.
	 * @return A new condition tree (this | C(x[0],v[0]) | ... | C(x[length-1],
	 * v[length-1]))
	 */
	public Condition or(FuzzySet[] x, Value[] v) {
		if (x.length != v.length)
			throw new IllegalArgumentException("Dimension mismatch");

		Condition r = this;
		for (int i = 0; i < x.length; i++)
			r = r.or(x[i], v[i]);
		return r;
	}

	/**
	 * Creates a new condition introducing a new node.
	 *
	 * @return A new condition tree !(this)
	 */
	public Condition not() {
		return new Condition(Operator.NOT, this, null);
	}

	/**
	 * Evaluates recursively the condition tree and returns the Tau value for
	 * the activation of this rule.
	 *
	 * @return The tau value of the membership for this rule.
	 */
	public double eval() { //returns TAU of this rule
		if (left != null && right != null) {
			if (operator == Operator.AND)
				return Math.min(left.eval(), right.eval());
			else if (operator == Operator.OR)
				return Math.max(left.eval(), right.eval());
			else //NOT
				return 1.0 - left.eval();
		} else {
			return root.eval(value.getValue());
		}
	}

	@Override
	public String toString() {
		if (left == null && right == null)
			return "[" + root.toString() + " w/" + value.toString() + "]";
		else if (right == null) {
			return "!" + left.toString();
		} else
			return "(" + left.toString() + " " + (operator == Operator.AND ? "&" : "|") + " " + right.toString() + ")";
	}

	enum Operator {
		None,
		AND,
		OR,
		NOT
	}
}
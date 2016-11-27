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
import java.util.*;


/**
 * Represent a set of fuzzy set with a common meaning. By example, the sets big,
 * medium, small by themselves doesn't mean anything, but if you group this
 * three set under the concept of height, their values may represents inches or
 * meters. This class is vital for the process of reasoning because this class
 * provide the way to get a coherent result of the defuzzify process. Besides,
 * this class hold the final value of this process.
 *
 * So far, this is the only use of this class, but can be useful for other
 * purposes in the future.
 *
 * @author kronenthaler
 */
public class FuzzyGroup {
	/**
	 * The members of this group.
	 */
	private HashSet<FuzzySet> members;
	/**
	 * The variable associated with this group.
	 */
	private Variable v;

	/**
	 * Constructor. Creates a FuzzyGroup with a list of members.
	 *
	 * @param mems List of members for this fuzzy group.
	 */
	public FuzzyGroup(FuzzySet... mems) {
		v = new Variable(0);
		members = new HashSet<FuzzySet>();

		for (FuzzySet m : mems)
			members.add(m);
	}

	/**
	 * Evaluate if a fuzzy set belongs to this group, ie, is in the same
	 * context.
	 *
	 * @param s Fuzzy set to check
	 * @return true is this set blong to this group, false otherwise.
	 */
	public boolean contains(FuzzySet s) {
		return members.contains(s);
	}

	/**
	 * Set the value for this group. NOT MUST be invoked by the user.
	 *
	 * @param d value to set.
	 */
	public void setValue(double d) {
		v.setValue(d);
	}

	/**
	 * Return the variable associate with this group, holding the final value of
	 * the defuzzify process.
	 *
	 * @return a variable with the final value.
	 */
	public Variable getVariable() {
		return v;
	}
}

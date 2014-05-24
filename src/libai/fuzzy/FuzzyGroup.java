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

package libai.fuzzy;

import libai.fuzzy.sets.FuzzySet;
import libai.common.Pair;
import java.util.*;


/**
 * Implementation of a fuzzy rule. Is contructed with a condition and a set of
 * one or more "actions". In the bottom line, the actions are fuzzy sets. The
 * important part in this reasoning process is related with the concept of
 * FuzzyGroup. A FuzzyGroup allow creating linguistic variables with a concrete
 * meaning, therefore, all the fuzzy sets in this actions must belong to one
 * fuzzy group attached to the engine.
 *
 * @author kronenthaler
 */
public class Rule {
	/**
	 * Condition tree for this rule.
	 */
	private Condition cond;
	/**
	 * Set of actions to be triggered if the condition is 'fulfill'
	 */
	private ArrayList<FuzzySet> actions;

	/**
	 * Constructor. Create a new rule with a condition tree and a list of fuzzy
	 * sets (actions).
	 *
	 * @param _cond The condition tree for this rule.
	 * @param acts The actions for this rule.
	 */
	public Rule(Condition _cond, FuzzySet... acts) {
		cond = _cond;
		actions = new ArrayList<FuzzySet>();
		for (FuzzySet a : acts)
			actions.add(a);
	}

	/**
	 * Fire the rule. Evaluate the condition, calculate the respective tau, and
	 * make the combination process over the actions. Returning a support set
	 * for each action in the rule. This results are combined in the engine to
	 * the defuzzify process.
	 *
	 * @return An array of ArrayLists of pairs double-double, containing each
	 * one the support calculated for the specific set of the action.
	 */
	public ArrayList<Pair<Double, Double>>[] fire() {
		ArrayList<Pair<Double, Double>> ret[] = new ArrayList[actions.size()];
		double tau = cond.eval();
		int i = 0;
		for (FuzzySet set : actions) {
			ret[i] = new ArrayList<Pair<Double, Double>>();
			ArrayList<Double> support = set.getSupport();

			for (Double d : support)
				ret[i].add(new Pair<Double, Double>(d, set.eval(d) * tau));

			i++;
		}
		return ret;
	}

	/**
	 * Get the action for at index.
	 *
	 * @param index The index for the action.
	 * @return The fuzzy set associated to that action.
	 */
	public FuzzySet getAction(int index) {
		return actions.get(index);
	}
}

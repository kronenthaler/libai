package net.sf.libai.fuzzy;

import java.util.*;

import net.sf.libai.common.*;
import net.sf.libai.fuzzy.sets.*;


/**
 * Implementation of a fuzzy rule.
 * Is contructed with a condition and a set of one or more "actions".
 * In the bottom line, the actions are fuzzy sets. The important part in this
 * reasoning process is related with the concept of FuzzyGroup. A FuzzyGroup allow
 * creating linguistic variables with a concrete meaning, therefore, all the
 * fuzzy sets in this actions must belong to one fuzzy group attached to the engine.
 * 
 * @author kronenthaler
 */
public class Rule {
	private Condition cond;
	private ArrayList<FuzzySet> actions;

	public Rule(Condition _cond, FuzzySet... acts){
		cond = _cond;
		actions = new ArrayList<FuzzySet>();
		for(FuzzySet a:acts)
			actions.add(a);
	}

	/**
	 * Fire the rule. Evaluate the condition, calculate the respective tau, and make
	 * the combination process over the actions.
	 * Returning a support set for each action in the rule.
	 * This results are combined in the engine to the defuzzify process.
	 *
	 * @return An array of ArrayLists of doubles, containing each one the support calculated for the specific
	 *	set of the action.
	 */
	public ArrayList<Pair<Double,Double>>[] fire(){
		ArrayList<Pair<Double,Double>> ret[] = new ArrayList[actions.size()];
		double tau = cond.eval();
		int i=0;
		for(FuzzySet set:actions){
			ret[i]=new ArrayList<Pair<Double,Double>>();
			ArrayList<Double> support = set.getSupport();

			for(Double d : support)
				ret[i].add(new Pair<Double,Double>(d,set.eval(d)*tau));
			
			i++;
		}
		return ret;
	}

	public FuzzySet getAction(int index){
		return actions.get(index);
	}
}

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
import libai.common.Pair;
import java.util.*;


/**
 * Core part of the rule system. The engine, the rules, and the sets are
 * created, and attached to this engine instance before the inference process
 * can start. After each run of the inference process, each rule is evaluated.
 * The actions are executed, and the fuzzygroups are updated.
 *
 * @author kronenthaler
 */
public class Engine {
	/**
	 * List of rules for the reasoning process.
	 */
	private ArrayList<Rule> rules;
	/**
	 * List of fuzzy groups associated to the different contexts of the actions
	 * and rules.
	 */
	private ArrayList<FuzzyGroup> groups;
	/**
	 * Flag to print verbose information over the reasoning process.
	 */
	private boolean debug = false;

	public Engine() {
		rules = new ArrayList<Rule>();
		groups = new ArrayList<FuzzyGroup>();
	}

	/**
	 * Constructor. Creates a Engine with verbose output.
	 */
	public Engine(boolean _debug) {
		this();
		debug = _debug;
	}

	/**
	 * Add one or more rules to this engine.
	 *
	 * @param rs One or more rules to attach.
	 */
	public void addRule(Rule... rs) {
		for (Rule r : rs)
			rules.add(r);
	}

	/**
	 * Remove the especified rule from the engine.
	 *
	 * @param r Rule to be removed.
	 */
	public void removeRule(Rule r) {
		rules.remove(r);
	}

	/**
	 * Add one or more groups to this engine.
	 *
	 * @param gs One or more groups to attach.
	 */
	public void addGroup(FuzzyGroup... gs) {
		for (FuzzyGroup g : gs)
			groups.add(g);
	}

	/**
	 * Remove the especified group from the engine.
	 *
	 * @param g The group to be removed.
	 */
	public void removeGroup(FuzzyGroup g) {
		groups.remove(g);
	}

	/**
	 * Find the especified set among the groups. If this set belong to any
	 * group, the first group is returned, null otherwise.
	 *
	 * @param s FuzzySet to find.
	 * @return The FuzzyGroup where belongs, otherwise null.
	 */
	public FuzzyGroup find(FuzzySet s) {
		//find and return the group where this set belongs
		for (FuzzyGroup g : groups)
			if (g.contains(s))
				return g;
		return null;
	}

	/**
	 * Run one iteration of the inference process. On that iteration, all the
	 * rules are evaluated. The results are merged according to the fuzzygroup
	 * where belong. Finally, the results are integrated (for fuzzy group) and
	 * the centroid is calculated.
	 */
	public void start() {
		HashMap<FuzzyGroup, ArrayList<Pair<Double, Double>>> allResults = null;
		for (Rule r : rules) {
			ArrayList<Pair<Double, Double>>[] fireResult = r.fire();

			if (debug) {
				System.err.println("\n\nRule fired: ");
				for (int i = 0; i < fireResult.length; i++) {
					System.err.println(fireResult[i]);
				}
				System.err.println("end rule");
			}

			if (allResults == null) {
				allResults = new HashMap<FuzzyGroup, ArrayList<Pair<Double, Double>>>();

				for (int i = 0; i < fireResult.length; i++) {
					allResults.put(find(r.getAction(i)), fireResult[i]);
				}
			} else {
				//merge, just keep the max if is repeated the first value of the pair
				for (int i = 0; i < fireResult.length; i++) {
					FuzzyGroup action = find(r.getAction(i));

					if (allResults.get(action) == null) {
						allResults.put(action, fireResult[i]);
						continue;
					}

					for (int j = 0; j < fireResult[i].size(); j++) {
						int index = Collections.binarySearch(allResults.get(action), fireResult[i].get(j));
						if (index >= 0) {
							allResults.get(action).set(index,
									allResults.get(action).get(index).second.compareTo(fireResult[i].get(j).second) > 0
									? allResults.get(action).get(index)
									: fireResult[i].get(j));
						} else {
							allResults.get(action).add(fireResult[i].get(j));
							Collections.sort(allResults.get(action));
						}
					}
				}
			}
		}

		//defuzzify
		for (FuzzyGroup action : allResults.keySet()) {
			double nominator = 0, denominator = 0;
			for (Pair<Double, Double> d : allResults.get(action)) {
				nominator += (d.first * d.second);
				denominator += (d.second);
			}
			if (denominator != 0)
				action.setValue(nominator / denominator);
			else
				action.setValue(0);
		}
	}
}

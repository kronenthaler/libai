package net.sf.libai.fuzzy;

import java.util.*;

import net.sf.libai.fuzzy.sets.*;
import net.sf.libai.common.*;

/**
 * Core part of the rule system.
 * The engine, the rules, and the sets are created, and attached to this engine instance
 * before the inference process can start.
 * After each run of the inference process, each rule is evaluated. The actions are executed,
 * and the fuzzygroups are updated.
 *
 * @author kronenthaler
 */
public class Engine {
	private ArrayList<Rule> rules;
	private ArrayList<FuzzyGroup> groups;
	private boolean debug=false;

	public Engine(){
		rules = new ArrayList<Rule>();
		groups = new ArrayList<FuzzyGroup>();
	}

	public Engine(boolean _debug){
		debug = _debug;
	}

	/**
	 * Add one or more rules to this engine.
	 * @param rs One or more rules to attach.
	 */
	public void addRule(Rule... rs){
		for(Rule r: rs)
			rules.add(r);
	}

	/**
	 * Remove the especified rule from the engine.
	 * @param r Rule to be removed.
	 */
	public void removeRule(Rule r){
		rules.remove(r);
	}

	/**
	 * Add one or more groups to this engine.
	 * @param gs One or more groups to attach.
	 */
	public void addGroup(FuzzyGroup... gs){
		for(FuzzyGroup g:gs)
			groups.add(g);
	}

	/**
	 * Remove the especified group from the engine.
	 * @param g The group to be removed.
	 */
	public void removeGroup(FuzzyGroup g){
		groups.remove(g);
	}

	/**
	 * Find the especified set among the groups. If this set belong to any group,
	 * the first group is returned, null otherwise.
	 * @param s FuzzySet to find.
	 * @return The FuzzyGroup where belongs, otherwise null.
	 */
	public FuzzyGroup find(FuzzySet s){
		//find and return the group where this set belongs
		for(FuzzyGroup g : groups)
			if(g.contains(s)) return g;
		return null;
	}

	/**
	 * Run one iteration of the inference process.
	 * On that iteration, all the rules are evaluated. The results are merged according
	 * to the fuzzygroup where belong.
	 * Finally, the results are integrated (for fuzzy group) and the centroid is calculated.
	 */
	public void start(){
		HashMap<FuzzyGroup, ArrayList<Pair<Double,Double>>> allResults=null;
		for(Rule r : rules){
			ArrayList<Pair<Double,Double>>[] fireResult = r.fire();
			
			//FOR DEBUG:
			if(debug){
				System.err.println("\n\nRule fired: ");
				for(int i=0;i<fireResult.length;i++){
					System.err.println(fireResult[i]);
				}
				System.err.println("end rule");
			}
			//END FOR DEBUG

			if(allResults == null){
				allResults = new HashMap<FuzzyGroup, ArrayList<Pair<Double,Double>>>();

				for(int i=0;i<fireResult.length;i++){
					allResults.put(find(r.getAction(i)), fireResult[i]);
				}
			}else{
				//merge, just keep the max if is repeated the first value of the pair
				for(int i=0;i<fireResult.length;i++){
					FuzzyGroup action = find(r.getAction(i));

					if(allResults.get(action)==null){
						allResults.put(action, fireResult[i]);
						continue;
					}

					for(int j=0;j<fireResult[i].size();j++){
						int index = Collections.binarySearch(allResults.get(action), fireResult[i].get(j));
						if(index >= 0){
							allResults.get(action).set(index,
								allResults.get(action).get(index).second.compareTo(fireResult[i].get(j).second)>0?
									allResults.get(action).get(index):
									fireResult[i].get(j));
						}else{
							allResults.get(action).add(fireResult[i].get(j));
							Collections.sort(allResults.get(action));
						}
					}
				}
			}
		}

		//defuzzify
		//allResults deberia ser una tabla hash que indica sobre cual variable estoy actuando.
		//hasta ahora se refire a la accion que se esta disparando.
		//asume que todas las activaciones corren sobre el mismo conjunto difuso.
		for(FuzzyGroup action : allResults.keySet()){
			double nominator=0,denominator=0;
			for(Pair<Double,Double> d : allResults.get(action)){
				nominator += (d.first*d.second);
				denominator += (d.second);
			}
			if(denominator != 0)
				action.setValue(nominator/denominator);
			else
				action.setValue(0);
		}
	}
}

package net.sf.libai.search;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class DFS {
	public State search(State init, State target){
		HashMap<State, Boolean> visited=new HashMap<State,Boolean>();
		Stack<State> q = new Stack<State>();
		q.add(init);
		visited.put(init,true);

		while(!q.isEmpty()){
			State current = q.pop();

			if(current.equals(target)){
				return current;
			}

			for(State next : current.getCandidates()){
				if(visited.get(next)==null){
					visited.put(next, true);
					q.push(next);
				}
			}
		}

		return null;
	}
}

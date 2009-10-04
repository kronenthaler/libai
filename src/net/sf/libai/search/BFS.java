package net.sf.libai.search;

import java.io.*;
import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class BFS {
	public State search(State init, State target){
		HashMap<State, Boolean> visited=new HashMap<State,Boolean>();
		ArrayList<State> q = new ArrayList<State>();
		q.add(init);
		visited.put(init,true);

		while(!q.isEmpty()){
			State current = q.remove(0);

			if(current.equals(target)){
				return current;
			}

			for(State next : current.getCandidates()){
				if(visited.get(next)==null){
					visited.put(next, true);
					q.add(next);
				}
			}
		}

		return null;
	}
}
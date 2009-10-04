package net.sf.libai.search;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class AStar {

	public State search(State init, State target){
		PriorityQueue<State> opened = new PriorityQueue<State>();
		HashMap<State, State> openedMirror = new HashMap<State, State>();
		HashMap<State, State> closed = new HashMap<State, State>();

		opened.add(init);
		openedMirror.put(init, init);
		
		while(!opened.isEmpty()){
			State current = opened.poll();
			
			openedMirror.remove(current);
			closed.put(current, current);
			
			if(current.equals(target)){
				return current;
			}

			for(State next : current.getCandidates()){
				if(closed.containsKey(next)){
					State prev = closed.get(next);
					if(prev.compareTo(next) <= 0) continue;

					closed.remove(next);
				}

				if(openedMirror.containsKey(next)){
					State prev = openedMirror.get(next);
					if(prev.compareTo(next) <= 0) continue;

					opened.remove(next); //bottle neck, must be a way to reduce the time, try indexing
					openedMirror.remove(next);
				}

				opened.add(next);
				openedMirror.put(next,next);
			}
		}
		
		return null;
	}
}

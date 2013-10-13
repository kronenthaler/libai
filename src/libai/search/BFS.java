package libai.search;

import java.io.*;
import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class BFS implements Search {
	public State search(State init) {
		HashMap<State, Boolean> visited = new HashMap<State, Boolean>();
		ArrayList<State> q = new ArrayList<State>();
		q.add(init);
		visited.put(init, true);

		while (!q.isEmpty()) {
			State current = q.remove(0);

			if (current.isSolution()) {
				return current;
			}

			for (State next : current.getCandidates()) {
				if (visited.get(next) == null) {
					visited.put(next, true);
					q.add(next);
				}
			}
		}

		return null;
	}
}
package libai.search;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class DFS implements Search {
    public State search(State init) {
        HashMap<State, Boolean> visited = new HashMap<State, Boolean>();
        Stack<State> q = new Stack<State>();
        q.add(init);
        visited.put(init, true);

        while (!q.isEmpty()) {
            State current = q.pop();

            if (current.isSolution()) {
                return current;
            }

            for (State next : current.getCandidates()) {
                if (visited.get(next) == null) {
                    visited.put(next, true);
                    q.push(next);
                }
            }
        }

        return null;
    }
}

package net.sf.libai.search;

/**
 *
 * @author kronenthaler
 */
public interface Search {
	/**
	 *	Do a search from the <code>init</code> state, until the current state becomes a solution (State.isSolution())
	 *	If the algorithm finds a solution return the current state as the solution.
	 *	That internal representation of the state must contain all the information needed to reconstruct the path or
	 *	any other operation needed by the user.
	 *	If there is no solution this method must return null.
	 *	@param init Initial state of the search
	 *	@return The final state that become a solution or null if there is no solution.
	 */
	public State search(State init);
}

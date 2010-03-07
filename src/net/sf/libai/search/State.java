package net.sf.libai.search;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public abstract class State implements Comparable<State>{
	/** Get the cost of the current state */
	public abstract double getCost();

	/** Get the heuristic cost of the current state */
	public abstract double getHeuristicCost();

	/** Returns a list with all the possible candidates from this state*/
	public abstract ArrayList<State> getCandidates();

	/** Compare two states */
	public abstract int compareTo(State o);

	/** Hash code of the state MUST BE implemented to keep track of the visited
	 states */
	public abstract int hashCode();

	/** Determines if two states are equals or equivalents */
	public boolean equals(Object o){
		return compareTo((State)o) == 0;
	}

	/** Determines if the current state is a solution or not */
	public abstract boolean isSolution();
}
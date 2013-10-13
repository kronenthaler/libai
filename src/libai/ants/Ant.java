/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libai.ants;

import java.util.Vector;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * It represent an Ant. An Ant is composed of a current position, an index where
 * it belong in the Enviroment array an a current solution
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public class Ant {
	/**
	 * Current Ant solution
	 */
	protected Vector<Integer> solution;
	/**
	 * Current Ant position, i.e.: the index of a node in the problem graph
	 */
	protected int currentPos;
	/**
	 * This Ant's position in the Enviroment's Ants array
	 */
	protected int index;

	/**
	 * Constructor. Allocates the ant's index and initial position
	 *
	 * @param index ant's index in the enviroment array
	 * @param initialPos ant's initial position (node index) in the problem
	 * graph
	 */
	protected Ant(int index, int initialPos) {
		this.index = index;
		this.currentPos = initialPos;
		this.solution = new Vector<Integer>();
	}

	/**
	 * Returns the ant's current position
	 *
	 * @return the ant's current position.
	 */
	public int getCurrentPos() {
		return currentPos;
	}

	/**
	 * Sets the ant's current position
	 *
	 * @param pos the ant's current position
	 */
	protected void setCurrentPos(int pos) {
		this.currentPos = pos;
	}

	/**
	 * Clears the ant's current solution
	 */
	public void clearSolution() {
		this.solution.clear();
	}

	/**
	 * Returns a reference to the Vector with the ant's current solution
	 *
	 * @return a reference to the Vector with the ant's current solution
	 */
	public Vector<Integer> getSolution() {
		return solution;
	}

	/**
	 * Returns a copy of the Vector with the ant's current solution
	 *
	 * @return a copy of the Vector with the ant's current solution
	 */
	public Vector<Integer> copySolution() {
		return new Vector<Integer>(this.solution);
	}

	/**
	 * Sets a Vector to be the ant's current solution
	 *
	 * @param solution a Vector with a solution
	 */
	public void setSolution(Vector<Integer> solution) {
		this.solution = solution;
	}

	/**
	 * Ads a component to the ant's current Vector solution
	 *
	 * @param Solution an integer component to be added to the current ant's
	 * solution
	 */
	public void addSolution(int Solution) {
		this.solution.add(Solution);
	}

	/**
	 * Returns the size of the ant's current solution
	 *
	 * @return an integer corresponding to the size of the ant's current
	 * solution
	 */
	public int getSolutionSize() {
		return this.solution.size();
	}

	/**
	 * Prints the ant's current solution to the standard output
	 */
	public void printSolution() {
		System.out.println("Solution of length: " + this.solution.size());
		System.out.println(this.solution.toString());
	}

	/**
	 * Return the string representation of an ant. Useful for debugging.
	 *
	 * @return An string with the ant's index an current solution.
	 */
	@Override
	public String toString() {
		return "{i = " + this.index + "," + this.solution.toString() + "}";
	}
}
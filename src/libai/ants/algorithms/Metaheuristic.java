/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Enrique Areyan <enrique3 at gmail.com>
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
package libai.ants.algorithms;

import libai.common.Matrix;
import libai.ants.AntFrameworkException;
import libai.ants.Graph;
import libai.ants.Node;
import libai.ants.Ant;
import libai.ants.Enviroment;
import java.util.*;

/**
 * This is the core class of the Ant Framework. It defines the basic operations
 * that any ACO algorithm must implement. This is an abstract class that must be
 * extended by the class that implements a particular ACO algorithm for
 * instance: AntSystem, AntColonySystem, MIX-MAX ACO, ...
 *
 * This class is composed of an instance of the Enviroment class, which holds
 * the necesary information to solve a given optimization problem
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
abstract public class Metaheuristic implements Comparator<Ant> {
	/**
	 * Instance of an Enviroment which holds all of the necesary information to
	 * solve an optimizacion problem
	 */
	protected Enviroment E;
	/**
	 * Hashtable that holds the parameters
	 */
	protected Hashtable<Integer, Double> Parameters = new Hashtable<Integer, Double>();
	/**
	 * Vector that holds the global best solution found
	 */
	protected Vector<Integer> bestSolution = new Vector<Integer>();
	/**
	 * Vector that holds the candidate list
	 */
	protected Hashtable<Integer, Vector<Node>> candidates = new Hashtable<Integer, Vector<Node>>();
	/**
	 * current iteration number
	 */
	protected int currentIterationNumber;
	/**
	 * initial node of the search
	 */
	protected static final int initialNode = 0;
	/**
	 * destination node of the search
	 */
	protected static final int destinationNode = 1;
	/**
	 * the maximum number of iterations
	 */
	protected static final int maxNumIterations = 2;
	/**
	 * Determine the relevance of the pheromone trail tau_ij when an Ant decides
	 * the next node to be incorporated in a solution. Used in the
	 * <code>decisionRule()</code> method
	 */
	protected static final int alpha = 3;
	/**
	 * Determine the relevance of the heuristic information n_ij when an Ant
	 * decides the next node to be incorporated in a solution. Used in the
	 * <code>decisionRule()</code> method
	 */
	protected static final int beta = 4;
	/**
	 * Enviroment's pheromones matrix local copy
	 */
	protected Matrix Pheromones;
	/**
	 * Enviroment's number of ants local copy
	 */
	protected int numberOfAnts;
	/**
	 * Enviroment's ant array local copy
	 */
	protected Ant[] Ants;
	/**
	 * Enviroment's graph local copy
	 */
	protected Graph Graph;
	/**
	 * Enviroment's graph number of node
	 */
	protected int numberOfNodes;

	/**
	 * Constructor. Allocates the enviroment.
	 *
	 * @param E enviroment
	 */
	protected Metaheuristic(Enviroment E) {
		this.setE(E);
	}

	/**
	 * Constructor. Empty constructor.
	 */
	protected Metaheuristic() {
	}

	/**
	 * Returns the enviroment
	 *
	 * @return the enviroment.
	 */
	public Enviroment getE() {
		return E;
	}

	/**
	 * Sets the enviroment
	 *
	 * @param E the enviroment.
	 */
	protected void setE(Enviroment E) {
		this.E = E;
		/* Make local copies of the enviroment's information in order to avoid innecesary stack calls */
		this.Ants = E.getAnts();
		this.numberOfAnts = E.getNumberOfAnts();
		this.Pheromones = E.getPheromones();
		this.Graph = E.getGraph();
	}

	/**
	 * Gets the number of nodes of the problem graph
	 *
	 * @return number of nodes of the problem graph
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	/**
	 * Sets the number of nodes of the graph problem
	 *
	 * @param numberOfNodes
	 */
	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	/**
	 * Sets a parameter
	 *
	 * @param key of the parameter
	 * @param param the value of the parameter
	 */
	protected void setParam(int key, double param) {
		this.Parameters.put(key, param);
	}

	public double getParam(int key) {
		return Parameters.get(key);
	}

	/**
	 * Updates the pheromone trail contained in the enviroment E according to
	 * some ACO algorithm specific logic
	 */
	abstract public void pheromonesUpdate();

	/**
	 * Evaporates the pheromone trail contained in the enviroment E according to
	 * some ACO algorithm specific logic
	 */
	abstract public void pheromonesEvaporation();

	/**
	 * Used by ants to decided the next node to visit.
	 *
	 * @param i source node
	 * @return destination node
	 */
	abstract public int decisionRule(int i, Vector<Integer> currentSolution);

	/**
	 * This is the body of the algorithm. Implements the fundamental logic and
	 * calls to other functions: pheromonesUpdate,pheromonesEvaporation..
	 * according to some ACO algorithm specific logic
	 *
	 * @throws AntFrameworkException
	 */
	abstract public void solve() throws AntFrameworkException;

	/**
	 * Checks whether or not all of the algorithm's parameters exists. If some
	 * obligatory parameter do not exist, the function throws an exception. If
	 * some other parameter do not exists but it is possible to set a default
	 * value, here is the place to do it.
	 *
	 * @throws Exception
	 */
	abstract public void checkParameters() throws Exception;

	/**
	 * Generate, per node in the graph, a list of candidates nodes according to
	 * some problem specific heuristic. The candidates are stored in this
	 * class's candidates hashtable
	 *
	 * @param max maximum number of candidates allowed
	 */
	abstract public void candidateList(int max);

	/**
	 * Given a node in the graph and an ant's current solution, returns a list
	 * of possible nodes to visit. This function must be implemented on the
	 * problem level, and usually obeys some problem related restricctions For
	 * instance, in the case of the TSP, this function must check which nodes
	 * have been visited and return only those wich have not been visited. For
	 * the case of the short route, this function returns all adjacents nodes to
	 * node i.
	 *
	 * @param i current node
	 * @param currentSolution ant's current solutin
	 */
	abstract public Vector<Integer> constrains(int i, Vector<Integer> currentSolution);

	/**
	 * This function is called on each iteration and it provides a way to
	 * implement centralized actions
	 */
	abstract public void daemonActions();

	/**
	 * Calculates the heuristic information. Must be implement on the problem
	 * level
	 *
	 * @param number a number being considered
	 * @return heuristic information
	 */
	abstract public double heuristicInfo(double number);

	/**
	 * Returns best solution
	 *
	 * @return best solution
	 */
	public Vector<Integer> getBestSolution() {
		return this.bestSolution;
	}

	/**
	 * Returns current iteration number
	 *
	 * @return current iteration number
	 */
	public int getCurrentIterationNumber() {
		return this.currentIterationNumber;
	}

	/**
	 * This function determines the lenght of a solution
	 *
	 * @param Solution a path constructed by an ant
	 * @return lenght of a path
	 */
	public double f(Vector<Integer> Solution) {
		double ret = 0;
		if (Solution.size() == 0) {
			return Double.MAX_VALUE;
		}
		int node_i = 0, node_j = 0;
		for (int i = 0; i < Solution.size() - 1; i++) {
			node_i = Solution.get(i);
			node_j = Solution.get(i + 1);
			ret += this.Graph.getM().position(node_i, node_j);
		}
		return ret;
	}

	/**
	 * Compares to ants' solutions based on the quality metric of f.
	 *
	 * @param o1 ant 1
	 * @param o2 ant 2
	 * @return 0 if solution of ant_1 = solution of ant_2, 1 if solution of
	 * ant_1 greater than solution of ant_2 and -1 otherwise
	 */
	public int compare(Ant o1, Ant o2) {
		//compare solutions
		Vector<Integer> sol1 = o1.getSolution(), sol2 = o2.getSolution();
		double fsol1 = this.f(sol1), fsol2 = this.f(sol2);
		if (fsol1 == fsol2) {
			return 0;
		} else if (fsol1 > fsol2) {
			return 1;
		} else {
			return -1;
		}
	}
}
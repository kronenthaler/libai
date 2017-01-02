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

import libai.ants.AntFrameworkException;
import libai.ants.Ant;
import libai.ants.Enviroment;
import java.util.Vector;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Ant Colony System algorithm.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class AntColonySystem extends Metaheuristic {
	/**
	 * If true, print debug information over System.out.println
	 */
	public static final boolean debug = false;
	/**
	 * Maximum number of candidates to be included in the candidate list
	 */
	protected static final int maxCandidates = 5;
	/**
	 * Used to balance exploration and explotation. r_0 is a number in the range
	 * [0,1] If r_0 is close to zero, the algorithm explores new paths If r_0 is
	 * close to one, the algorith exploits by favoring the best edge accoriding
	 * to the candidate list
	 */
	protected static final int r_0 = 6;
	/**
	 * r_1 is a number in the range [0,1]. For small values of ro_1, the
	 * existing pheromone concentrations on links evaporate slowly, while the
	 * influence of the best route is dampened. For larger values of ro_1,
	 * previous pheromone deposits evaporate rapidly, but the influence of th
	 * best rout is emphasized Used in
	 * <code>pheromonesUpdate()</code> and
	 * <code>pheromonesEvaporation()</code>
	 */
	protected static final int ro_1 = 7;
	/**
	 * r_2 is a number in the range [0,1]. Deals with local pheromone
	 * evaporation, in the same manner that ro_1 deals with global pheromone
	 * update
	 */
	protected static final int ro_2 = 8;
	/**
	 * An small positive constant that reinforces pheromones on local paths.
	 * Experimental results on different TSPs showed tat
	 * <code>tau_0 = 1 / (N_g * L)</code> provided good results
	 * <code>N_g</code> is the number of nodes in the graph and
	 * <code>L</code> is the lenght of a tour produced by a nearest neighbor
	 * heuristic for TSPs.
	 */
	protected static final int tau_0 = 9;


	/* class methods */
	public AntColonySystem(Enviroment E) {
		super(E);
	}

	protected AntColonySystem() {
	}

	@Override
	public void checkParameters() throws AntFrameworkException {
		/* check mandatory parameters */
		if (!this.Parameters.containsKey(AntColonySystem.initialNode)) {
			throw new AntFrameworkException("Parameter initialNode must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.destinationNode)) {
			throw new AntFrameworkException("Parameter destinationNode must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.maxNumIterations) || this.Parameters.get(AntColonySystem.maxNumIterations) <= 0) {
			throw new AntFrameworkException("Parameter maxNumIterations must exists and must be greater than zero (0)");
		}
		if (!this.Parameters.containsKey(AntColonySystem.maxCandidates)) {
			throw new AntFrameworkException("Parameter maxCandidates must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.r_0)) {
			throw new AntFrameworkException("Parameter r_0 must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.ro_1)) {
			throw new AntFrameworkException("Parameter ro_1 must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.ro_2)) {
			throw new AntFrameworkException("Parameter ro_2 must exists");
		}
		if (!this.Parameters.containsKey(AntColonySystem.tau_0)) {
			throw new AntFrameworkException("Parameter tau_0 must exists");
		}
		/* set default value to other parameters */
		this.setParam(AntColonySystem.alpha, 1);

		if (!this.Parameters.containsKey(AntColonySystem.beta)) {
			this.setParam(AntColonySystem.beta, 1);
		}
		if (AntColonySystem.debug) {
			System.out.println("Parameters = " + this.Parameters.toString());
		}
	}

	@Override
	public void daemonActions() {
	}

	@Override
	public void solve() throws AntFrameworkException {
		/* Check parameters to ensure that we have all we need before proceding */
		checkParameters();
		/* Create candidate list */
		candidateList(this.Parameters.get(AntColonySystem.maxCandidates).intValue());
		//System.out.println("Desde solve: "+this.candidates.toString());

		this.currentIterationNumber = 0;
		int currentNode, selectedNode, localInitialNode, localDestinationNode, localMaxNumIterations;
		if (AntColonySystem.debug) {
			System.out.println("Solving AntColonySystem");
		}
		//print initial pheromone trail
		//this.Pheromones.show();

		/* get parameters */
		//initial node
		localInitialNode = this.Parameters.get(AntColonySystem.initialNode).intValue();
		//destination node
		localDestinationNode = this.Parameters.get(AntColonySystem.destinationNode).intValue();
		//maxIterations
		localMaxNumIterations = this.Parameters.get(AntColonySystem.maxNumIterations).intValue();
		//sets the number of nodes in the graph
		this.setNumberOfNodes(this.Graph.getM().getColumns());
		if (AntColonySystem.debug) {
			System.out.println("localInitialNode = " + localInitialNode);
			System.out.println("localDestinationNode = " + localDestinationNode);
		}
		do {
			if (AntColonySystem.debug) {
				System.out.println("Running Ant Colony System, iteration # " + this.currentIterationNumber + " ...");
			}
			//for each ant
			for (int i = 0; i < this.numberOfAnts; i++) {
				currentNode = localInitialNode;
				Ant a = this.Ants[i];
				a.addSolution(currentNode);
				do {
					/* choose next node based on the proporional desicion rule */
					selectedNode = decisionRule(currentNode, a.getSolution());
					if (selectedNode >= 0) {
						//add the node selected to this ant's solution
						a.addSolution(selectedNode);
						/* Apply local pheromone update */
						this.localPheromonesUpdate(currentNode, selectedNode);
					}
					/* Move ant */
					currentNode = selectedNode;
				} while (currentNode != localDestinationNode && currentNode > 0);//stop when destination node its reached
                /*System.out.println("f(this.bestSolution) = "+ f(this.bestSolution));
				 if(f(a.getSolution()) < f(this.bestSolution)){
				 System.out.println("Changing best sol from "+this.bestSolution+" to "+a.getSolution());
				 this.bestSolution = a.copySolution();
				 }*/
			}
			/* Find best ant */
			//E.showAnts();
			//E.sortAnts(this);//Arrays.sort(E.Ants, this); //kronenthaler: mejor que el metodo de ordenar este en el environment
			//System.out.println("Ants ordered");
			//E.showAnts();

			if (f(this.Ants[0].getSolution()) < f(this.bestSolution)) {
				//System.out.println("Changing best sol from "+this.bestSolution+" to "+this.Ants[0].getSolution());
				this.bestSolution = this.Ants[0].copySolution();
			}
			/* pheromones evaporation */
			pheromonesEvaporation();
			/* Apply global pheromone update, only for the globally best ant */
			pheromonesUpdate();
			//print pheromones
			//this.Pheromones.show();
            /* Clear ants' solutions */
			for (int i = 0; i < this.numberOfAnts; i++) {
				this.Ants[i].clearSolution();
			}
			this.currentIterationNumber++;
		} while (this.currentIterationNumber < localMaxNumIterations);
		if (AntColonySystem.debug) {
			System.out.println("best solution = " + this.bestSolution + " , f(bestSolution) = " + f(this.bestSolution));
		}
	}

	/**
	 * Updates pheromone trail of a current local solution
	 *
	 * @param i position i of the solution
	 * @param j position j of the solution
	 */
	public void localPheromonesUpdate(int i, int j) {
		double localRo2 = this.Parameters.get(AntColonySystem.ro_2);
		double localTau0 = this.Parameters.get(AntColonySystem.tau_0);

		this.Pheromones.position(i, j, ((1 - localRo2) * this.Pheromones.position(i, j)) + (localRo2 * localTau0));
	}

	@Override
	public void pheromonesUpdate() {
		/* Update pheromones only on the best tour so far */
		//System.out.println("pheromonesUpdate of the best tour = "+this.bestSolution );
		int node_i = 0, node_j = 0;
		for (int i = 0; i < this.bestSolution.size() - 1; i++) {
			node_i = this.bestSolution.get(i);
			node_j = this.bestSolution.get(i + 1);
			this.Pheromones.increment(node_i, node_j, this.Parameters.get(AntColonySystem.ro_1) * (1 / f(this.bestSolution)));
		}
	}

	@Override
	public final void pheromonesEvaporation() {
		this.Pheromones.multiply(1 - this.Parameters.get(AntColonySystem.ro_1), this.Pheromones);
	}

	@Override
	public int decisionRule(int i, Vector<Integer> currentSolution) {
		double localR_0 = this.Parameters.get(AntColonySystem.r_0);
		int nodeJ = -1;
		/* Get possible nodes */
		Vector<Integer> possibleNodes = this.constrains(i, currentSolution);
		int cantPossibleNodes = possibleNodes.size();
		/* check if there is at least 1 possible node to be selected */
		if (cantPossibleNodes <= 0) {
			//There aren't any possible next candidates, therefore
			return -1;
		}
		/* Check to see if there exists a j from candidateList*/
		if (this.candidates.get(i).size() > 0) {
			double localAlpha = this.Parameters.get(AntColonySystem.alpha), localBeta = this.Parameters.get(AntColonySystem.beta);
			/* generate a random to see if we are going to select node from candidate list */
			double random = Math.random();
			if (random <= localR_0) {
				/*Find form candidate list */
				//System.out.println("Find from candidate list for i = "+this.candidates.get(i));
				double argmax = 0;
				/* for each candidate in the list */
				for (int j = 0; j < this.candidates.get(i).size(); j++) {
					//System.out.println("candidate j = "+ this.candidates.get(i).get(j));
					double currentArgmax = Math.pow(this.Pheromones.position(i, this.candidates.get(i).get(j).getIndex()), localAlpha) /*tau ij^alpha (alpha = 0)*/ * Math.pow(this.candidates.get(i).get(j).getHeuristicInfo(), localBeta) /* nij^beta*/;
					/* Select argmax node only if it exists in the possibleNodes vector*/
					if (currentArgmax > argmax && possibleNodes.indexOf(this.candidates.get(i).get(j).getIndex()) >= 0) {
						nodeJ = this.candidates.get(i).get(j).getIndex();
						argmax = currentArgmax;
						//System.out.println("nodeJ is in possibles nodes = "+nodeJ + "possibles = "+possibleNodes.toString());
					}
				}
				if (nodeJ == -1) {
					nodeJ = decisionRuleNotFromCandidate(i, possibleNodes);
				}
				//System.out.println("candidate nodeJ = "+nodeJ);
				return nodeJ;
			}
		}
		/* Uses same proportional rule as AntSystem, except alpha = 1 */
		nodeJ = decisionRuleNotFromCandidate(i, possibleNodes);
		//System.out.println("Find elsewhere ... result = "+nodeJ);
		return nodeJ;
	}

	/**
	 * Selects a node from all posible nodes, without taking into consideration
	 * candidates list. This is the same desicion rule from Ant System but
	 * without the alpha parameter. It is use here when there is no candidates
	 * in the list.
	 *
	 * @param i source node
	 * @param possibleNodes {@code possibleNodes}
	 * @return destination node
	 */
	public int decisionRuleNotFromCandidate(int i, Vector<Integer> possibleNodes) {
		/* counter of the number of times a node have been triying to selected a next node and maximun number of tries allowed*/
		int counter = 0, allowedNumberOfTries = 2 * this.getNumberOfNodes();
		int cantPossibleNodes = possibleNodes.size();
		/* Get alpha (desicion rule) and beta (heuristic information) parameters */
		double localAlpha = this.Parameters.get(AntSystem.alpha);
		double localBeta = this.Parameters.get(AntSystem.beta);

		double total_pheromone = 0;
		//Calculate total probability
		for (int j = 0; j < cantPossibleNodes; j++) {
			total_pheromone += Math.pow(this.Pheromones.position(i, possibleNodes.get(j)), localAlpha) * Math.pow(this.heuristicInfo(this.Graph.getM().position(i, possibleNodes.get(j))), localBeta);
		}
		do {
			if (AntColonySystem.debug) {
				System.out.println("ACS Seleccionando nodo desde " + i);
			}
			for (int j = 0; j < cantPossibleNodes; j++) {
				if (Math.random() <= ((Math.pow(this.Pheromones.position(i, possibleNodes.get(j)), localAlpha) * Math.pow(this.heuristicInfo(this.Graph.getM().position(i, possibleNodes.get(j))), localBeta)) / total_pheromone)) {
					return possibleNodes.get(j);
				}
			}
			/* check to see if the maximum number of tries have been reached */
			counter = counter + cantPossibleNodes;
			if (counter >= allowedNumberOfTries) {
				return -1;
			}
		} while (true);
	}
}
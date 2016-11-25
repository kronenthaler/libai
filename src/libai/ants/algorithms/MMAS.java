/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
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
 * First introduced by Stutzle and Hoos, this class implements the Min-Max Ant
 * System algorithm. This algorithm was conceived as a fix of the prematurely
 * stagnation behavior of AS for complex problem. Stagnation means that all ants
 * follow exactly the same path, and premature stagnation occurs when ants
 * explore little and too rapidly exploit the highest pheromone concentrations.
 * The main differences between MMAS and AS is that pheromone intensities are
 * restricted within given intervals and initial pheromoes are set to a max
 * allowed value.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
abstract public class MMAS extends Metaheuristic {
	/**
	 * If true, print debug information over System.out.println
	 */
	public static final boolean debug = false;
	/**
	 * Holds the rate at which pheromones will evaporate in the
	 * <code>pheromonesEvaporation()</code> method
	 */
	protected static final int pheromonesEvaporationRate = 5;
	/**
	 * Minimun value for the pheromone trail
	 */
	protected static final int tau_min = 6;
	/**
	 * Maximun value for the pheromone trail
	 */
	protected static final int tau_max = 7;

	/**
	 * Constructor. Allocates the enviroment.
	 *
	 * @param E enviroment
	 */
	protected MMAS(Enviroment E) {
		super(E);
	}

	/**
	 * Constructor. Empty constructor.
	 */
	protected MMAS() {
	}

	public void checkParameters() throws AntFrameworkException {
		/* check obligatory parameters */
		if (!this.Parameters.containsKey(MMAS.initialNode)) {
			throw new AntFrameworkException("Parameter initialNode must exists");
		}
		if (!this.Parameters.containsKey(MMAS.destinationNode)) {
			throw new AntFrameworkException("Parameter destinationNode must exists");
		}
		if (!this.Parameters.containsKey(MMAS.maxNumIterations) || this.Parameters.get(MMAS.maxNumIterations) <= 0) {
			throw new AntFrameworkException("Parameter maxNumIterations must exists and must be greater than zero (0)");
		}
		if (!this.Parameters.containsKey(MMAS.pheromonesEvaporationRate)) {
			throw new AntFrameworkException("Parameter pheromonesEvaporationRate must exists");
		}
		if (!this.Parameters.containsKey(MMAS.tau_min)) {
			throw new AntFrameworkException("Parameter tau_min must exists");
		}
		if (!this.Parameters.containsKey(MMAS.tau_max)) {
			throw new AntFrameworkException("Parameter tau_max must exists");
		}

	}

	public boolean stagnationPoint() {
		double total_lambda = 0, lambda_i, tau_i_min, tau_i_max;
		for (int i = 0, r = this.Graph.getM().getRows(); i < r; i++) {
			lambda_i = 0;
			tau_i_min = Double.MAX_VALUE;
			tau_i_max = 0;
			for (int j = 0, c = this.Graph.getM().getColumns(); j < c; j++) {
				if (this.Graph.getM().position(i, j) < Integer.MAX_VALUE) {
					double tau_i_j = this.Pheromones.position(i, j);
					//Determine tau_i_min
					if (tau_i_min > tau_i_j) {
						tau_i_min = tau_i_j;
					}
					//Determine tau_i_max
					if (tau_i_max < tau_i_j) {
						tau_i_max = tau_i_j;
					}
				}
			}
			if (tau_i_min == Double.MAX_VALUE) {
				tau_i_min = 0;
			}
			lambda_i = (0.05 /* lambda parameter */ * (tau_i_max - tau_i_min)) + tau_i_min;
			total_lambda = total_lambda + lambda_i;
		}
		//System.out.println("total_lambda = "+(total_lambda / (this.Graph.getM().getColumns() * this.Graph.getM().getRows())));
		if ((total_lambda / (this.Graph.getM().getColumns() * this.Graph.getM().getRows())) < 0.05) {
			//System.out.println("stagnation point");
			return true;
		}
		//System.out.println("no stagnation point");
		return false;
	}

	public int decisionRule(int i, Vector<Integer> currentSolution) {
		/* counter of the number of times a node have been triying to selected a next node and maximun number of tries allowed*/
		int counter = 0, allowedNumberOfTries = 2 * this.getNumberOfNodes();
		/* Get possible nodes */
		Vector<Integer> possibleNodes = this.constrains(i, currentSolution);
		int cantPossibleNodes = possibleNodes.size();
		/* check if there is at least 1 possible node to be selected */
		if (cantPossibleNodes <= 0) {
			//There aren't any possible next candidates, therefore
			return -1;
		}
		/* Get alpha (desicion rule) and beta (heuristic information) parameters */
		double localAlpha = this.Parameters.get(AntSystem.alpha);
		double localBeta = this.Parameters.get(AntSystem.beta);

		double total_pheromone = 0;
		//Calculate total probability
		for (int j = 0; j < cantPossibleNodes; j++) {
			total_pheromone += Math.pow(this.Pheromones.position(i, possibleNodes.get(j)), localAlpha) * Math.pow(this.heuristicInfo(this.Graph.getM().position(i, possibleNodes.get(j))), localBeta);
		}
		do {
			if (MMAS.debug) {
				System.out.println("MMAS Seleccionando nodo desde " + i);
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

	public void pheromonesUpdate() {
		/* Update pheromones only on the best tour so far */
		//System.out.println("pheromonesUpdate of the best tour = "+this.bestSolution );
		int node_i = 0, node_j = 0;
		for (int i = 0; i < this.bestSolution.size() - 1; i++) {
			node_i = this.bestSolution.get(i);
			node_j = this.bestSolution.get(i + 1);
			this.Pheromones.increment(node_i, node_j, this.Parameters.get(MMAS.pheromonesEvaporationRate) * (1 / f(this.bestSolution)));
		}
	}

	public final void pheromonesEvaporation() {
		this.Pheromones.multiply(this.Parameters.get(MMAS.pheromonesEvaporationRate), this.Pheromones);
	}

	public void solve() throws AntFrameworkException {
		/* Check parameters to ensure that we have all we need before proceding */
		this.checkParameters();
		/* Initial variables */
		this.currentIterationNumber = 0;
		int currentNode, localInitialNode, localDestinationNode, localMaxNumIterations;
		double localTauMax, localTauMin;
		/* Determine if it is iteration best or global best */
		/* get parameters */
		//initial node
		localInitialNode = (int) this.Parameters.get(MMAS.initialNode).intValue();
		//destination node
		localDestinationNode = (int) this.Parameters.get(MMAS.destinationNode).intValue();
		//maxIterations
		localMaxNumIterations = (int) this.Parameters.get(MMAS.maxNumIterations).intValue();
		//sets the number of nodes in the graph
		this.setNumberOfNodes(this.Graph.getM().getColumns());
		//tauMax
		localTauMax = this.Parameters.get(MMAS.tau_max);
		localTauMin = this.Parameters.get(MMAS.tau_min);
		//run algorithm
		do {
			/* Check if algorithm is in a stagnation point */
			if (MMAS.debug) {
				System.out.println("Running MMAS, iteration # " + this.currentIterationNumber + " ...");
			}
			if (this.stagnationPoint()) {
				for (int i = 0, r = this.Pheromones.getRows(); i < r; i++) {
					for (int j = 0, c = this.Pheromones.getColumns(); j < c; j++) {
						this.Pheromones.increment(i, j, 1 * (localTauMax - this.Pheromones.position(i, j)));
					}
				}
			}
			//print pheromones
			//this.Pheromones.show();
			//for each ant
			for (int i = 0; i < this.numberOfAnts; i++) {
				currentNode = localInitialNode;
				//System.out.println("========== Hormiga "+i+"\n");
				Ant a = this.Ants[i];
				a.addSolution(currentNode);
				do {
					/* choose next node based on the proporional desicion rule */
					currentNode = decisionRule(currentNode, a.getSolution());
					if (currentNode >= 0) {
						//add the node selected to this ant's solution
						a.addSolution(currentNode);
					}
					//System.out.println("currentNode = " +currentNode);
				} while (currentNode != localDestinationNode && currentNode > 0);//stop when destination node its reached
                /* Check if this ant's solution is the best solution */
				if (f(a.getSolution()) < f(this.bestSolution)) {
					this.bestSolution = a.copySolution();
				}
			}
			if (f(this.Ants[0].getSolution()) < f(this.bestSolution)) {
				//System.out.println("Changing best sol from "+this.bestSolution+" to "+this.Ants[0].getSolution());
				this.bestSolution = this.Ants[0].copySolution();
			}
			/* pheromones evaporation */
			pheromonesEvaporation();
			/* pheromones update */
			pheromonesUpdate();
			//print pheromones
			//this.Pheromones.show();
            /* Clear ants' solutions */
			for (int i = 0; i < this.numberOfAnts; i++) {
				this.Ants[i].clearSolution();
			}
			/* Constrict tau_ij to be in [tau_min,tau_max] for all (i,j) */
			for (int i = 0, r = this.Pheromones.getRows(); i < r; i++) {
				for (int j = 0, c = this.Pheromones.getColumns(); j < c; j++) {
					if (this.Pheromones.position(i, j) > localTauMax) {
						this.Pheromones.position(i, j, localTauMax);
					} else if (this.Pheromones.position(i, j) < localTauMin) {
						this.Pheromones.position(i, j, localTauMin);
					}
				}
			}
			/* Update tau_min */
			int Ng = this.Pheromones.getColumns();
			//System.out.println(Ng);
			//System.out.println(Math.sqrt(0.5));
			//System.out.println((1 - (Math.sqrt(0.5) * Ng)));
			localTauMin = (localTauMax * (1 - (Math.sqrt(0.5) * Ng))) / (((Ng / 2) - 1) * (Math.sqrt(0.5) * Ng));
			/* Update tau_max */
			localTauMax = (1 / (1 - this.Parameters.get(MMAS.pheromonesEvaporationRate))) * (1 / f(this.bestSolution));
			//System.out.println("localTauMin = "+localTauMin);
			//System.out.println("localTauMax = "+localTauMax);
            /* Call daemon Actions function */
			//daemonActions();
			this.currentIterationNumber++;
		} while (this.currentIterationNumber < localMaxNumIterations);
		if (MMAS.debug) {
			System.out.println("best solution = " + this.bestSolution + " , f(bestSolution) = " + f(this.bestSolution));
		}
	}

	public void daemonActions() {
	}

	public final void candidateList(int i) {
	}
}
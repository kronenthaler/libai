/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libai.ants.algorithms;

import libai.ants.AntFrameworkException;
import libai.ants.Ant;
import libai.ants.Enviroment;
import java.util.Vector;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Ant System algorithm. First developed by Dorigo, this
 * algorithm introduces a transition probability rule (implemented here in
 * <code>desicionRule()</code>) to include heuristic information. However, this
 * is still basic algorithm which will probably won't work very well for large
 * problems. Other algorithm, such as AntConolySystem introduce several upgrades
 * which greatly improve performance.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class AntSystem extends Metaheuristic {
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
	 * Constructor. Allocates the enviroment.
	 *
	 * @param E enviroment
	 */
	protected AntSystem(Enviroment E) {
		super(E);
	}

	/**
	 * Constructor. Empty constructor.
	 */
	protected AntSystem() {
	}

	/* Standard methods*/
	public void checkParameters() throws AntFrameworkException {
		/* check obligatory parameters */
		if (!this.Parameters.containsKey(AntSystem.initialNode)) {
			throw new AntFrameworkException("Parameter initialNode must exists");
		}
		if (!this.Parameters.containsKey(AntSystem.destinationNode)) {
			throw new AntFrameworkException("Parameter destinationNode must exists");
		}
		if (!this.Parameters.containsKey(AntSystem.maxNumIterations) || this.Parameters.get(AntSystem.maxNumIterations) <= 0) {
			throw new AntFrameworkException("Parameter maxNumIterations must exists and must be greater than zero (0)");
		}
		/* set default value to other parameters */
		if (!this.Parameters.containsKey(AntSystem.pheromonesEvaporationRate)) {
			this.setParam(AntSystem.pheromonesEvaporationRate, 0.8);
		}
		if (!this.Parameters.containsKey(AntSystem.alpha)) {
			this.setParam(AntSystem.alpha, 2);
		}
		if (!this.Parameters.containsKey(AntSystem.beta)) {
			this.setParam(AntSystem.beta, 5);
		}
		if (AntSystem.debug) {
			System.out.println("Parameters = " + this.Parameters.toString());
		}

	}

	public void solve() throws AntFrameworkException {
		/* Check parameters to ensure that we have all we need before proceding */
		checkParameters();

		/* Initial variables */
		this.currentIterationNumber = 0;
		int currentNode, localInitialNode, localDestinationNode, localMaxNumIterations;

		/* get parameters */
		//initial node
		localInitialNode = (int) this.Parameters.get(AntSystem.initialNode).intValue();
		//destination node
		localDestinationNode = (int) this.Parameters.get(AntSystem.destinationNode).intValue();
		//maxIterations
		localMaxNumIterations = (int) this.Parameters.get(AntSystem.maxNumIterations).intValue();
		//sets the number of nodes in the graph
		this.setNumberOfNodes(this.Graph.getM().getColumns());
		if (AntSystem.debug) {
			System.out.println("localInitialNode = " + localInitialNode);
			System.out.println("localDestinationNode = " + localDestinationNode);
		}
		//print initial pheromone trail
		//if(AntSystem.debug){
		//    this.Pheromones.show();
		//}
		//run algorithm
		do {
			if (AntSystem.debug) {
				System.out.println("Running AntSystem, iteration # " + this.currentIterationNumber + " ...");
			}
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
				} while (currentNode != localDestinationNode && currentNode > 0);//stop when destination node its reached

				/* Check if this ant's solution is the best solution */
				if (f(a.getSolution()) < f(this.bestSolution)) {
					this.bestSolution = a.copySolution();
				}
			}
			/* pheromones evaporation */
			pheromonesEvaporation();
			/* pheromones update */
			pheromonesUpdate();
			//print pheromones
			//if(AntSystem.debug){
			//    this.Pheromones.show();
			//}
            /* Clear ants' solutions */
			for (int i = 0; i < this.numberOfAnts; i++) {
				this.Ants[i].clearSolution();
			}
			/* Call daemon Actions function */
			daemonActions();
			this.currentIterationNumber++;
		} while (this.currentIterationNumber < localMaxNumIterations);
		if (AntSystem.debug) {
			System.out.println("best solution = " + this.bestSolution + " , f(bestSolution) = " + f(this.bestSolution));
		}
	}

	public void daemonActions() {
	}

	public void pheromonesUpdate() {
		double deltaTau_ij;

		for (int i = 0, r = this.Graph.getM().getRows(); i < r; i++) {
			for (int j = 0, c = this.Graph.getM().getColumns(); j < c; j++) {
				deltaTau_ij = 0.0;
				for (int k = 0, a = this.numberOfAnts; k < a; k++) {
					Vector<Integer> solution = this.Ants[k].getSolution();
					if (linkOccursInPath(i, j, solution)) {
						deltaTau_ij += antCycle(solution);
					}
				}
				this.Pheromones.increment(i, j, deltaTau_ij);
			}
		}
	}

	public void pheromonesEvaporation() {
		this.Pheromones.multiply(this.Parameters.get(AntSystem.pheromonesEvaporationRate), this.Pheromones);
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
			if (AntSystem.debug) {
				System.out.println("AS Seleccionando nodo desde " + i);
			}
			for (int j = 0; j < cantPossibleNodes; j++) {
				if (Math.random() <= ((Math.pow(this.Pheromones.position(i, possibleNodes.get(j)), localAlpha) * Math.pow(this.heuristicInfo(this.Graph.getM().position(i, possibleNodes.get(j))), localBeta)) / total_pheromone)) {
					return possibleNodes.get(j);
				}
			}
			/* check to see if the maximum number of tries have been reached */
			counter++;
			if (counter >= allowedNumberOfTries) {
				return -1;
			}
		} while (true);
	}

	public final void candidateList(int max) {
	}

	/**
	 * Determine whether a link i,j exists in a Vector
	 *
	 * @param i component i of the link
	 * @param j component j of the link
	 * @param solution a Vector
	 * @return true if link i,j exists in vector otherwise false
	 */
	public boolean linkOccursInPath(int i, int j, Vector<Integer> solution) {
		for (int k = 0; k < solution.size() - 1; k++) {
			if (solution.get(k) == i && solution.get(k + 1) == j) {
				return true;
			}
		}
		return false;
	}

	public double antCycle(Vector<Integer> Solution) {
		return 1.0 / f(Solution);
	}

	public double antDensity() {
		double ret = 0;

		return ret;
	}

	public double antQuantity() {
		double ret = 0;

		return ret;
	}
}
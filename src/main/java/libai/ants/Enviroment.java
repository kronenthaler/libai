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
package libai.ants;

import libai.common.matrix.Matrix;

import java.util.Arrays;
import java.util.Comparator;

/**
 * This class belong to the core classes of the Ant Framework.
 * <p>
 * It represent the enviroment in which an optimization problem is going to be
 * solved. An enviroment is composed of a problem Graph, a Matrix of pheromones
 * and a number of Ants. This class is highly coupled with the Metaheuristic
 * class, which utilizes this class to obtain all the necesary information to
 * solve a given optimization problem.
 *
 * @author Enrique Areyan, enrique3 at gmail.com
 * @version 1
 */
public class Enviroment {
	/**
	 * Graph wich represent the information of the problem to optimize
	 */
	protected Graph Graph;
	/**
	 * The trail of pheromones which ants will use to make desicions about where
	 * to move next in the graph
	 */
	protected Matrix Pheromones;
	protected int numberOfAnts;
	/**
	 * Array of Ants
	 */
	protected Ant[] Ants;

	/**
	 * Empty constructor.
	 */
	public Enviroment() {
	}

	/**
	 * Constructor. Allocates only the Graph
	 *
	 * @param G the graph with the problem related information
	 */
	public Enviroment(Graph G) {
		this.setGraph(G);
	}

	/**
	 * Constructor. Allocates only the Graph and the pheromone trail
	 *
	 * @param G                the graph with the problem related information
	 * @param randomPheromones {@code randomPheromones}
	 */
	public Enviroment(Graph G, boolean randomPheromones) {
		this.setGraph(G);
		if (randomPheromones) {
			this.Pheromones = Matrix.random(G.getM().getRows(), G.getM().getColumns(), false);
		}
	}

	/**
	 * Constructor alias for Enviroment(G,false). Initializates the trail
	 * pheromone to a value v.
	 *
	 * @param G object Graph
	 * @param v initial value of the pheromone trail
	 */
	public Enviroment(Graph G, double v) {
		this(G, false);
		this.Pheromones = new Matrix(G.getM().getRows(), G.getM().getColumns());
		Pheromones.setValue(v);
	}

	/**
	 * Constructor. Allocates the number of ants and the Graph. It also
	 * initialize the array of ants and the pheromone trail accoridng to the
	 * dimension of the graph.
	 *
	 * @param numberOfAnts     the number of ants to use
	 * @param G                the graph with the problem related information
	 * @param randomPheromones {@code randomPheromones}
	 * @throws AntFrameworkException if numberOfAnts is less or equal to 0
	 */
	public Enviroment(int numberOfAnts, Graph G, boolean randomPheromones) throws AntFrameworkException {
		this.setNumberOfAnts(numberOfAnts);
		this.setAnts();
		this.setGraph(G);
		if (randomPheromones) {
			this.Pheromones = Matrix.random(G.getM().getRows(), G.getM().getColumns());
		}
	}

	/**
	 * Constructor alias for Enviroment(numberOfAnts,G,false). Initializates the
	 * trail pheromone to a value v.
	 *
	 * @param numberOfAnts number of ants
	 * @param G            object Graph
	 * @param v            initial value of the pheromone trail
	 * @throws libai.ants.AntFrameworkException AntFrameworkException
	 */
	public Enviroment(int numberOfAnts, Graph G, double v) throws AntFrameworkException {
		this(numberOfAnts, G, false);
		this.Pheromones = new Matrix(G.getM().getRows(), G.getM().getColumns());
		Pheromones.setValue(v);
	}

	/**
	 * Returns the number of ants
	 *
	 * @return the number of ants.
	 */
	public int getNumberOfAnts() {
		return Ants.length;
	}

	/**
	 * Sets the number of ants of this Enviroment
	 *
	 * @param numberOfAnts {@code numberOfAnts}
	 */
	public void setNumberOfAnts(int numberOfAnts) {
		this.numberOfAnts = numberOfAnts;
	}

	/**
	 * Returns the ant located in the i position of the Ants array
	 *
	 * @param i ant's position in the array
	 * @return an Ant
	 */
	public Ant getAnt(int i) {
		return Ants[i];
	}

	/**
	 * Sets the array of ants of this enviroment.
	 *
	 * @throws libai.ants.AntFrameworkException AntFrameworkException
	 */
	public void setAnts() throws AntFrameworkException {
		if (this.numberOfAnts <= 0) {
			throw new AntFrameworkException("The number of ants cannot be less than or equal to zero (0)");
		}

		this.Ants = new Ant[this.numberOfAnts];
		for (int i = 0; i < this.numberOfAnts; i++) {
			Ants[i] = new Ant(i, 0);
		}

	}

	/**
	 * Returns the array of ants of this enviroment
	 *
	 * @return array of Ant
	 */
	public Ant[] getAnts() {
		return this.Ants;
	}

	/**
	 * Sets both the number of ants and the array of ants
	 *
	 * @param numberOfAnts number of ants
	 * @throws AntFrameworkException if number of ants is &lt;= 0
	 */
	public void setAnts(int numberOfAnts) throws AntFrameworkException {
		this.setNumberOfAnts(numberOfAnts);
		this.setAnts();
	}

	/**
	 * Prints the array of Ants to the standard output
	 */
	public void showAnts() {
		for (int i = 0; i < this.getNumberOfAnts(); i++) {
			System.out.println(this.getAnt(i));
		}
	}

	/**
	 * Returns the Pheromone trail
	 *
	 * @return a Matrix containing the Pheromones trail
	 */
	public Matrix getPheromones() {
		return Pheromones;
	}

	/**
	 * Set the pheromones trail to a fix value received as parameter This
	 * function must be call only if the graph of the enviroment has already
	 * been set
	 *
	 * @param v value to set the pheromone
	 */
	public void setPheromones(double v) {
		this.Pheromones = new Matrix(this.Graph.getM().getRows(), this.Graph.getM().getColumns());
		Pheromones.setValue(v);
	}

	/**
	 * Returns the Graph of the problem
	 *
	 * @return Graph
	 */
	public Graph getGraph() {
		return this.Graph;
	}

	/**
	 * @param Graph the Graph to set
	 */
	public void setGraph(Graph Graph) {
		this.Graph = Graph;
	}

	/**
	 * Set the pheromones trail to something received as parameter
	 *
	 * @param Pheromones the Pheromones to set
	 */
	public void setPheromones(Matrix Pheromones) {
		this.Pheromones = Pheromones;
	}

	public void sortAnts(Comparator<Ant> comparator) {
		Arrays.sort(Ants, comparator);
	}
}
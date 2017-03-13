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
import libai.ants.Enviroment;

/**
 * @author enriqueareyan
 */
abstract public class AntQ extends AntColonySystem {
	/**
	 * Learning step size
	 */
	protected static final int gamma = 10;

	public AntQ(Enviroment E) {
		super(E);
	}

	protected AntQ() {
	}

	@Override
	public void checkParameters() throws AntFrameworkException {
		super.checkParameters();
		/* check obligatory parameters */
		if (!this.Parameters.containsKey(AntQ.gamma)) {
			throw new AntFrameworkException("Parameter gamma must exists");
		}
	}

	public double getMaxNeighbor(int i) {
		int cols = this.Graph.getM().getColumns();
		double max = 0;
		//Calculate adjancent nodes
		for (int j = 0; j < cols; j++) {
			double v = this.Graph.getM().position(i, j);
			if (v < Integer.MAX_VALUE) {
				/* Node i,j are connected */
				if (this.Pheromones.position(i, j) > max) {
					max = this.Pheromones.position(i, j);
				}
			}
		}
		return max;
	}

	@Override
	public void localPheromonesUpdate(int i, int j) {
		double localRo1 = this.Parameters.get(AntQ.ro_1);
		double localGamma = this.Parameters.get(AntQ.gamma);
		//System.out.println("getMaxNeighbor = "+ getMaxNeighbor(j));
		this.Pheromones.position(i, j, ((1 - localRo1) * this.Pheromones.position(i, j)) + (localRo1 * localGamma * getMaxNeighbor(j)));
	}

	@Override
	public void pheromonesUpdate() {
		/* Update pheromones only on the best tour so far */
		//System.out.println("pheromonesUpdate of the best tour = "+this.bestSolution );
		int node_i = 0, node_j = 0;
		for (int i = 0; i < this.bestSolution.size() - 1; i++) {
			node_i = this.bestSolution.get(i);
			node_j = this.bestSolution.get(i + 1);
			this.Pheromones.increment(node_i, node_j, this.Parameters.get(AntQ.ro_1) * ((1 / f(this.bestSolution)) * getMaxNeighbor(node_j)));
		}
	}
}

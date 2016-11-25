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
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Elitits Ant System algorithm. It is essentially the same as
 * Ant System, but the arcs of the best-so-far tour are reinforced on each
 * iteration using the
 * <code>daemonActions()</code> function. Empirical results shows that this
 * behaves better that Ant System.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class ElitistAntSystem extends AntSystem {
	/**
	 * If true, print debug information over System.out.println
	 */
	//public static final boolean debug = false;
	/**
	 * Determine the weight given to the best-so-far tour Used in the
	 * <code>daemonActions()</code> method
	 */
	protected static final int epsilon = 6;

	/**
	 * Constructor. Allocates the enviroment.
	 *
	 * @param E enviroment
	 */
	protected ElitistAntSystem(Enviroment E) {
		super(E);
	}

	/**
	 * Constructor. Empty constructor.
	 */
	protected ElitistAntSystem() {
	}

	@Override
	public void checkParameters() throws AntFrameworkException {
		super.checkParameters();
		/* check obligatory parameters */
		if (!this.Parameters.containsKey(ElitistAntSystem.epsilon)) {
			throw new AntFrameworkException("Parameter epsilon must exists");
		}
	}

	@Override
	public void daemonActions() {
		/* Update pheromones only on the best tour so far */
		//System.out.println("epsilon = "+ this.Parameters.get(ElitistAntSystem.epsilon));
		//System.out.println("pheromonesUpdate of the best tour = "+this.bestSolution );
		int node_i = 0, node_j = 0;
		for (int i = 0; i < this.bestSolution.size() - 1; i++) {
			node_i = this.bestSolution.get(i);
			node_j = this.bestSolution.get(i + 1);
			this.Pheromones.increment(node_i, node_j, this.Parameters.get(ElitistAntSystem.epsilon) * (1 / f(this.bestSolution)));
		}
	}
}

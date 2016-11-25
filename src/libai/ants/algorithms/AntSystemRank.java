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

import libai.ants.Ant;
import libai.ants.Enviroment;
import java.util.*;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Rank Ant System algorithm. It is essentially the same as
 * Elitist Ant System, but the arcs of the best-so-far tour of the best
 * <code>r</code> ants are reinforced proportionally to the ant's rank Empirical
 * results shows that this behaves slightly better that Elitits Ant System.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class AntSystemRank extends ElitistAntSystem {
	/**
	 * Constructor. Allocates the enviroment.
	 *
	 * @param E enviroment
	 */
	protected AntSystemRank(Enviroment E) {
		super(E);
	}

	/**
	 * Constructor. Empty constructor.
	 */
	protected AntSystemRank() {
	}

	@Override
	public void pheromonesUpdate() {
		/* order ants*/
		E.sortAnts(this);
		/* calculated amount of elitist ants */
		int numberOfElitistAnts = Math.min(this.numberOfAnts, this.Parameters.get(AntSystemRank.epsilon).intValue() - 1);
		/* forach elitist ant */
		for (int k = 0; k < numberOfElitistAnts; k++) {
			Ant a = this.Ants[k];
			Vector<Integer> solution = a.getSolution();
			double contribution = Math.max(0, this.Parameters.get(AntSystemRank.epsilon) - 1 - k) * (1 / f(solution));
			//System.out.println("Contribution of Ant["+k+"] = "+contribution);
            /* Add contribution to each arc of this ant's solution */
			int node_i = 0, node_j = 0;
			for (int i = 0; i < solution.size() - 1; i++) {
				node_i = solution.get(i);
				node_j = solution.get(i + 1);
				this.Pheromones.increment(node_i, node_j, contribution);
			}
		}
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.libai.ants.algorithms;

import net.sf.libai.ants.*;
import java.util.*;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Rank Ant System algorithm.
 * It is essentially the same as Elitist Ant System, but the arcs of the best-so-far tour
 * of the best <code>r</code> ants are reinforced proportionally to the ant's rank
 * Empirical results shows that this behaves slightly better that Elitits Ant System.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class AntSystemRank extends ElitistAntSystem {
    /**
     *	Constructor. Allocates the enviroment.
     *	@param E enviroment
     */
    protected AntSystemRank(Enviroment E) {
        super(E);
    }

    /**
     *	Constructor. Empty constructor.
     */
    protected AntSystemRank(){}
    
    @Override
    public void pheromonesUpdate(){
        /* order ants*/
        E.sortAnts(this);
        /* calculated amount of elitist ants */
        int numberOfElitistAnts = Math.min(this.numberOfAnts, this.Parameters.get(AntSystemRank.epsilon).intValue() - 1 );
        /* forach elitist ant */
        for(int k=0;k<numberOfElitistAnts;k++){
            Ant a = this.Ants[k];
            Vector<Integer> solution = a.getSolution();
            double contribution = Math.max(0, this.Parameters.get(AntSystemRank.epsilon) - 1 - k) * (1 / f(solution));
            //System.out.println("Contribution of Ant["+k+"] = "+contribution);
            /* Add contribution to each arc of this ant's solution */
            int node_i = 0, node_j = 0;
            for(int i=0;i<solution.size()-1;i++){
                node_i = solution.get(i);
                node_j = solution.get(i+1);
                this.Pheromones.increment(node_i, node_j,contribution);
            }
        }
    }
}

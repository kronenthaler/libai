/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.libai.ants.algorithms;

import net.sf.libai.ants.*;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements the Elitits Ant System algorithm.
 * It is essentially the same as Ant System, but the arcs of the best-so-far tour
 * are reinforced on each iteration using the <code>daemonActions()</code> function.
 * Empirical results shows that this behaves better that Ant System.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public abstract class ElitistAntSystem extends AntSystem{
    /** If true, print debug information over System.out.println*/
    //public static final boolean debug = false;

    /** Determine the weight given to the best-so-far tour
     *  Used in the <code>daemonActions()</code> method*/
    protected static final int epsilon  = 6;

    /**
     *	Constructor. Allocates the enviroment.
     *	@param E enviroment
     */
    protected ElitistAntSystem(Enviroment E) {
        super(E);
    }

    /**
     *	Constructor. Empty constructor.
     */
    protected ElitistAntSystem(){}
    
    @Override
    public void checkParameters()throws AntFrameworkException{
        super.checkParameters();
        /* check obligatory parameters */
        if(!this.Parameters.containsKey(ElitistAntSystem.epsilon)){
            throw new AntFrameworkException("Parameter epsilon must exists");
        }
    }

    @Override
    public void daemonActions(){
        /* Update pheromones only on the best tour so far */
        //System.out.println("epsilon = "+ this.Parameters.get(ElitistAntSystem.epsilon));
        //System.out.println("pheromonesUpdate of the best tour = "+this.bestSolution );
        int node_i = 0, node_j = 0;
        for(int i =0;i<this.bestSolution.size()-1;i++){
            node_i = this.bestSolution.get(i);
            node_j = this.bestSolution.get(i+1);
            this.Pheromones.increment(node_i, node_j,this.Parameters.get(ElitistAntSystem.epsilon) * (1/f(this.bestSolution)));
        }
    }
}

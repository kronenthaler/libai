/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.libai.ants;
/**
 * This class belong to the core classes of the Ant Framework.
 *
 * Implements a single node of a problem Graph.
 * It stores the graph index, distance relative to other Node and heuristic information
 * It is mainly used as a handy tool to compute candidate list nodes.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 */
public class Node implements Comparable<Node>{
    /** Index of the node in the graph*/
    protected int index;

	/** Distance according to other node */
    protected double distance;

	/** Heuristic information */
    protected double heuristicInfo;

	/**
     *	Constructor. Allocates the basic information of a node.
     *	@param index index of the node
     *	@param distance distance according to another node
     *	@param heuristicInfo heuristic information
     */
    public Node(int index , double distance, double heuristicInfo) {
        this.index = index;
        this.distance = distance;
        this.heuristicInfo = heuristicInfo;
    }

    /**
     *	Implements Comparable interface. Compares two nodes.
     *	@param n another node object
     */
    public int compareTo(Node n){
        if(this.distance == n.distance){
            return 0;
        }else if(this.distance > n.distance){
            return 1;
        }else{
            return -1;
        }
    }
	
    /**
     *	Return the string representation of this node.
     *	Useful for debugging.
     *	@return An string with the index, distance and heuristic information of the node.
     */
    @Override
    public String toString(){
        return "{"+this.index+" , " + this.distance +" , " + this.heuristicInfo + "}";
    }

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the heuristicInfo
	 */
	public double getHeuristicInfo() {
		return heuristicInfo;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}
}

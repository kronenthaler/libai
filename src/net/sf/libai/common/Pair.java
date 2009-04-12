package net.sf.libai.common;

/**
 *
 * @author kronenthaler
 */
public final class Pair<V extends Comparable,K extends Comparable> implements Comparable<Pair>{
	public V first;
	public K second;
	public Pair(V x, K y){
		first = x;
		second = y;
	}

	public int compareTo(Pair o) {
		return first.compareTo(o.first);
	}

	public String toString(){
		return "("+first+","+second+")";
	}
}

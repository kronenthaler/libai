package net.sf.libai.experimental;

import java.util.*;
import net.sf.libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class Tree implements Comparable<Tree>{
	protected Attribute output;
	protected Pair<Attribute, Tree> childs[];

	protected Tree(){}

	protected Tree(Attribute root){
		output = root;
	}

	protected Tree(Pair<Attribute,Tree>[] c){
		childs = c;
	}

	protected Tree(ArrayList<Pair<Attribute,Tree>> c){
		childs = new Pair[c.size()];
		for(int i=0,n=childs.length;i<n;i++)
			childs[i] = c.get(i);
	}

	public boolean isLeaf(){
		return (childs ==null || childs.length==0) && output!=null;
	}

	public void print(){
		print("");
	}

	private void print(String indent){
		if(isLeaf()){
			System.out.println(indent+"["+output+"]");
		}else{
			for(Pair<Attribute,Tree> p : childs){
				if(p.first.isCategorical())
					System.out.println(indent+"["+p.first.name+" = "+((DiscreteAttribute)p.first).getValue()+"]");
				else
					System.out.println(indent+"["+p.first.name+(childs[0]==p?" < ":" >= ")+((ContinuousAttribute)p.first).getValue()+"]");
				p.second.print(indent+"\t");
			}
		}
	}

	@Override
	public int compareTo(Tree o) {
		return 1;
	}

	public Attribute eval(DataRecord dr){
		if(isLeaf()) return output;
		if(childs[0].first.isCategorical()){
			for(Pair<Attribute,Tree> p : childs){
				if(dr.contains(p.first))
					return p.second.eval(dr);
			}
		}else{
			for(int i=0;i<dr.getAttributeCount();i++){
				if(dr.getAttribute(i).getName().equals(childs[0].first.name)){
					if(dr.getAttribute(i).compareTo(childs[0].first) < 0)
						return childs[0].second.eval(dr);
					else
						return childs[1].second.eval(dr);
				}
			}
		}

		return null; //no prediction
	}
}

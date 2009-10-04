package net.sf.libai.trees;

import java.io.*;
import net.sf.libai.common.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 *
 * @author kronenthaler
 */
public abstract class DecisionTree implements Comparable<DecisionTree>{
	protected Attribute output; //a categorical value
	protected Pair<Attribute,DecisionTree>[] childs;

	protected DecisionTree(){}

	protected DecisionTree(Attribute root){
		output = root;
	}

	protected DecisionTree(Pair<Attribute,DecisionTree>[] c){
		childs = c;
	}
	
	public int compareTo(DecisionTree o) {
		return 1;
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
			for(Pair<Attribute,DecisionTree> p : childs){
				if(p.first.isCategorical())
					System.out.println(indent+"["+p.first.name+" = "+p.first.value+"]");
				else{
					System.out.println(indent+"["+p.first.name+(childs[0]==p?" < ":" >= ")+p.first.value+"]");
				}
				p.second.print(indent+"\t");
			}
		}
	}

	public abstract Attribute predict(RecordData r);
	protected abstract DecisionTree train(DataSet r);

	public boolean save(File path){
		try{
			PrintStream out = new PrintStream(new FileOutputStream(path));
			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<"+getClass().getSimpleName()+">");
			save(out,"\t");
			out.println("</"+getClass().getSimpleName()+">");
			out.close();
			//safe format into a XML file.
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	private void save(PrintStream out,String indent) throws IOException{
		if(isLeaf()){
			out.println(indent+"<leaf type=\""+output.value.getClass().getName()+"\" name=\""+output.name+"\"><![CDATA["+output.value+"]]></leaf>");
		}else{
			out.println(indent+"<node splits=\""+childs.length+"\">");
			for(Pair<Attribute,DecisionTree> p : childs){
				out.println(indent+"\t<split type=\""+p.first.value.getClass().getName()+"\" name=\""+p.first.name+"\"><![CDATA["+p.first.value+"]]></split>");
				p.second.save(out,indent+"\t");
			}
			out.println(indent+"</node>");
		}
	}

	protected abstract DecisionTree load(Node root);
}

package net.sf.libai.experimental;

import java.util.*;
import net.sf.libai.common.Pair;

/**
 * TODO: falta implementar el pruning, missing values.
 * Tomar lo que estaba implementado como la forma de los atributos y los save/load de los arboles
 * e incorporarlos aqui.
 * Prune: Hay que evaluar un training set y ver cuantos elementos caen en cada hoja.
 * Calcular unos errores (necesitan una tabla para la distribucion normal)
 * Ver si el valor combinado de los hijos es peor que el error estimado del padre
 * Si los hijos son peores se cambian todos los hijos por una simple hoja (la que mejor error tenga?)
 * Se sigue resolviendo la recursion hasta llegar a la raiz.
 * @author kronenthaler
 */
public class C45 implements Comparable<C45>{
	private double confidence;
	private double z ;

	protected Attribute output;
	protected Attribute mostCommonLeaf;
	protected Pair<Attribute, C45> childs[];
	protected int good=0,bad=0; //cuenta los aciertos y fallos durante el proceso de pruning
	protected double error;

	protected C45(){
		setConfidence(0.25);
	}

	protected C45(Attribute root){
		output = root;
	}

	protected C45(Pair<Attribute,C45>[] c){
		childs = c;
	}

	protected C45(ArrayList<Pair<Attribute,C45>> c){
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
			System.out.println(indent+"["+output+" {"+good+":"+bad+"}]");
		}else{
			for(Pair<Attribute,C45> p : childs){
				if(p.first.isCategorical())
					System.out.println(indent+"["+p.first.name+" = "+((DiscreteAttribute)p.first).getValue()+" {"+good+":"+bad+"}]");
				else
					System.out.println(indent+"["+p.first.name+(childs[0]==p?" < ":" >= ")+((ContinuousAttribute)p.first).getValue()+" {"+good+":"+bad+"}]");
				p.second.print(indent+"\t");
			}
		}
	}

	@Override
	public int compareTo(C45 o) {
		return 1;
	}

	public Attribute eval(DataRecord dr){
		return eval(dr, false, null);
	}
	public Attribute eval(DataRecord dr,boolean keeptrack, Attribute expected){
		if(isLeaf()){
			if(keeptrack){
				if(output.compareTo(expected)==0)
					good++;
				else
					bad++;
			}
			
			return output;
		}
		if(childs[0].first.isCategorical()){
			for(Pair<Attribute,C45> p : childs){
				if(dr.contains(p.first))
					return p.second.eval(dr,keeptrack,expected);
			}
		}else{
			for(int i=0;i<dr.getAttributeCount();i++){
				if(dr.getAttribute(i).getName().equals(childs[0].first.name)){
					if(dr.getAttribute(i).compareTo(childs[0].first) < 0)
						return childs[0].second.eval(dr,keeptrack,expected);
					else
						return childs[1].second.eval(dr,keeptrack,expected);
				}
			}
		}

		return null; //no prediction
	}

	/**
	 *	Return an unprune tree from the given dataset.
	 */
	public static C45 getInstance(DataSet ds){
		return new C45().train(ds);
	}

	/**
	 *	Return a pruned tree from the given dataset using the standard confidence of 25%
	 */
	public static C45 getInstancePrune(DataSet ds){
		return new C45().train(ds).prune(ds);
	}

	/**
	 *	Return a pruned tree from the given dataset using the specified confidence.
	 */
	public static C45 getInstancePrune(DataSet ds, double confidence){
		C45 ret = new C45();
		ret.setConfidence(confidence);
		return ret.train(ds).prune(ds);
	}

	public void setConfidence(double c){
		confidence = c;

		double a=0;
		double b=99;
		double upperLimit = doLeft(b);

		for(int index = 0;a<=3;a+=0.01,index++){
			double sum=upperLimit-doLeft(a);
			sum = 1.0-sum;
			if(sum >= c){
				z = a;
				break;
			}
		}
	}

	private double doLeft(double z){
		if(z < -6.5) return 0;
		if(z > 6.5) return 1;

		long factK=1;
		double sum=0;
		double term=1;
		int k=0;
		while(Math.abs(term)>Math.exp(-23)) {
			term=0.3989422804*Math.pow(-1,k) * Math.pow(z,k) / (2*k+1) / Math.pow(2,k) * Math.pow(z,k+1) / factK;
			sum+=term;
			k++;
			factK*=k;

		}
		sum+=1/2;
		if(sum < 0.0000000001) sum=0;

		return sum;
	}

	public C45 train(DataSet ds){
		HashSet<Integer> visited = new HashSet<Integer>();
		visited.add(ds.getOutputIndex());
		return train(ds, visited,"");
	}

	private C45 train(DataSet ds, HashSet<Integer> visited,String deep){
		if(ds.getItemsCount() == 0) return null;
		//ds.print(deep);
		
		int attributeCount = ds.getAttributeCount();
		int output = ds.getOutputIndex();
		int itemsCount = ds.getItemsCount();

		//base case: all the output are the same.
		if(ds.allTheSameOutput())
			return new C45(ds.get(0).getAttribute(output));
		
		//base case: all the attributes are the same.
		Attribute att = ds.allTheSame();
		if(att!=null)
			return new C45(att);

		//else
		
		double max = -Double.MIN_VALUE;
		int index = -1;
		int indexOfValue = -1;
		double splitValue = Double.MIN_VALUE;
		for(int i=0;i<attributeCount;i++){
			if(!visited.contains(i)){
				double g[] = ds.gain(0,itemsCount,i); //get the maximun gain ratio.

//				System.err.println(deep+"--");
//				for(double d : g)
//					System.err.println(deep+d);
//				System.err.println(deep+"--");

				if(g[1] > max){
					max = g[1];
					index = i; //split attribute
					if(g.length > 4){
						splitValue = g[4];
						indexOfValue = (int)g[5];
					}
				}
			}
		}

		ds.sortOver(index);
		ArrayList<Pair<Attribute, C45>> childs = new ArrayList<Pair<Attribute, C45>>();
		if(ds.get(0).getAttribute(index) instanceof DiscreteAttribute){
			visited.add(index); //mark as ready, avoid revisiting a nominal attribute.

			for(int i=0,hi=itemsCount;i<hi;){
				int j=0;
				int nlo = i;
				for(j=i;j<hi-1;j++,i++)
					if(!ds.get(j).getAttribute(index).equals(ds.get(j+1).getAttribute(index)))
						break;
				i++;

				childs.add(new Pair<Attribute, C45>(ds.get(nlo).getAttribute(index),
													 train(new DataSet(ds, nlo, i), visited,deep+"\t")));
			}
		}else{
			DataSet l = new DataSet(ds, 0, indexOfValue);
			DataSet r = new DataSet(ds, indexOfValue, itemsCount);
			C45 left = train(l, visited, deep+"\t");
			childs.add(new Pair<Attribute,C45>(new ContinuousAttribute(ds.get(0).getAttribute(index).getName(),splitValue), left));
			
			C45 right = train(r, visited, deep+"\t");
			childs.add(new Pair<Attribute,C45>(new ContinuousAttribute(ds.get(0).getAttribute(index).getName(),splitValue), right));
		}

		return new C45(childs);
	}

	public C45 prune(DataSet ds){
		System.out.println("confidence: "+confidence+" z:"+z);
		//first of all, evaluate all the data set over the tree, and keep track of the results.
		for(int i=0,n=ds.getItemsCount();i<n;i++){
			eval(ds.get(i),true, ds.get(i).getAttribute(ds.getOutputIndex()));
		}

		//print();

		prune();

		return this;
	}

	//TODO: revisar el prune contra un ejemplo concreto
	//por alguna razon aparentemente no esta bien el calculo de los good/bad y nunca da un valor menor
	//que el error combinado.
	private Attribute prune(){
		if(isLeaf()){
			error = error(1.0/(double)(bad+good), good/(double)(bad+good));
			return output;
		}else{
			double errorCombined = 0;
			HashMap<Attribute, Integer> freq = new HashMap<Attribute, Integer>();

			for(Pair<Attribute,C45> p : childs){
				Attribute leaf = p.second.prune();
				if(freq.get(leaf) == null) freq.put(leaf,0);
				freq.put(leaf,freq.get(leaf)+1);

				good += p.second.good;
				bad += p.second.bad;

				errorCombined += p.second.error * p.second.good;
			}
			
			int max = Integer.MIN_VALUE;
			for(Attribute att : freq.keySet()){
				if(max < freq.get(att)){
					max = freq.get(att);
					mostCommonLeaf = att;
				}
			}

			errorCombined *= 1.0/(double)(good+bad); //error combinado. calcular el error neto y ver si es peor.
			error = error(1.0/(double)(bad+good), good/(double)(bad+good));

			if(error < errorCombined){
				if(mostCommonLeaf != null){
					error = errorCombined;
					//prune this branch
					childs = null;
					output = mostCommonLeaf;
					System.out.println("prune");
				}
			}

			return mostCommonLeaf;
		}
	}

	private double error(double invN, double f){
		double z2 = z*z;
		double e = (f + (z2*invN*0.5) + z * Math.sqrt((f*invN) - (f*f*invN) + (z2*invN*invN*0.25))) / (1 + (z2*invN));
		return e;
	}
}

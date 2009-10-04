package net.sf.libai.trees;

import java.util.*;
import java.util.ArrayList;
import net.sf.libai.common.*;
/**
 *	TODO: mejorar la esuructura de datos
 *	- hacer que el algoritmo de entrenamiento use datasets en vez de arrays de records.
 *  - usar el arreglo que mantenga la frecuencia de los valores de los diferentes atributos.
 *  - crear una jerarquia de atributos: discrete<T>, continuous<T>
 *  - los calculos de entropia estan atados al dataset
 *  - los algoritmos de entrenamiento estan atados al decision tree, no al data set.
 *	@author kronenthaler
 */
public class DataSet {
	private ArrayList<RecordData> records;
	private HashMap<Comparable, Integer> freq[];
	private int outputIndex;

	public DataSet(int output){
		outputIndex = output;
		records = new ArrayList<RecordData>();
	}

	public DataSet(String path){
		//precalculate some values of the tree to speed the calculations.
		//foreach record
		//	addRecord(r)
	}

	public DataSet(int output, RecordData... recs){
		this(output);
		freq = new HashMap[recs[0].attributes.size()];
		for(RecordData r : recs)
			addRecord(r);
	}

	public void addRecord(RecordData d){
		if(freq == null) freq = new HashMap[d.attributes.size()];
		
		records.add(d);

		for(int i=0;i<d.attributes.size();i++){
			Comparable att = d.attributes.get(i).value;
			if(freq[i]==null) freq[i]=new HashMap<Comparable,Integer>();
			if(!freq[i].containsKey(att)) freq[i].put(att,0);
			freq[i].put(att,freq[i].get(att)+1);
		}
	}

	public int getRecordCount(){ return records.size(); }
	public void randomize(){ Collections.shuffle(records); }
	public RecordData getRecord(int index){ return records.get(index); }
	public HashMap<Comparable,Integer> getFreq(int index){ return freq[index]; }
	
	protected Attribute equals(){
		if(records.size() == 1) return records.get(0).attributes.get(outputIndex);
		
		//HashMap<Attribute,Integer> freq = new HashMap<Attribute,Integer>();
		Pair<Attribute, Integer> top = null;
		for(int i=1;i<records.size();i++){
			if(!records.get(i).equals(records.get(i-1))){
				return null;
			}else{
				RecordData r = records.get(i-1);

				if(top == null || top.second < freq[outputIndex].get(r.attributes.get(outputIndex).value))
					top = new Pair<Attribute, Integer>(r.attributes.get(outputIndex),freq[outputIndex].get(r.attributes.get(outputIndex).value));
			}
		}

		return top.first;
	}

	protected boolean sameOutput(){
		RecordData prev = null;
		for(RecordData r : records){
			if(prev != null){
				if(prev.attributes.get(outputIndex).compareTo(r.attributes.get(outputIndex)) != 0)
					return false;
			}
			prev = r;
		}

		return true;
	}

	public double IG(int Y, int X){
		return H(Y) - H(Y,X);
	}

	public Comparable[] IGstar(int y, int x){
		double hy = H(y);
		double max = 0;
		Comparable maxt = null;

		//System.out.println("x:"+freq[x].size());
		for(Comparable t : freq[x].keySet()){
			double aux = Hd(y,x,t);
			double temp = hy - aux;

			if(temp > max){
				max = temp;
				maxt = t;
			}
		}
		return new Comparable[]{max,maxt};
	}

	//H(Y|X:t)
	private double Hd(int y, int x, Comparable t){
		//H(Y|X < t) P(X < t) + H(Y|X >= t) P(X >= t)
		DataSet less = new DataSet(outputIndex);
		DataSet greater = new DataSet(outputIndex);
		for(RecordData r : records){
			if(r.attributes.get(x).value.compareTo(t) <= 0) less.addRecord(r);
			else greater.addRecord(r);
		}

		double pless = less.records.size() / (double)records.size();
		double pgreater = greater.records.size() / (double)records.size();

		pless *= (less.records.size()!=0)? less.H(y) : 0;
		pgreater *= (greater.records.size()!=0)? greater.H(y) : 0;
		return pless + pgreater;
	}//*/

	//H(X)
	public double H(int x){
		double acum=0;
		int n = records.size();
		for(Comparable e:freq[x].keySet()){
			double p = freq[x].get(e)/(double)n;
			acum += p*(Math.log(p)/Math.log(2));
		}

		return -acum;
	}

	//H(Y|X)
	public double H(int y, int x){
		double acum=0;
		int n = records.size();
		for(Comparable e:freq[x].keySet()){
			double p = freq[x].get(e)/(double)n; //prob of X=v
			acum += p * H(y, x, e);
		}

		return acum;
	}

	//H(Y|X=v)
	public double H(int y, int x, Attribute v){
		return H(y,x,v.value);
	}

	//H(Y|X=v)
	private double H(int y, int x, Comparable v){
		//calcular la entropia para los registros cuyo valor de x es v.
		ArrayList<RecordData> filter = new ArrayList<RecordData>();
		for(RecordData r : records)
			if(r.attributes.get(x).value.compareTo(v)==0)
				filter.add(r);

		HashMap<Comparable,Integer> values = new HashMap<Comparable,Integer>();
		for(RecordData r : filter){
			Comparable value = r.attributes.get(y).value;
			if(!values.containsKey(value)) values.put(value,0);
			values.put(value,values.get(value)+1);
		}

		double acum=0;
		int n = filter.size();
		for(Comparable e:values.keySet()){
			double p = values.get(e)/(double)n;
			acum += p*(Math.log(p)/Math.log(2));
		}

		return -acum;
	}

	/**
	 * @return the outputIndex
	 */
	public int getOutputIndex() {
		return outputIndex;
	}

	/**
	 * @param outputIndex the outputIndex to set
	 */
	public void setOutputIndex(int outputIndex) {
		this.outputIndex = outputIndex;
	}

	public String toString(){
		StringBuffer ret = new StringBuffer();
		for(RecordData r:records)
			ret.append(r).append('\n');
		return ret.toString();
	}
}

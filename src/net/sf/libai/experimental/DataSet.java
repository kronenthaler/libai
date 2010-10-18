package net.sf.libai.experimental;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class DataSet {
	private Vector<DataRecord> data;
	private int output;
	private int attributeCount;

	public DataSet(int o,DataRecord... data_){
		data = new Vector<DataRecord>();
		for(DataRecord d : data_)
			data.add(d);
		output = o;
		if(data_.length > 0)
			attributeCount = data.get(0).getAttributeCount();
	}

	public DataSet(DataSet src, int lo, int hi){
		if(lo < 0){
			System.err.println("lo < 0 |");
			src.print("");
		}
		//take the info from other dataset.
		data = new Vector<DataRecord>();
		for(int i=lo;i<hi;i++)
			data.add(src.data.get(i));

		output = src.output;
		attributeCount = src.attributeCount;
	}

	public void addRecord(DataRecord r){
		data.add(r);
		attributeCount = r.getAttributeCount();
	}

	public void sortOver(final int a){
		Collections.sort(data, new Comparator<DataRecord>(){
			@Override
			public int compare(DataRecord o1, DataRecord o2) {
				return o1.getAttribute(a).compareTo(o2.getAttribute(a));
			}
		});
	}

	private double[] info(int lo, int hi, int a){
		HashMap<String,Integer> freq = new HashMap<String, Integer>();
		for(int i=lo;i<hi;i++){
			if(!(data.get(i).getAttribute(a) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");
			
			String v = ((DiscreteAttribute)data.get(i).getAttribute(a)).getValue();
			if(freq.get(v)==null)
				freq.put(v,0);
			freq.put(v,freq.get(v)+1);
		}

		double total = hi-lo;
		double acum = 0;
		for(String e : freq.keySet()){
			int f = freq.get(e);
			if(f!=0){
				double p=(f/total);
				acum += -p*(Math.log10(p)/Math.log10(2));
			}
		}
		return new double[]{acum,total};
	}

	private double[] infoAvg(int lo,int hi, int a){
		sortOver(a);
		
		if(data.get(lo).getAttribute(a) instanceof DiscreteAttribute)
			return infoAvgDiscrete(lo,hi,a);
		else
			return infoAvgContinuous(lo,hi,a);
	}

	private double[] infoAvgDiscrete(int lo, int hi, int a){
		double acum = 0;
		double splitInfo = 0;
		double total = hi - lo;
		for(int i=lo;i<hi;){
			int j=0;
			int nlo = i;

			for(j=i;j<hi-1;j++,i++)
				if(!data.get(j).getAttribute(a).equals(data.get(j+1).getAttribute(a)))
					break;
			i++;

			double[] res = info(nlo,i,output);
			acum += res[0]*(res[1]/total);
			splitInfo += -(res[1]/total)*(Math.log10(res[1]/total)/Math.log10(2));
		}
		return new double[]{acum,splitInfo};
	}

	private double[] infoAvgContinuous(int lo, int hi, int a){
		double acum = 0;
		double splitInfo = 0;
		double total = hi - lo;

		//1. Calcular las frecuencias totales; son las mismas para todos los atributos.
		//	 cuello de botella #1, como hacer que el dataset no tenga que recalcular las frecuencias
		//	 en cada creacion => al saber cuales son las frecuencias por las que se hace el split, se
		//	 puede inicializar el nuevo dataset con esas frecuencias.
		HashMap<String, Integer> totalFreq = new HashMap<String, Integer>();
		HashMap<Double, HashMap<String, Integer>> freqAcum = new HashMap<Double, HashMap<String, Integer>>();
		HashSet<String> keys = new HashSet<String>();

		for(int i=lo;i<hi;i++){
			if(!(data.get(i).getAttribute(output) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute)data.get(i).getAttribute(output)).getValue();
			keys.add(v);
			totalFreq.put(v,0);
		}
		
		for(int i=lo;i<hi;i++){
			if(!(data.get(i).getAttribute(output) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute)data.get(i).getAttribute(output)).getValue();
			if(totalFreq.get(v)==null)
				totalFreq.put(v,0);
			totalFreq.put(v,totalFreq.get(v)+1);

			double va = ((ContinuousAttribute)data.get(i).getAttribute(a)).getValue();
			if(freqAcum.get(va)==null){
				freqAcum.put(va, new HashMap<String,Integer>());
				for(String e : keys){
					if(i-1 < 0)
						freqAcum.get(va).put(e, 0);
					else{
						double pva = ((ContinuousAttribute)data.get(i-1).getAttribute(a)).getValue();
						freqAcum.get(va).put(e,freqAcum.get(pva).get(e));
					}
				}
			}
			freqAcum.get(va).put(v, freqAcum.get(va).get(v)+1);
		}

		//System.err.println("TOTAL freqs: "+ totalFreq);
		//System.err.println("Acum freqs: "+freqAcum);

		double maxInfo = -Double.MIN_VALUE;
		double maxSplitInfo = -Double.MIN_VALUE;
		double bestSplitValue = Integer.MAX_VALUE;
		int bestIndex = -1000;

		//2. Crear una tabla para saber las frecuencias acumuladas hasta un indice. (mismo valor, misma tabla anterior.)
		for(int i=lo;i<hi;i++){
			double value = ((ContinuousAttribute)data.get(i).getAttribute(a)).getValue();

			HashMap<String, Integer> freq = freqAcum.get(value);

			double total2 = i-lo+1;
			double acum2 = 0;

			double total3 = total - total2;
			double acum3 = 0;

			for(String e : freq.keySet()){
				int f = freq.get(e);
				if(f!=0){
					double p=(f/total2);
					acum2 += -p*(Math.log10(p)/Math.log10(2));
				}

				f = totalFreq.get(e) - freq.get(e);
				if(f!=0){
					double p = f/total3;
					acum3 += -p*(Math.log10(p)/Math.log10(2));
				}
			}
			double infoA = (total2/total) * acum2;
			double infoB = (total3/total) * acum3;

			splitInfo = 0;
			if((int)total2 != 0)
				splitInfo += -(total2/total)*(Math.log10(total2/total)/Math.log10(2));

			if((int)total3 != 0)
				splitInfo += -(total3/total)*(Math.log10(total3/total)/Math.log10(2));

			if(splitInfo > maxSplitInfo){
				maxInfo = infoA+infoB;
				int k=0;
				for(k=i+1;k<hi;k++){
					double nextValue = ((ContinuousAttribute)data.get(k).getAttribute(a)).getValue();
					if(value!=nextValue){
						bestSplitValue = (value + nextValue)/2;
						bestIndex = k;
						break;
					}
				}
				
				if(k==hi){
					bestSplitValue = value;
					bestIndex = hi-1;
				}

				maxSplitInfo = splitInfo;
			}
		}
		return new double[]{maxInfo, maxSplitInfo, bestSplitValue, bestIndex};
	}

	public double[] gain(int lo, int hi, int a){
		double info[] = infoAvg(lo,hi,a);
		double gain = info(0, data.size(), output)[0] - info[0];
		double gainRatio = gain / info[1];

		if(info.length>2)
			return new double[]{gain, gainRatio, info[0], info[1], info[2], info[3]};
		else
			return new double[]{gain, gainRatio, info[0], info[1]};
	}

	public int getOutputIndex(){ return output; }
	public int getAttributeCount(){ return attributeCount; }
	public int getItemsCount(){ return data.size(); }
	public DataRecord get(int index){ return data.get(index); }

	public boolean allTheSameOutput(){
		for(int i=0;i<data.size();i++){
			if(!data.get(i).getAttribute(output).equals(data.get(0).getAttribute(output)))
				return false;
		}
		return true;
	}

	public Attribute allTheSame(){
		HashMap<String,Integer> freq = new HashMap<String, Integer>();
		
		for(int i=0;i<data.size();i++){
			for(int j=0;j<attributeCount;j++){
				if(j!=output &&
				   !data.get(i).getAttribute(j).equals(data.get(0).getAttribute(j)))
				return null;
			}
			
			if(!(data.get(i).getAttribute(output) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");
			
			String v = ((DiscreteAttribute)data.get(i).getAttribute(output)).getValue();
			if(freq.get(v)==null)
				freq.put(v,0);
			freq.put(v,freq.get(v)+1);
		}

		int max = Integer.MIN_VALUE;
		String mostCommon = null;
		for(String e : freq.keySet()){
			if(freq.get(e) > max){
				max = freq.get(e);
				mostCommon = e;
			}
		}
		return new DiscreteAttribute(((DiscreteAttribute)data.get(0).getAttribute(output)).getName(),
									 mostCommon);
	}

	public void print(String deep){
		System.err.println(deep+"DATASET");
		for(int i=0;i<data.size();i++){
			System.err.print(deep);
			for(int j=0;j<attributeCount;j++)
				System.err.print(data.get(i).getAttribute(j)+"\t\t");
			System.err.println("");
		}
		System.err.println(deep+"END DATASET");
	}
}
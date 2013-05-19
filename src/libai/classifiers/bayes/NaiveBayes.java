package libai.classifiers.bayes;

import libai.classifiers.*;

/**
 *
 * @author kronenthaler
 */
public class NaiveBayes {
	protected DataSet ds;
	
	public NaiveBayes train(DataSet ds){
		this.ds = ds;
		return this;
	}
	
	//calculate the maximum posterior probability this data record (x) in the data set
	//P(Ci|x) > P(Cj|x) 1 <= j < m, i!=j
	public Attribute eval(DataRecord x){
		Attribute winner = null;
		double max = -Double.MAX_VALUE;
		for(Attribute c : ds.getClasses()){
			double tmp = P(c, x);
			if(tmp > max){
				max = tmp;
				winner = c;
			}
		}
		return winner;
	}
	
	//P(H|x) = P(x|H)P(H) / P(x)
	//relaxed calculation of P(H|x). the exact value is not necessary, just to know which class
	//has the highest value.
	private double P(Attribute h, DataRecord x){
		return P(x,h)*P(h);
	}
	
	private double P(DataRecord x, Attribute h){
		double p = 1;
		int offset = -1;
		int length = 0;
		
		ds.sortOver(ds.getOutputIndex());
		for(int i=0;i<ds.getItemsCount();i++){
			if(offset == -1 && 
			   ds.get(i).getAttribute(ds.getOutputIndex()).equals(h)){
				offset = i;
			}
			if(offset != -1){
				if(ds.get(i).getAttribute(ds.getOutputIndex()).equals(h)){
					length++;
				}else
					break;//done looking
			}
		}
				
		//look for all records in ds with class h.
		for(int k=0,n=x.getAttributeCount();k<n;k++){
			Attribute attr = x.getAttribute(k);
			if(attr.isCategorical()){
				p *= (count(attr, k, offset, length)+1) / (double)(ds.getOutputFrequency(h)+1);
			}else{
				p *= gaussian((ContinuousAttribute)attr, k, offset, length);
			}
		}
		return p;
	}

	//laplace's correction. x+1 / |d|+|c|
	private double P(Attribute h){
		return (ds.getOutputFrequency(h)+1) / (double)(ds.getItemsCount() + ds.getClasses().size());
	}
	
	private int count(Attribute xk, int k, int offset, int length){
		int count = 0;
		for(int i=offset;i<offset+length;i++){
			if(ds.get(i).getAttribute(k).equals(xk))
				count++;
		}
		return count;
	}
	
	private double gaussian(ContinuousAttribute xk, int k, int offset, int length){
		double mean = 0;
		double sd = 0;
		
		for(int i=offset;i<offset+length;i++){
			mean += ((ContinuousAttribute)ds.get(i).getAttribute(k)).getValue();
			sd += Math.pow(((ContinuousAttribute)ds.get(i).getAttribute(k)).getValue(),2);
		}
		sd = (sd - ((mean*mean)/(double)length));
		mean /= (double)length;
		sd /= (double)(length-1);
		
		return Math.exp(-(Math.pow(xk.getValue()-mean,2)/(2*sd))) * (1/(Math.sqrt(2*Math.PI*sd)));
	}
	
	public static void main(String arg[]){
		/*DataSet ds = new DataSet(4, new String[]{"age", "income", "student", "credit_rating", "buys_computer"},
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("h"),new DiscreteAttribute("n"),new DiscreteAttribute("f"),new DiscreteAttribute("n")),//1
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("h"),new DiscreteAttribute("n"),new DiscreteAttribute("e"),new DiscreteAttribute("n")),//2
				new DataRecord(new DiscreteAttribute("m"), new DiscreteAttribute("h"),new DiscreteAttribute("n"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//3
				new DataRecord(new DiscreteAttribute("s"), new DiscreteAttribute("m"),new DiscreteAttribute("n"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//4
				new DataRecord(new DiscreteAttribute("s"), new DiscreteAttribute("l"),new DiscreteAttribute("y"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//5
				new DataRecord(new DiscreteAttribute("s"), new DiscreteAttribute("l"),new DiscreteAttribute("y"),new DiscreteAttribute("e"),new DiscreteAttribute("n")),//6
				new DataRecord(new DiscreteAttribute("m"), new DiscreteAttribute("l"),new DiscreteAttribute("y"),new DiscreteAttribute("e"),new DiscreteAttribute("y")),//7
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("m"),new DiscreteAttribute("n"),new DiscreteAttribute("f"),new DiscreteAttribute("n")),//8
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("l"),new DiscreteAttribute("y"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//9
				new DataRecord(new DiscreteAttribute("s"), new DiscreteAttribute("m"),new DiscreteAttribute("y"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//10
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("m"),new DiscreteAttribute("y"),new DiscreteAttribute("e"),new DiscreteAttribute("y")),//11
				new DataRecord(new DiscreteAttribute("m"), new DiscreteAttribute("m"),new DiscreteAttribute("n"),new DiscreteAttribute("e"),new DiscreteAttribute("y")),//12
				new DataRecord(new DiscreteAttribute("m"), new DiscreteAttribute("h"),new DiscreteAttribute("y"),new DiscreteAttribute("f"),new DiscreteAttribute("y")),//13
				new DataRecord(new DiscreteAttribute("s"), new DiscreteAttribute("m"),new DiscreteAttribute("n"),new DiscreteAttribute("e"),new DiscreteAttribute("n"))//14
				);
		System.out.println(
				new NaiveBayes().eval(ds,
				new DataRecord(new DiscreteAttribute("y"), new DiscreteAttribute("m"),new DiscreteAttribute("y"),new DiscreteAttribute("f"))));
		//*/
		DataSet ds2 = new DataSet(3, 
				new DataRecord(new ContinuousAttribute(6), new ContinuousAttribute(180), new ContinuousAttribute(12)	,new DiscreteAttribute("male")  ),
				new DataRecord(new ContinuousAttribute(5.92), new ContinuousAttribute(190), new ContinuousAttribute(11)	,new DiscreteAttribute("male")  ),
				new DataRecord(new ContinuousAttribute(5.58), new ContinuousAttribute(170), new ContinuousAttribute(12)	,new DiscreteAttribute("male")  ),
				new DataRecord(new ContinuousAttribute(5.92), new ContinuousAttribute(165), new ContinuousAttribute(10)	,new DiscreteAttribute("male")  ),
				new DataRecord(new ContinuousAttribute(5), new ContinuousAttribute(100), new ContinuousAttribute(6)	,new DiscreteAttribute("female")),
				new DataRecord(new ContinuousAttribute(5.5), new ContinuousAttribute(150), new ContinuousAttribute(8)	,new DiscreteAttribute("female")),
				new DataRecord(new ContinuousAttribute(5.42), new ContinuousAttribute(130), new ContinuousAttribute(7)	,new DiscreteAttribute("female")),
				new DataRecord(new ContinuousAttribute(5.75), new ContinuousAttribute(150), new ContinuousAttribute(9)	,new DiscreteAttribute("female")));
		
		System.out.println(
				new NaiveBayes().train(ds2).eval(new DataRecord(new ContinuousAttribute(6), new ContinuousAttribute(130), new ContinuousAttribute(8))));
	}
}

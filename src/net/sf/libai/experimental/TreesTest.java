package net.sf.libai.experimental;


import java.io.*;
import java.util.*;
import java.math.*;

/**
 *
 * @author kronenthaler
 */
public class TreesTest {
	String[][] data;
	int output;
	
	public static void main(String[] args) {
        new TreesTest().doit();
    }

	public void doit(){
		/*data = new String[][]{{"sunny",		"hot",	"high",		"false",	"no"},
							  {"sunny",		"hot",	"high",		"true",		"no"},
							  {"overcast",	"hot",	"high",		"false",	"yes"},
							  {"rainy",		"mild",	"high",		"false",	"yes"},
							  {"rainy",		"cool",	"normal",	"false",	"yes"},
							  {"rainy",		"cool",	"normal",	"true",		"no"},
							  {"overcast",	"cool",	"normal",	"true",		"yes"},
							  {"sunny",		"mild",	"high",		"false",	"no"},
							  {"sunny",		"cool",	"normal",	"false",	"yes"},
							  {"rainy",		"mild",	"normal",	"false",	"yes"},
							  {"sunny",		"mild",	"normal",	"true",		"yes"},
							  {"overcast",	"mild",	"high",		"true",		"yes"},
							  {"overcast",	"hot",	"normal",	"false",	"yes"},
							  {"rainy",		"mild",	"high",		"true",		"no"},
		};*/

		/*DataSet ds = new DataSet(4,
								  new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","no")),
								  new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")),
								  new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")),
								  new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","no")),
								  new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","cool"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","sunny"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","overcast"), new DiscreteAttribute("temp","hot"), new DiscreteAttribute("humidity","normal"), new DiscreteAttribute("wind","false"), new DiscreteAttribute("play","yes")),
								  new DataRecord(new DiscreteAttribute("outlook","rainy"), new DiscreteAttribute("temp","mild"), new DiscreteAttribute("humidity","high"), new DiscreteAttribute("wind","true"), new DiscreteAttribute("play","no")));
		//*/

		/*DataSet ds = new DataSet(0,
								new DataRecord(new DiscreteAttribute("outcome","no"), new ContinuousAttribute("val",65)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",64)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",68)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",69)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",70)),
								new DataRecord(new DiscreteAttribute("outcome","no"), new ContinuousAttribute("val",71)),
								new DataRecord(new DiscreteAttribute("outcome","no"), new ContinuousAttribute("val",72)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",72)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",75)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",75)),
								new DataRecord(new DiscreteAttribute("outcome","no"), new ContinuousAttribute("val",80)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",81)),
								new DataRecord(new DiscreteAttribute("outcome","yes"), new ContinuousAttribute("val",83)),
								new DataRecord(new DiscreteAttribute("outcome","no"), new ContinuousAttribute("val",85))
				);
		//*/

		/*ID3 id3 = new ID3();
		Tree t = id3.train(ds);
		t.print();
		ds.sortOver(0);
		int i=0;
		for(;i<ds.getItemsCount();i++){
			System.err.println(t.eval(ds.get(i))+" == "+ds.get(i).getAttribute(0));
			if(t.eval(ds.get(i)).compareTo(ds.get(i).getAttribute(0))!=0)
				System.out.println(ds.get(i));
		}*/
		

		try{
			DataSet ds = new DataSet(10);
			BufferedReader in = new BufferedReader(new FileReader("magic.data"));
			while(true){
				String line = in.readLine();
				if(line == null)break;
				String toks[] = line.split(",");
				Attribute attrs[] = new Attribute[toks.length];
				for(int i=0;i<attrs.length;i++){
					try{
						attrs[i] = new ContinuousAttribute(""+i,Double.parseDouble(toks[i]));
					}catch(NumberFormatException e1){
						attrs[i] = new DiscreteAttribute(""+i,toks[i]);
					}
				}
				ds.addRecord(new DataRecord(attrs));
			}
			in.close();
			//*/

			//DataSet ret[] = ds.generateTrainningTestSets(0.3);
			//System.out.println(ret[0].getRecordCount()+" "+ret[1].getRecordCount());

			C45 tree = C45.getInstance(ds);
			//tree.print();
			int error = 0;
			for(int i=0;i<ds.getItemsCount();i++){
				DataRecord r = ds.get(i);
				if((tree.eval(r).compareTo(r.getAttribute(ds.getOutputIndex()))!=0)){
					System.out.println(r+"=> "+tree.eval(r)+" "+(tree.eval(r).compareTo(r.getAttribute(ds.getOutputIndex()))==0));
					error++;
				}
			}
			System.out.println("error: "+error/(double)ds.getItemsCount());

			//tree = C45.getInstancePrune(ds);
			tree.setConfidence(0.25);
			tree.prune(ds);
			//tree.print();
			error = 0;
			for(int i=0;i<ds.getItemsCount();i++){
				DataRecord r = ds.get(i);
				if((tree.eval(r).compareTo(r.getAttribute(ds.getOutputIndex()))!=0)){
					System.out.println(r+"=> "+tree.eval(r)+" "+(tree.eval(r).compareTo(r.getAttribute(ds.getOutputIndex()))==0));
					error++;
				}
			}
			System.out.println("error: "+error/(double)ds.getItemsCount());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void printData(){
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length;j++)
				System.out.print(data[i][j]+"\t");
			System.out.println("");
		}
	}
}
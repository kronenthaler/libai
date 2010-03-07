package net.sf.libai.trees;

import java.io.*;
import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class Test {
	public static void main(String a[]){
		DataSet ds = new DataSet(10);
		DataSet test = new DataSet(10);
		DataSet ig = new DataSet(10);
		DataSet ih = new DataSet(10);

		try{
			BufferedReader in = new BufferedReader(new FileReader("magic.data"));
			while(true){
				String line = in.readLine();
				if(line == null)break;
				String toks[] = line.split(",");
				Attribute attrs[] = new Attribute[toks.length];
				for(int i=0;i<attrs.length;i++){
					//try{
					//	attrs[i] = new Attribute(""+i,Integer.parseInt(toks[i]));
					//}catch(NumberFormatException e){
						try{
							attrs[i] = new Attribute(""+i,Double.parseDouble(toks[i]));
						}catch(NumberFormatException e1){
							attrs[i] = new Attribute(""+i,toks[i]);
						}
					//}
				}
				ds.addRecord(new RecordData(attrs));
			}
			in.close();

			DataSet ret[] = ds.generateTrainningTestSets(0.3);
			System.out.println(ret[0].getRecordCount()+" "+ret[1].getRecordCount());

			DecisionTree tree = C4_5.getInstance(ret[0]);
			tree.print();
			tree.save(new File("magic.c45"));
			int error = 0;

			for(int i=0;i<ret[1].getRecordCount();i++){
				RecordData r = ret[1].getRecord(i);
				if((tree.predict(r).compareTo(r.attributes.get(ds.getOutputIndex()))!=0)){
					System.out.println(r+"=> "+tree.predict(r)+" "+(tree.predict(r).compareTo(r.attributes.get(ds.getOutputIndex()))==0));
					error++;
				}
			}
			System.out.println("error: "+error/(double)ret[1].getRecordCount());
		}catch(Exception e){
			e.printStackTrace();
		}

		/*DataSet ds = new DataSet(4,
				new RecordData(	new Attribute<String>("District","Suburban"), new Attribute<String>("House Type","Detached"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Nothing")),
				new RecordData(	new Attribute<String>("District","Suburban"), new Attribute<String>("House Type","Detached"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Nothing")),
				new RecordData(	new Attribute<String>("District","Rural"), new Attribute<String>("House Type","Detached"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Urban"), new Attribute<String>("House Type","Semi-detached"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Urban"), new Attribute<String>("House Type","Semi-detached"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Urban"), new Attribute<String>("House Type","Semi-detached"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Nothing")),
				new RecordData(	new Attribute<String>("District","Rural"), new Attribute<String>("House Type","Semi-detached"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Suburban"), new Attribute<String>("House Type","Terrace"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Nothing")),
				new RecordData(	new Attribute<String>("District","Suburban"), new Attribute<String>("House Type","Semi-detached"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Urban"), new Attribute<String>("House Type","Terrace"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Suburban"), new Attribute<String>("House Type","Terrace"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Rural"), new Attribute<String>("House Type","Terrace"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Rural"), new Attribute<String>("House Type","Detached"), new Attribute<String>("Income","Low"), new Attribute<String>("Previous Customer","No"), new Attribute<String>("Outcome","Responded")),
				new RecordData(	new Attribute<String>("District","Urban"), new Attribute<String>("House Type","Terrace"), new Attribute<String>("Income","High"), new Attribute<String>("Previous Customer","Yes"), new Attribute<String>("Outcome","Nothing")));

		DecisionTree tree = ID3.getInstance(ds);
		tree.inOrden();
		tree.save(new File("dt.id3"));
		tree = ID3.getInstance(new File("dt.id3"));
		tree.inOrden();

		for(RecordData r : ds.records)
			System.out.println(tree.predict(r)+" / "+r.attributes.get(ds.outputIndex));
		*/

		/*DataSet ds2 = new DataSet(4,
				new RecordData(new Attribute<String>("outlook","overcast"), new Attribute<Integer>("temperature",64), new Attribute<Integer>("humidity",65), new Attribute<String>("windy","true"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","rain"), new Attribute<Integer>("temperature",65), new Attribute<Integer>("humidity",70), new Attribute<String>("windy","true"), new Attribute<String>("play","Don't_Play")),
				new RecordData(new Attribute<String>("outlook","rain"), new Attribute<Integer>("temperature",68), new Attribute<Integer>("humidity",80), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","sunny"), new Attribute<Integer>("temperature",69), new Attribute<Integer>("humidity",70), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","rain"), new Attribute<Integer>("temperature",70), new Attribute<Integer>("humidity",96), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","rain"), new Attribute<Integer>("temperature",71), new Attribute<Integer>("humidity",80), new Attribute<String>("windy","true"), new Attribute<String>("play","Don't_Play")),
				new RecordData(new Attribute<String>("outlook","sunny"), new Attribute<Integer>("temperature",72), new Attribute<Integer>("humidity",95), new Attribute<String>("windy","false"), new Attribute<String>("play","Don't_Play")),
				new RecordData(new Attribute<String>("outlook","overcast"), new Attribute<Integer>("temperature",72), new Attribute<Integer>("humidity",90), new Attribute<String>("windy","true"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","rain"), new Attribute<Integer>("temperature",75), new Attribute<Integer>("humidity",80), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","sunny"), new Attribute<Integer>("temperature",75), new Attribute<Integer>("humidity",70), new Attribute<String>("windy","true"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","sunny"), new Attribute<Integer>("temperature",80), new Attribute<Integer>("humidity",90), new Attribute<String>("windy","true"), new Attribute<String>("play","Don't_Play")),
				new RecordData(new Attribute<String>("outlook","overcast"), new Attribute<Integer>("temperature",81), new Attribute<Integer>("humidity",75), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","overcast"), new Attribute<Integer>("temperature",83), new Attribute<Integer>("humidity",78), new Attribute<String>("windy","false"), new Attribute<String>("play","Play")),
				new RecordData(new Attribute<String>("outlook","sunny"), new Attribute<Integer>("temperature",85), new Attribute<Integer>("humidity",85), new Attribute<String>("windy","false"), new Attribute<String>("play","Don't_Play"))
				);

		DecisionTree tree2 = C4_5.getInstance(ds2);
		tree2.print();*/

		//for(RecordData r : ds2.records)
		//	System.out.println(tree2.predict(r)+" / "+r.attributes.get(ds2.outputIndex) +" = "+ (tree2.predict(r).compareTo(r.attributes.get(ds2.outputIndex))==0));

		//tree2.save(new File("dt.c45"));
		//tree2 = C4_5.getInstance(new File("dt.c45"));
		//tree2.print();//*/
	}
}

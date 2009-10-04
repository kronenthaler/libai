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
				//if(attrs[4].value.compareTo("Iris-virginica")==0) iv.addRecord(new RecordData(attrs));
				//if(attrs[4].value.compareTo("Iris-versicolor")==0) ive.addRecord(new RecordData(attrs));
				//if(attrs[4].value.compareTo("Iris-setosa")==0) is.addRecord(new RecordData(attrs));
				if(attrs[10].value.compareTo("g")==0) ig.addRecord(new RecordData(attrs));
				if(attrs[10].value.compareTo("h")==0) ih.addRecord(new RecordData(attrs));
			}
			in.close();

			//iv.randomize();
			//ive.randomize();
			//is.randomize();
			//ig.randomize();
			//ih.randomize();
			//picar el data set en un conjunto de 70/30 para train y test.

			int i=0;
			/*for(;i<iv.getRecordCount()*0.7;i++)
				ds.addRecord(iv.getRecord(i));
			for(;i<iv.getRecordCount();i++)
				test.addRecord(iv.getRecord(i));

			for(i=0;i<ive.getRecordCount()*0.7;i++)
				ds.addRecord(ive.getRecord(i));
			for(;i<ive.getRecordCount();i++)
				test.addRecord(ive.getRecord(i));

			for(i=0;i<is.getRecordCount()*0.7;i++)
				ds.addRecord(is.getRecord(i));
			for(;i<is.getRecordCount();i++)
				test.addRecord(is.getRecord(i));*/

			for(i=0;i<ig.getRecordCount()*0.2;i++)
				ds.addRecord(ig.getRecord(i));
			for(;i<ig.getRecordCount();i++)
				test.addRecord(ig.getRecord(i));

			for(i=0;i<ih.getRecordCount()*0.2;i++)
				ds.addRecord(ih.getRecord(i));
			for(;i<ih.getRecordCount();i++)
				test.addRecord(ih.getRecord(i));

			DecisionTree tree = C4_5.getInstance(ds);
			tree.print();
			tree.save(new File("magic.c45"));
			int error = 0;

			for(i=0;i<test.getRecordCount();i++){
				RecordData r = test.getRecord(i);
				if((tree.predict(r).compareTo(r.attributes.get(ds.getOutputIndex()))!=0)){
					System.out.println(r+"=> "+tree.predict(r)+" "+(tree.predict(r).compareTo(r.attributes.get(ds.getOutputIndex()))==0));
					error++;
				}
			}
			System.out.println("error: "+error/(double)test.getRecordCount());
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

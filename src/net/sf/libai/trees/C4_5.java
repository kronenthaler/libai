package net.sf.libai.trees;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import net.sf.libai.common.*;


/**
 *
 * @author kronenthaler
 */
public class C4_5 extends DecisionTree{
	
	private C4_5(){}
	private C4_5(Attribute att){ super(att); }
	private C4_5(Pair<Attribute,DecisionTree>[] c){ super(c); }

	public static DecisionTree getInstance(DataSet ds){
		return new C4_5().train(ds);
	}

	public static DecisionTree getInstance(File path){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new FileInputStream(path));
			NodeList root = doc.getElementsByTagName("C4_5").item(0).getChildNodes();
			
			for(int i=0;i<root.getLength();i++){
				Node current = root.item(i);
				if(current.getNodeName().equals("node") || current.getNodeName().equals("leaf")){
					return new C4_5().load(current);
				}
			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Attribute predict(RecordData r) {
		if(isLeaf()) return output;
		if(childs[0].first.isCategorical()){
			for(Pair<Attribute,DecisionTree> p : childs){
				if(r.attributes.contains(p.first))
					return p.second.predict(r);
			}
		}else{
			for(int i=0;i<r.attributes.size();i++){
				if(r.attributes.get(i).name.equals(childs[0].first.name)){
					if(r.attributes.get(i).compareTo(childs[0].first) < 0)
						return childs[0].second.predict(r);
					else
						return childs[1].second.predict(r);
				}
			}
		}

		return null; //no prediction
	}

	@Override
	protected DecisionTree train(DataSet r) {
		System.out.println("r.size()="+r.getRecordCount());
		if(r.getRecordCount()==0) return null;

		//recorrer los registro y ver si todos los datos son iguales.
		Attribute aux = r.equals();
		if(aux != null) return new C4_5(aux); //retornar el atributo de salida mas comun.

		//si se me acabaron los attributos para picar => generar una hoja con el valor mas frecuente.
		//if(visited.size()+1 == r.getRecord(0).attributes.size())
		//	return new C4_5();

		//si todos tienen la misma salida retornar un nodo hoja con esa salida.
		if(r.sameOutput()) return new C4_5(r.getRecord(0).attributes.get(r.getOutputIndex()));

		//caso general.
		int maxj = -1;
		double max = 0;
		Comparable t = null;
		Attribute current=null;

		for(int j=0,m=r.getRecord(0).attributes.size();j<m;j++){//para cada campo, si el atributo es categorico.
			//calcular el IG(Y|Xj) y guardar el j con el maximo IG(Y|Xj)
			if(j == r.getOutputIndex()) continue;
			current = r.getRecord(0).attributes.get(j);
			if(current.isCategorical()){
				double tempj = r.IG(r.getOutputIndex(), j);
				if(tempj > max){
					max = tempj;
					maxj = j;
				}
			}else{
				Comparable result[] = r.IGstar(r.getOutputIndex(), j);
				if((Double)result[0] > max){
					max = (Double)result[0];
					maxj = j;
					t = (result[1]);
				}
			}
		}

		if(r.getRecord(0).attributes.get(maxj).isCategorical()){
			HashMap<Comparable,Integer> values = r.getFreq(maxj);
			Pair<Attribute, DecisionTree> childs[] = new Pair[values.size()];
			int childV = 0;
			for(Comparable e:values.keySet()){
				DataSet subset = new DataSet(r.getOutputIndex());
				for(int i=0;i<r.getRecordCount();i++){
					RecordData r1 = r.getRecord(i);
					if(r1.attributes.get(maxj).value.compareTo(e)==0){
						subset.addRecord(r1);
					}
				}
				childs[childV++] = new Pair<Attribute,DecisionTree>(subset.getRecord(0).attributes.get(maxj),train(subset));
			}

			return new C4_5(childs);
		}else{
			System.out.println(r);
			DataSet Xlow = new DataSet(r.getOutputIndex());
			DataSet Xhi = new DataSet(r.getOutputIndex());
			for(int i=0;i<r.getRecordCount();i++){
				if(r.getRecord(i).attributes.get(maxj).value.compareTo(t) <= 0) Xlow.addRecord(r.getRecord(i));
				else Xhi.addRecord(r.getRecord(i));
			}

			DecisionTree childLow = train(Xlow);
			DecisionTree childHi = train(Xhi);
			Attribute spliter = new Attribute(r.getRecord(0).attributes.get(maxj).name,t);
			return new C4_5(new Pair[]{ new Pair<Attribute,DecisionTree>(spliter,childLow),
										new Pair<Attribute,DecisionTree>(spliter,childHi) });
		}
	}

	protected DecisionTree load(Node root){
		if(root.getNodeName().equals("node")){
			Pair<Attribute,DecisionTree> childs[] = new Pair[Integer.parseInt(root.getAttributes().getNamedItem("splits").getTextContent())];
			NodeList aux = root.getChildNodes();
			int currentChild=0;
			for(int i=0,n=aux.getLength();i<n;i++){
				Node current = aux.item(i);
				if(current.getNodeName().equals("split")){
					Attribute att = Attribute.load(current);
					for(;i<n;i++)
						if((current=aux.item(i)).getNodeName().equals("leaf") || current.getNodeName().equals("node")) break;
					childs[currentChild++] = new Pair<Attribute,DecisionTree>(att,load(current));
				}
			}
			return new C4_5(childs);
		}else if(root.getNodeName().equals("leaf")){
			return new C4_5(Attribute.load(root));
		}
		return null;
	}
}

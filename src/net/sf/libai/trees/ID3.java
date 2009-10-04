package net.sf.libai.trees;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import net.sf.libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class ID3 extends DecisionTree{
	private ID3(){}
	private ID3(Attribute att){ super(att); }
	private ID3(Pair<Attribute,DecisionTree>[] c){ super(c); }

	public static DecisionTree getInstance(DataSet ds){
		return new ID3().train(ds);
	}

	public static DecisionTree getInstance(File path){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new FileInputStream(path));
			NodeList root = doc.getElementsByTagName("ID3").item(0).getChildNodes();

			for(int i=0;i<root.getLength();i++){
				Node current = root.item(i);
				if(current.getNodeName().equals("node") || current.getNodeName().equals("leaf")){
					return new ID3().load(current);
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
		for(Pair<Attribute,DecisionTree> p : childs){
			if(r.attributes.contains(p.first))
				return p.second.predict(r);
		}
		
		return null; //no prediction
	}

	@Override
	protected DecisionTree train(DataSet r) {
		if(r.getRecordCount()==0) return null;

		//recorrer los registro y ver si todos los datos son iguales.
		Attribute aux = r.equals();
		if(aux != null) return new ID3(aux); //retornar el atributo de salida mas comun.

		//si todos tienen la misma salida retornar un nodo hoja con esa salida.
		if(r.sameOutput()) return new ID3(r.getRecord(0).attributes.get(r.getOutputIndex()));

		//caso general.
		int maxj = 0;
		double max = 0;
		Attribute t = null;

		for(int j=0,m=r.getRecord(0).attributes.size();j<m;j++){//para cada campo, si el atributo es categorico.
			//calcular el IG(Y|Xj) y guardar el j con el maximo IG(Y|Xj)
			if(j == r.getOutputIndex()) continue;
			double tempj = r.IG(r.getOutputIndex(), j);
			if(tempj > max){
				max = tempj;
				maxj = j;
			}
		}

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

		return new ID3(childs);
	}

	@Override
	protected DecisionTree load(Node root){
		if(root.getNodeName().equals("node")){
			Pair<Attribute,DecisionTree> childs[] = new Pair[Integer.parseInt(root.getAttributes().getNamedItem("splits").getTextContent())];
			NodeList aux = root.getChildNodes();
			int currentChild=0;
			for(int i=0,n=aux.getLength();i<n;i++){
				Node current = aux.item(i);
				if(current.getNodeName().equals("split")){
					Attribute att = Attribute.load(current);
					if(!att.isCategorical()) throw new IllegalArgumentException("Some of the node aren't categorical");
					for(;i<n;i++)
						if((current=aux.item(i)).getNodeName().equals("leaf") || current.getNodeName().equals("node")) break;
					childs[currentChild++] = new Pair<Attribute,DecisionTree>(att,load(current));
				}
			}
			return new ID3(childs);
		}else if(root.getNodeName().equals("leaf")){
			Attribute att = Attribute.load(root);
			if(!att.isCategorical()) throw new IllegalArgumentException("Some of the node aren't categorical");
			return new ID3(att);
		}
		return null;
	}
}

package net.sf.libai.trees;

import java.io.Serializable;
import org.w3c.dom.*;

/**
 *
 * @author kronenthaler
 */
public class Attribute<T extends Comparable> implements Comparable<Attribute>,Serializable{
	String name;
	T value;

	public Attribute(String _name, T _value){
		name = _name;
		value = _value;
	}

	public int compareTo(Attribute o) {
		if(!name.equals(o.name)) return name.compareTo(o.name);
		return value.compareTo(o.value);
	}

	@Override
	public boolean equals(Object o){
		return compareTo((Attribute)o) == 0;
	}

	public boolean isCategorical(){
		return value instanceof String;
	}

	public static Attribute load(Node root){
		Attribute att = null;
		String type = root.getAttributes().getNamedItem("type").getTextContent();
		if(type.equals("java.lang.String"))
			att = new Attribute(root.getAttributes().getNamedItem("name").getTextContent(), root.getTextContent());
		else if(type.equals("java.lang.Integer"))
			att = new Attribute(root.getAttributes().getNamedItem("name").getTextContent(), Integer.parseInt(root.getTextContent()));
		else if(type.equals("java.lang.Double"))
			att = new Attribute(root.getAttributes().getNamedItem("name").getTextContent(), Double.parseDouble(root.getTextContent()));
		return att;
	}

	public String toString(){
		return name+"->"+value;
	}
}

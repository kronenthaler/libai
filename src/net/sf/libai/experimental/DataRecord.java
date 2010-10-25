package net.sf.libai.experimental;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class DataRecord {
	private Vector<Attribute> attributes;
	
	public DataRecord(Attribute... atts){
		attributes = new Vector<Attribute>();
		for(Attribute a: atts)
			attributes.add(a);
	}

	public int getAttributeCount(){
		return attributes.size();
	}

	public Attribute getAttribute(int a){
		return attributes.get(a);
	}

	public boolean contains(Attribute a){
		for(Attribute att : attributes){
			if(att.getClass() == a.getClass()){
				if(att.getName().equals(a.getName())){
					if(att.compareTo(a) == 0)
						return true;
				}
			}
		}
		return false;
	}

	public String toString(){
		return attributes.toString();
	}
}

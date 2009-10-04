package net.sf.libai.trees;

import java.util.*;

/**
 *
 * @author kronenthaler
 */
public class RecordData {
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();

	public RecordData(Attribute... atts){
		for(Attribute a : atts)
			attributes.add(a);
	}

	public String toString(){
		return "{"+attributes+"}";
	}
}

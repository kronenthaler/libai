package libai.classifiers.bayes;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import libai.classifiers.Attribute;
import libai.common.Pair;
import libai.common.dataset.CountTree;
import libai.common.dataset.DataSet;

/**
 *
 * @author kronenthaler
 */
public class CPTable {
    private Map<Attribute,Integer> lookupEvent;
    private Map<List<Attribute>, Integer> lookupGiven;
    private double table[][]; //given, event
    private List<Integer> parents; //important to restore!
    
    private CPTable(){
        lookupEvent = new HashMap<Attribute, Integer>();
        lookupGiven = new HashMap<List<Attribute>, Integer>();
    }
    
    public CPTable(DataSet ds, int vertex){
        this();
        int N = ds.getItemsCount();
        Map<Attribute, Integer> freq = ds.getFrequencies(0, N, vertex);
        
        table = new double[1][freq.size()];
        for(Attribute key : freq.keySet()){
            addGiven(key, null, (freq.get(key)+1)/((double)N + freq.size()));
        }
    }
    
    public CPTable(DataSet ds, CountTree countTree, List<Integer> parents, int vertex){
        this();
        this.parents = parents;
        
        int N = ds.getItemsCount();
        Map<Attribute, Integer> events = ds.getFrequencies(0, N, vertex);
        int combinations = 1;
        for(Integer parent : parents)
            combinations *= ds.getFrequencies(0, N, parent).size();
        
        table = new double[combinations][events.size()];
        for(int i=0;i<combinations;i++)
            Arrays.fill(table[i], 1/(double)events.size());
        
        initialize(ds, countTree, parents, 0, new ArrayList<Pair<Integer, Attribute>>(), vertex);
    }
    
    private void initialize(DataSet ds, CountTree countTree, List<Integer> parents, int currentIndex, List<Pair<Integer, Attribute>> values, int vertex){
        Map<List<Attribute>, Double> buffer = new HashMap<List<Attribute>, Double>();
        int N = ds.getItemsCount();
        
        if(currentIndex >= parents.size()){
            Map<Attribute, Integer> freq = ds.getFrequencies(0, N, vertex);
            int acumCount = 0;
            for(Attribute value : freq.keySet()){
                values.add(new Pair<Integer, Attribute>(vertex, value));
                int frequency = countTree.getCount(values.toArray(new Pair[0]));
                acumCount += frequency;
                values.remove(values.size()-1);
                
                List<Attribute> key = new ArrayList<Attribute>();
                key.add(value);
                for(Pair<Integer, Attribute> p : values) key.add(p.second);
                
                buffer.put(key, (double)frequency);
            }

            for(List<Attribute> key: buffer.keySet()){
                Attribute event = key.get(0);
                List<Attribute> evidence = key.size()-1 == 0 ? null : key.subList(1, key.size());
                
                addGiven(event, evidence, (buffer.get(key) + 1) / (double)(acumCount + freq.size()));
            }
        }else{
            Map<Attribute, Integer> parentValues = ds.getFrequencies(0, N, parents.get(currentIndex));
            for(Attribute value : parentValues.keySet()){
                Pair<Integer, Attribute> v = new Pair<Integer, Attribute>(parents.get(currentIndex), value);
                List<Pair<Integer, Attribute>> newValues = new ArrayList<Pair<Integer, Attribute>>(values);
                newValues.add(v);
                initialize(ds, countTree, parents, currentIndex+1, newValues, vertex);
            }
        }
    }
    
    public void addGiven(Attribute event, List<Attribute> evidence, double p){
        if(lookupEvent.get(event)==null)
            lookupEvent.put(event, lookupEvent.size());
        
        if(lookupGiven.get(evidence)==null)
            lookupGiven.put(evidence, lookupGiven.size());
        
        table[lookupGiven.get(evidence)][lookupEvent.get(event)] = p;
    }
    
    public double P(Attribute event, List<Attribute> evidence){
        //take just the elements that are the parents of this node.
        List<Attribute> evidenceSubset = null;
        if(parents != null){
            evidenceSubset = new ArrayList<Attribute>();
            for(Integer parent : parents){
                evidenceSubset.add(evidence.get(parent));
            }
        }
        return P1(event, evidenceSubset);
    }
    
    private double P1(Attribute event, List<Attribute> evidence){
        return table[lookupGiven.get(evidence)][lookupEvent.get(event)];
    }
    
    public Set<Attribute> getEventValues(){
        return lookupEvent.keySet();
    }
    
    //load from XML node formatted as BIF. it might require some aditional info, about the variables and their values.
    
    public boolean save(File path){
        try{
            PrintStream out = new PrintStream(path);
            out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            out.println(toXMLBIF());
            out.close();
        }catch(IOException ex){
            return false;
        }
        
        return true;
    }
    
    public String toXMLBIF(){
        StringBuilder str = new StringBuilder();
        str.append("<DEFINITION>\n");
        //attribute name in the events.
        Attribute event = lookupEvent.keySet().iterator().next();
        str.append("<FOR>").append(event.getName()).append("<FOR>\n"); 
        
        List<Attribute> evidence = lookupGiven.keySet().iterator().next();
        if(evidence!=null){
            for(Attribute attr : evidence){
                str.append("<GIVEN>").append(attr.getName()).append("</GIVEN>\n"); //as many as the evidence list
            }
        }
        
        str.append("<TABLE>");
        Attribute[] keys = lookupEvent.keySet().toArray(new Attribute[0]);
        Arrays.sort(keys);
        
        List<Attribute>[] evidences = lookupGiven.keySet().toArray(new List[0]);
        Arrays.sort(evidences, new Comparator<List<Attribute>>(){
            @Override
            public int compare(List<Attribute> o1, List<Attribute> o2) {
                for(int i=0;i<o1.size();i++){
                    if(o1.get(i).compareTo(o2.get(i)) == 0) continue;
                    return o1.get(i).compareTo(o2.get(i));
                }
                return 0;
            }
        });
        
        for(List<Attribute> evi : evidences){
            for(Attribute evt : keys){
                str.append(P1(evt, evi)+" ");
            }
        }
        str.append("</TABLE>\n");
        
        str.append("</DEFINITION>\n");
        return str.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        int spacing = 0;
        int spacing2 = 0;
        for(Attribute key : lookupEvent.keySet()){
            spacing = Math.max(spacing, key.toString().length());
        }
        
        for(Attribute key : lookupEvent.keySet()){
            str.append(String.format("%"+spacing+"s ", key));
        }
        str.append("\n");
        
        for(List<Attribute> keyX : lookupGiven.keySet()){
            for(Attribute keyY : lookupEvent.keySet()){
                str.append(String.format("%"+spacing+"f ", table[lookupGiven.get(keyX)][lookupEvent.get(keyY)]));
            }
            str.append("|");
            if(keyX != null){
                for(Attribute a : keyX)
                    str.append(String.format("%s\t", a));
            }
            str.append("\n");
        }
        return str.toString();
    }
}

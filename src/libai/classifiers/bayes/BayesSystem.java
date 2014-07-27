package libai.classifiers.bayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libai.classifiers.Attribute;
import static libai.classifiers.bayes.BayesNetwork.EPSILON;
import libai.common.Graph;
import libai.common.dataset.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TODO:
 * - check the calculation of the P(x|E) is correct [need a text book to check]
 * - consider continuous attributes.
 * - 
 * @author kronenthaler
 */
public abstract class BayesSystem {
    protected int outputIndex; //can be removed when tge CPT is used, it can be part of the BIF 0.50 specification.
    protected int totalCount; //can be removed when the CPT is used.
    protected MetaData metadata; //can be removed when the CPT is used.
    
    protected CountTree countTree;
    protected CPTable weights[];
    
    protected abstract Graph getStructure(DataSet ds, double eps);
    
    // include a method to create a frequency tree and use it for every counting related activity.
    protected void initCountTree(DataSet ds){
        countTree = new CountTree(ds);
    }
    
    protected final CPTable learnWeights(int vertex, Graph g, DataSet ds){
        List<Integer> parents = new ArrayList<Integer>();
        for(int i=0;i<g.getVertexCount();i++){
            if(i == vertex) continue;
            if(g.isParent(i, vertex)){
                parents.add(i);
            }
        }
        Collections.sort(parents);
        
        if(parents.isEmpty())
            return new CPTable(ds, vertex);
            
        return new CPTable(ds, countTree, parents, vertex);
    }
    
    /** 
     * Train a BayesSystem system given a dataset. Depending of the implementation it
     * might train structure and weight alike.
     * @param ds DataSet to learn the BayesSystem system from.
     * @return An instance of the same type ready to evaluate vectors of evidence.
     */
    public BayesSystem train(DataSet ds) {
        initCountTree(ds);
        
        Graph structure = getStructure(ds, EPSILON);
        weights = new CPTable[structure.getVertexCount()];
        
        //for each vertex, calculate the weight given the structure.
        for(int i=0;i<weights.length;i++){
            weights[i] = learnWeights(i, structure, ds);
        }
        
        return this;
    }
    
    /** Calculates the condition probability of h given the vector of evidence x. */
    protected double P(Attribute c, List<Attribute> x){
        //iterate over all parents of outputIndex, all the way up, until there is no more parents.
        //acumulate the results multiplying them.
        double p = 1;
        for(int current=0; current < weights.length; current++){
            Attribute z = x.get(current);
            double d = weights[current].P(z, x);
            p *= d;
        }
        return p;
    }
    
    //this might be reimplemented here if both algorithms use the CPTable structure.
    public Attribute eval(int outputIndex, List<Attribute> x) {
        Attribute winner = null;
        double max = -Double.MAX_VALUE;
        for (Attribute c : weights[outputIndex].getEventValues()) {
            x.remove(outputIndex); //remove placeholder attribute
            x.add(outputIndex, c); //insert the current value in use
            double tmp = P(c, x);
            if (tmp > max) {
                max = tmp;
                winner = c;
            }
        }
        
        return winner;
    }
    
    //need some factory methods.
    
    //implement new version 0.50 that supports continuous variables.
    //based on: http://www.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/ XMLBIF
    public boolean save(File path) {
        try{
            PrintStream out = new PrintStream(path);
            out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            out.println("<BIF VERSION=\"0.3\">");
            out.println("<NETWORK>");
            out.println("<NAME><![CDATA["+path+"]]></NAME>");
            
            for(CPTable table : weights){
                Set<Attribute> v = table.getEventValues();
                Attribute[] values = v.toArray(new Attribute[0]);
                Arrays.sort(values);
                
                //all variables
                out.println("<VARIABLE TYPE=\"nature\">");
                out.println("<NAME>"+values[0].getName()+"</NAME>");
                for(Attribute outcome : values)
                    out.println("<OUTCOME>"+outcome.getValue()+"</OUTCOME>");
                out.println("</VARIABLE>");
                
                //all tables
                out.println(table.toXMLBIF());
            }
            
            out.println("</NETWORK>");
            out.println("</BIF>");
            out.close();
        }catch(FileNotFoundException ex){
            return false;
        }
        
        return true;
    }

    public BayesSystem load(Node root) {
        //read first the variables.
        //read the definitions, pass the info of the variables to the CPTable load ?
        Map<String, List<String>> variables = new HashMap<String, List<String>>();
        //same order of the variables!
        NodeList children = root.getChildNodes();
        for(int i=0,n=children.getLength();i<n;i++){
            Node current = children.item(i);
            if(current.getNodeName().equals("VARIABLE")){
                NodeList var = current.getChildNodes();
                String name="";
                List<String> values = new ArrayList<String>();
                for(int j=0,m=var.getLength();j<m;j++){
                    if(var.item(j).getNodeName().equals("NAME"))
                        name = var.item(j).getTextContent();
                    else if(var.item(j).getNodeName().equals("OUTCOME"))
                        values.add(var.item(j).getTextContent());
                }
                variables.put(name, values);
            }
        }
        
        weights = new CPTable[variables.size()];
        for(int i=0,k=0,n=root.getChildNodes().getLength();i<n;i++){
            //look up for definitions
            //table has to generate permutations of the parents in the same order they were written.
            Node current = children.item(i);
            if(current.getNodeName().equals("DEFINITION")){
                weights[k++] = new CPTable(current, variables);
            }
        }
        
        return this;
    }
}

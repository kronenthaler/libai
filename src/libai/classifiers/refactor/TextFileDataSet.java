package libai.classifiers.refactor;

import java.util.Iterator;
import java.util.Set;
import libai.classifiers.Attribute;
import libai.classifiers.DataRecord;
import java.io.*;
import java.util.*;
import libai.classifiers.ContinuousAttribute;
import libai.classifiers.DiscreteAttribute;

/**
 *
 * @author kronenthaler
 */
public class TextFileDataSet implements DataSet{
    private ArrayList<DataRecord> data = new ArrayList<DataRecord>();
    private Set<Attribute> classes = new HashSet<Attribute>();
    private int outputIndex;
    
    private MetaData metadata = new MetaData(){
        @Override
        public boolean isCategorical(int fieldIndex) {
            return data.get(0).get(fieldIndex).isCategorical();
        }

        @Override
        public int getAttributeCount() {
            return data.get(0).getAttributeCount();
        }
    };
    
    public TextFileDataSet(File dataSource, int output){
        outputIndex = output;
        try{
            BufferedReader in = new BufferedReader(new FileReader(dataSource));
            while (true){
                String line = in.readLine();
                if(line == null) break;
                String[] tokens = line.split(",");
                Attribute[] attributes = new Attribute[tokens.length];
                for(int i=0; i<tokens.length; i++){
                    String token = tokens[i];
                    Attribute attr = null;
                    try{
                        attr = new ContinuousAttribute(Double.parseDouble(token));
                    }catch(NumberFormatException e){
                        attr = new DiscreteAttribute(token);
                    }
                
                    attributes[i] = attr;
                    if(i==outputIndex)
                        classes.add(attr);
                }
                data.add(new DataRecord(attributes));
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public int getOutputIndex() {
        return outputIndex;
    }
    
    @Override
    public Set<Attribute> getClasses() {
        return classes;
    }

    @Override
    public int getItemsCount() {
        return data.size();
    }

    @Override
    public MetaData getMetaData() {
        return metadata;
    }

    @Override
    public DataSet[] splitKeepingRelation(double proportion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<DataRecord> iterator() {
        return data.iterator();
    }

}

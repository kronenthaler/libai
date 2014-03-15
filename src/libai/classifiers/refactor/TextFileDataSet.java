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
    private List<List<Attribute>> data = new ArrayList<List<Attribute>>();
    private Set<Attribute> classes = new HashSet<Attribute>();
    private int outputIndex;
    
    private MetaData metadata = new MetaData(){
        @Override
        public boolean isCategorical(int fieldIndex) {
            return data.get(0).get(fieldIndex).isCategorical();
        }

        @Override
        public int getAttributeCount() {
            return data.get(0).size();
        }

        @Override
        public Set<Attribute> getClasses() {
            return classes;
        }

        @Override
        public String getAttributeName(int fieldIndex) {
            return "["+fieldIndex+"]";
        }
    };
    
    TextFileDataSet(int output){
        outputIndex = output;
    }
    
    private TextFileDataSet(TextFileDataSet parent, int lo, int hi){
        this(parent.outputIndex);
        addRecords(parent.data.subList(lo, hi));
    }
    
    public TextFileDataSet(File dataSource, int output){
        this(output);
        try{
            BufferedReader in = new BufferedReader(new FileReader(dataSource));
            while (true){
                String line = in.readLine();
                if(line == null) break;
                String[] tokens = line.split(",");
                ArrayList<Attribute> record = new ArrayList<Attribute>();
                for(int i=0; i<tokens.length; i++){
                    String token = tokens[i];
                    Attribute attr = null;
                    try{
                        attr = new ContinuousAttribute(Double.parseDouble(token));
                    }catch(NumberFormatException e){
                        attr = new DiscreteAttribute(token);
                    }
                
                    record.add(attr);
                    if(i==outputIndex)
                        classes.add(attr);
                }
                data.add(record);
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public DataSet getSubset(int lo, int hi){
        return new TextFileDataSet(this, lo, hi);
    }
    
    @Override
    public int getOutputIndex() {
        return outputIndex;
    }
    
    @Override
    public int getItemsCount() {
        return data.size();
    }

    @Override
    public MetaData getMetaData() {
        return metadata;
    }

    public Iterable<List<Attribute>> sortOver(final int fieldIndex){
        ArrayList<List<Attribute>> copy = new ArrayList<List<Attribute>>(data);
        Collections.sort(copy, new Comparator<List<Attribute>>(){
           @Override
			public int compare(List<Attribute> o1, List<Attribute> o2) {
				int ret = o1.get(fieldIndex).compareTo(o2.get(fieldIndex));
				if (ret == 0)
					return o1.get(outputIndex).compareTo(o2.get(outputIndex));
				return ret;
			}
        });
        return copy;
    }
    
    @Override
    public DataSet[] splitKeepingRelation(double proportion) {
        TextFileDataSet a = new TextFileDataSet(outputIndex);
		TextFileDataSet b = new TextFileDataSet(outputIndex);
        
        Iterable<List<Attribute>> sortedData = sortOver(outputIndex);
        Attribute prev = null;
        List<List<Attribute>> buffer = new ArrayList<List<Attribute>>();
        for (List<Attribute> record : sortedData){
            if((prev != null && prev.compareTo(record.get(outputIndex)) != 0)){
                Collections.shuffle(buffer);
                a.addRecords(buffer.subList(0, (int)(buffer.size() * proportion)));
                b.addRecords(buffer.subList((int)(buffer.size() * proportion), buffer.size()));
                buffer.clear();
            }
            
            buffer.add(record);
            prev = record.get(outputIndex);
        }
        
        if(!buffer.isEmpty()){
            Collections.shuffle(buffer);
            a.addRecords(buffer.subList(0, (int)(buffer.size() * proportion)));
            b.addRecords(buffer.subList((int)(buffer.size() * proportion), buffer.size()));
        }
        
		return new DataSet[]{a, b};
    }
    
    public final void addRecords(Collection<? extends List<Attribute>> list){
        data.addAll(list);
        for(List<Attribute> record : list){
            classes.add(record.get(outputIndex));
        }
    }
    
    @Override
    public String toString(){
        return data.toString();
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        return data.iterator();
    }
    
    @Override
    public boolean allTheSameOutput() {
        return metadata.getClasses().size() != 1;
    }

    @Override
    public Attribute allTheSame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GainInformation gain(int lo, int hi, int fieldIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

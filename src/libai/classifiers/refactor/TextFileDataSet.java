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
                    Attribute attr = Attribute.getInstance(token);
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
        return sortOver(0, getItemsCount(), fieldIndex);
    }
    
    public Iterable<List<Attribute>> sortOver(final int lo, final int hi, final int fieldIndex){
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
        return copy.subList(lo, hi);
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
        HashMap<String, Integer> freq = new HashMap<String, Integer>();

		for (int i = 0; i < data.size(); i++) {
			for (int j = 0, n = metadata.getAttributeCount(); j < n; j++) {
				if (j != outputIndex && 
					!data.get(i).get(j).equals(data.get(0).get(j)))
					return null;
			}

			if (!(data.get(i).get(outputIndex) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute) data.get(i).get(outputIndex)).getValue();
			if (freq.get(v) == null)
				freq.put(v, 0);
			freq.put(v, freq.get(v) + 1);
		}

		int max = Integer.MIN_VALUE;
		String mostCommon = null;
		for (String e : freq.keySet()) {
			if (freq.get(e) > max) {
				max = freq.get(e);
				mostCommon = e;
			}
		}
        
        return Attribute.getInstance(mostCommon);
    }
    
    @Override
    public HashMap<Attribute, Integer> getFrequencies(int lo, int hi, int fieldIndex) {
        if(!metadata.isCategorical(fieldIndex))
            throw new IllegalArgumentException("The attribute must be discrete");
        
        HashMap<Attribute, Integer> freq = new HashMap<Attribute, Integer>();
        for(int i=lo;i<hi;i++){
            List<Attribute> record = data.get(i);
            Attribute v = record.get(fieldIndex);
			if (freq.get(v) == null)
				freq.put(v, 0);
			freq.put(v, freq.get(v) + 1);
        }
        
        return freq;
    }
	
	@Override
	public HashMap<Double, HashMap<Attribute, Integer>> getAccumulatedFrequencies(final int lo, final int hi, final int fieldIndex){
		Iterable<List<Attribute>> records = sortOver(lo, hi, fieldIndex);
		List<Attribute> prev = null;
		HashMap<Double, HashMap<Attribute, Integer>> freqAcum = new HashMap<Double, HashMap<Attribute, Integer>>();
		
		for(List<Attribute> record : records){
			double va = ((ContinuousAttribute) record.get(fieldIndex)).getValue();
			Attribute v = record.get(outputIndex);
			
			if(freqAcum.get(va) == null){
				freqAcum.put(va, new HashMap<Attribute, Integer>());
				for(Attribute c : metadata.getClasses()){
					if(prev == null)
						freqAcum.get(va).put(c, 0);
					else{
						double pva = ((ContinuousAttribute) prev.get(fieldIndex)).getValue();
						freqAcum.get(va).put(c, freqAcum.get(pva).get(c));
					}
				}
			}
			
			prev = record;
			HashMap<Attribute, Integer> m = freqAcum.get(va);
			m.put(v, m.get(v) + 1);
		}
		
		return freqAcum;
	}
}

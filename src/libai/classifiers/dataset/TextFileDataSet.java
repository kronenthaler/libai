package libai.classifiers.dataset;

import java.util.Iterator;
import java.util.Set;
import libai.classifiers.*;
import java.io.*;
import java.util.*;
import libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class TextFileDataSet implements DataSet {
    private List<List<Attribute>> data = new ArrayList<List<Attribute>>();
    private Set<Attribute> classes = new HashSet<Attribute>();
    private int outputIndex;
    private int orderBy;
    private HashMap<Triplet<Integer, Integer, Integer>, HashMap<Attribute, Integer>> cache;

    private MetaData metadata = new MetaData() {
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
            return "[" + fieldIndex + "]";
        }
    };

    TextFileDataSet(int output) {
        outputIndex = output;
        orderBy = outputIndex;
        cache = new HashMap<Triplet<Integer, Integer, Integer>, HashMap<Attribute, Integer>>();
    }

    private TextFileDataSet(TextFileDataSet parent, int lo, int hi) {
        this(parent.outputIndex);
        addRecords(parent.data.subList(lo, hi));
    }

    public TextFileDataSet(File dataSource, int output) {
        this(output);
        try {
            BufferedReader in = new BufferedReader(new FileReader(dataSource));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                String[] tokens = line.split(",");
                ArrayList<Attribute> record = new ArrayList<Attribute>();
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    Attribute attr = Attribute.getInstance(token, metadata.getAttributeName(i));
                    record.add(attr);
                    if (i == outputIndex)
                        classes.add(attr);
                }
                data.add(record);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataSet getSubset(int lo, int hi) {
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

    @Override
    public Iterable<List<Attribute>> sortOver(final int fieldIndex) {
        return sortOver(0, getItemsCount(), fieldIndex);
    }

    @Override
    public Iterable<List<Attribute>> sortOver(final int lo, final int hi, final int fieldIndex) {
        orderBy = fieldIndex;
        Collections.sort(data, new Comparator<List<Attribute>>() {
            @Override
            public int compare(List<Attribute> o1, List<Attribute> o2) {
                int ret = o1.get(fieldIndex).compareTo(o2.get(fieldIndex));
                if (ret == 0)
                    return o1.get(outputIndex).compareTo(o2.get(outputIndex));
                return ret;
            }
        });
        return data.subList(lo, hi);
    }

    @Override
    public DataSet[] splitKeepingRelation(double proportion) {
        TextFileDataSet a = new TextFileDataSet(outputIndex);
        TextFileDataSet b = new TextFileDataSet(outputIndex);

        Iterable<List<Attribute>> sortedData = sortOver(outputIndex);
        Attribute prev = null;
        List<List<Attribute>> buffer = new ArrayList<List<Attribute>>();
        for (List<Attribute> record : sortedData) {
            if ((prev != null && prev.compareTo(record.get(outputIndex)) != 0)) {
                Collections.shuffle(buffer);
                a.addRecords(buffer.subList(0, (int) (buffer.size() * proportion)));
                b.addRecords(buffer.subList((int) (buffer.size() * proportion), buffer.size()));
                buffer.clear();
            }

            buffer.add(record);
            prev = record.get(outputIndex);
        }

        if (!buffer.isEmpty()) {
            Collections.shuffle(buffer);
            a.addRecords(buffer.subList(0, (int) (buffer.size() * proportion)));
            b.addRecords(buffer.subList((int) (buffer.size() * proportion), buffer.size()));
        }

        return new DataSet[]{a, b};
    }

    public final void addRecords(Collection<? extends List<Attribute>> list) {
        data.addAll(list);
        for (List<Attribute> record : list) {
            classes.add(record.get(outputIndex));
        }
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        for (List<Attribute> r : sortOver(orderBy))
            str.append(r.toString()).append('\n');
        return str.toString();
    }

    @Override
    public Iterator<List<Attribute>> iterator() {
        return data.iterator();
    }

    @Override
    public boolean allTheSameOutput() {
        return metadata.getClasses().size() == 1;
    }

    @Override
    public Attribute allTheSame() {
        HashMap<String, Integer> freq = new HashMap<String, Integer>();

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0, n = metadata.getAttributeCount(); j < n; j++) {
                if (j != outputIndex
                        && !data.get(i).get(j).equals(data.get(0).get(j)))
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

        return Attribute.getInstance(mostCommon, metadata.getAttributeName(outputIndex));
    }

    @Override
    public HashMap<Attribute, Integer> getFrequencies(int lo, int hi, int fieldIndex) {
        Triplet<Integer, Integer, Integer> key = new Triplet<Integer, Integer, Integer>(lo, hi, fieldIndex);

        if (cache.get(key) != null)
            return cache.get(key);

        //if (!metadata.isCategorical(fieldIndex))
        //    throw new IllegalArgumentException("The attribute must be discrete");

        HashMap<Attribute, Integer> freq = new HashMap<Attribute, Integer>();
        for (int i = lo; i < hi; i++) {
            List<Attribute> record = data.get(i);
            Attribute v = record.get(fieldIndex);
            if (freq.get(v) == null)
                freq.put(v, 0);
            freq.put(v, freq.get(v) + 1);
        }

        cache.put(key, freq);

        return freq;
    }

    @Override
    public void close() {
        data.clear();
        data = null;
    }

    @Override
    public int getFrecuencyOf(Pair<Integer, Attribute>... values) {
        int count = 0;
        for(List<Attribute> record : sortOver(outputIndex)){
            boolean flag = true;
            for(Pair<Integer, Attribute> var : values){
                if(!record.get(var.first).equals(var.second)){
                    flag = false;
                    break;
                }
            }
            if(flag) 
                count++;
        }
        return count;
    }
}

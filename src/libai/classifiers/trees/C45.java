package libai.classifiers.trees;

import libai.classifiers.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import libai.classifiers.dataset.DataSet;
import libai.classifiers.dataset.MetaData;
import libai.common.Pair;

/**
 * TODO: missing values.
 *
 * @author kronenthaler
 */
public class C45 implements Comparable<C45> {
    public static final int NO_PRUNE = 0;
    public static final int QUINLANS_PRUNE = 1;
    public static final int LAPLACE_PRUNE = 2;
    protected Attribute output;
    protected Pair<Attribute, C45> childs[];
    protected double error;
    protected double backedUpError;
    //prune variables
    protected Attribute mostCommonLeaf;
    //Laplace's error pruning
    protected int mostCommonLeafFreq = Integer.MIN_VALUE;
    protected int samplesCount; //how many samples pass for this node in the pruning process.
    protected HashMap<Attribute, Integer> samplesFreq = new HashMap<Attribute, Integer>(); //used to the pruning process.
    //Quinlan's prunning using confidence
    protected double confidence = 0.25;
    protected double z;
    protected int good, bad;

    public static class EntropyInformation {
    }

    public static class DiscreteEntropyInformation extends EntropyInformation {
        public double maxInfo;
        public double maxSplitInfo;
    }

    public static class ContinuousEntropyInformation extends DiscreteEntropyInformation {
        public double splitValue;
        public int indexOfSplitValue;
    }

    public static class GainInformation extends ContinuousEntropyInformation {
        public double gain;
        public double ratio;
    }

    //constructors
    public C45() {
        setConfidence(confidence);
    }

    protected C45(Attribute root) {
        this();
        output = root;
    }

    protected C45(Pair<Attribute, C45>[] c) {
        this();
        childs = c;
    }

    protected C45(ArrayList<Pair<Attribute, C45>> c) {
        this();
        childs = new Pair[c.size()];
        for (int i = 0, n = childs.length; i < n; i++)
            childs[i] = c.get(i);
    }

    //Factories
    public static C45 getInstance(File path) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(path));
            NodeList root = doc.getElementsByTagName("C45").item(0).getChildNodes();

            for (int i = 0; i < root.getLength(); i++) {
                Node current = root.item(i);
                if (current.getNodeName().equals("node") || current.getNodeName().equals("leaf")) {
                    return new C45().load(current);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return an unpruned tree from the given dataset.
     */
    public static C45 getInstance(DataSet ds) {
        return new C45().train(ds);
    }

    /**
     * Return a pruned tree from the given dataset using the standard confidence
     * of 25%
     */
    public static C45 getInstancePrune(DataSet ds, int type) {
        return new C45().train(ds).prune(ds, type);
    }

    /**
     * Return a pruned tree from the given dataset using the specified
     * confidence.
     */
    public static C45 getInstancePrune(DataSet ds, double confidence) {
        C45 ret = new C45();
        ret = ret.train(ds);
        ret.setConfidence(confidence);
        return ret.prune(ds, QUINLANS_PRUNE);
    }

    //Tree related
    public boolean isLeaf() {
        return (childs == null || childs.length == 0) && output != null;
    }

    public Attribute eval(List<Attribute> record, DataSet ds) {
        return eval(record, false, null, ds);
    }

    private Attribute eval(List<Attribute> record, boolean keeptrack, Attribute expected, DataSet ds) {
        if (keeptrack) {
            //laplace pruning
            if (samplesFreq.get(expected) == null) {
                for (Attribute att : ds.getMetaData().getClasses())
                    samplesFreq.put(att, 0);
            }
            samplesFreq.put(expected, samplesFreq.get(expected) + 1);

            if (mostCommonLeafFreq < samplesFreq.get(expected)) {
                mostCommonLeafFreq = samplesFreq.get(expected);
                mostCommonLeaf = expected;
            }
            samplesCount++;
        }

        if (isLeaf()) {
            if (keeptrack) {
                //quinlan pruning
                if (output.compareTo(expected) == 0)
                    good++;
                else
                    bad++;
            }
            return output;
        } else {
            if (childs[0].first.isCategorical()) {
                for (Pair<Attribute, C45> p : childs) {
                    if (record.contains(p.first))
                        return p.second.eval(record, keeptrack, expected, ds);
                }
            } else {
                try {
                    for (int i = 0; i < ds.getMetaData().getAttributeCount(); i++) {
                        if (ds.getMetaData().getAttributeName(i).equals(childs[0].first.getName())) {
                            if (record.get(i).compareTo(childs[0].first) <= 0)
                                return childs[0].second.eval(record, keeptrack, expected, ds);
                            else
                                return childs[1].second.eval(record, keeptrack, expected, ds);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null; //no prediction
    }

    public C45 train(DataSet ds) {
        HashSet<Integer> visited = new HashSet<Integer>();
        visited.add(ds.getOutputIndex());
        return train(ds, visited, "");
    }

    private C45 train(DataSet ds, HashSet<Integer> visited, String deep) {
        MetaData metadata = ds.getMetaData();
        int attributeCount = metadata.getAttributeCount();
        int outputIndex = ds.getOutputIndex();
        int itemsCount = ds.getItemsCount();

        //no more attributes to visit or no more items to check
        if (itemsCount == 0 || visited.size() == attributeCount)
            return null;

        //base case: all the output are the same.
        if (ds.allTheSameOutput())
            return new C45(ds.iterator().next().get(outputIndex));

        //base case: all the attributes are the same.
        Attribute att = ds.allTheSame();
        if (att != null)
            return new C45(att);

        //else
        double max = -Double.MIN_VALUE;
        int index = -1;
        int indexOfValue = -1;
        double splitValue = Double.MIN_VALUE;
        for (int i = 0; i < attributeCount; i++) {
            if (!visited.contains(i)) {
                GainInformation gain = gain(ds, 0, itemsCount, i); //get the maximun gain ratio.
                if (gain.ratio > max) {
                    max = gain.ratio;
                    index = i; //split attribute
                    if (!metadata.isCategorical(i)) {
                        splitValue = gain.splitValue;
                        indexOfValue = gain.indexOfSplitValue;
                    }
                }
            }
        }

        Iterable<List<Attribute>> sortedRecords = ds.sortOver(index);
        ArrayList<Pair<Attribute, C45>> children = new ArrayList<Pair<Attribute, C45>>();
        if (metadata.isCategorical(index)) {
            visited.add(index); //mark as ready, avoid revisiting a nominal attribute.

            Iterator<List<Attribute>> records = sortedRecords.iterator();
            records.hasNext(); //just to move the pointer.

            Attribute prev = records.next().get(index), current = null;
            int nlo, i = 0;
            while (prev != null) {
                current = null;
                nlo = i;

                while (records.hasNext()) {
                    current = records.next().get(index);
                    if (!prev.equals(current)) {
                        break;
                    }
                    i++;
                }
                i++;
                
                DataSet section = ds.getSubset(nlo, i);
                children.add(new Pair<Attribute, C45>(prev,
                        train(section, visited, deep + "\t")));
                section.close();
                
                prev = current;
            }

        } else {
            DataSet l = ds.getSubset(0, indexOfValue);
            DataSet r = ds.getSubset(indexOfValue, itemsCount);
            String fieldName = ds.getMetaData().getAttributeName(index);

            C45 left = train(l, visited, deep + "\t");
            children.add(new Pair<Attribute, C45>(Attribute.getInstance(splitValue, fieldName), left));

            C45 right = train(r, visited, deep + "\t");
            children.add(new Pair<Attribute, C45>(Attribute.getInstance(splitValue, fieldName), right));
            
            l.close();
            r.close();
        }

        return new C45(children);
    }

    private GainInformation gain(DataSet ds, int lo, int hi, int fieldIndex) {
        DiscreteEntropyInformation info = (DiscreteEntropyInformation) infoAvg(ds, lo, hi, fieldIndex);
        double gain = info(ds, 0, ds.getItemsCount(), ds.getOutputIndex()) - info.maxInfo;
        double gainRatio = gain / info.maxSplitInfo;

        GainInformation result = new GainInformation();
        result.gain = gain;
        result.ratio = gainRatio;
        result.maxInfo = info.maxInfo;
        result.maxSplitInfo = info.maxSplitInfo;

        if (info instanceof ContinuousEntropyInformation) {
            result.splitValue = ((ContinuousEntropyInformation) info).splitValue;
            result.indexOfSplitValue = ((ContinuousEntropyInformation) info).indexOfSplitValue;
        }

        return result;
    }

    private EntropyInformation infoAvg(DataSet ds, int lo, int hi, int fieldIndex) {
        if (ds.getMetaData().isCategorical(fieldIndex))
            return infoAvgDiscrete(ds, lo, hi, fieldIndex);
        else
            return infoAvgContinuous(ds, lo, hi, fieldIndex);
    }

    private DiscreteEntropyInformation infoAvgDiscrete(DataSet ds, int lo, int hi, int fieldIndex) {
        DiscreteEntropyInformation info = new DiscreteEntropyInformation();
        double total = hi - lo;
        Attribute prev = null, current = null;
        Iterable<List<Attribute>> sortedRecords = ds.sortOver(lo, hi, fieldIndex);
        Iterator<List<Attribute>> records = sortedRecords.iterator();

        prev = records.next().get(fieldIndex);

        int nlo, i = lo;
        while (prev != null) {
            current = null;
            nlo = i;

            while (records.hasNext()) {
                current = records.next().get(fieldIndex);
                if (!prev.equals(current)) {
                    break;
                }
                i++;
            }
            i++;

            prev = current;

            double res = info(ds, nlo, i, ds.getOutputIndex());
            double chunkSize = i - nlo;
            info.maxInfo += res * (chunkSize / total);
            info.maxSplitInfo += -(chunkSize / total) * (Math.log10(chunkSize / total) / Math.log10(2));
        }

        return info;
    }

    private ContinuousEntropyInformation infoAvgContinuous(DataSet ds, int lo, int hi, int fieldIndex) {
        HashMap<Attribute, Integer> totalFreq = ds.getFrequencies(lo, hi, ds.getOutputIndex());
        HashMap<Double, HashMap<Attribute, Integer>> freqAcum = ds.getAccumulatedFrequencies(lo, hi, fieldIndex);

        double splitInfo = 0;
        double total = hi - lo;

        double maxInfo = -Double.MIN_VALUE;
        double maxSplitInfo = -Double.MIN_VALUE;
        double bestSplitValue = Integer.MAX_VALUE;
        int bestIndex = Integer.MIN_VALUE;

        for (Attribute key : ds.getMetaData().getClasses()) {
            if (totalFreq.get(key) == null)
                totalFreq.put(key, 0);
        }

        Iterable<List<Attribute>> records = ds.sortOver(lo, hi, fieldIndex);
        int i = lo;
        for (List<Attribute> record : records) {
            double value = ((ContinuousAttribute) record.get(fieldIndex)).getValue();
            HashMap<Attribute, Integer> freq = freqAcum.get(value);

            double total2 = i - lo + 1;
            double acum2 = 0;

            double total3 = total - total2;
            double acum3 = 0;

            for (Attribute e : freq.keySet()) {
                int f = freq.get(e);
                if (f > 0) {
                    double p = (f / total2);
                    acum2 += -p * (Math.log10(p) / Math.log10(2));
                }

                f = totalFreq.get(e) - freq.get(e);
                if (f > 0) {
                    double p = f / total3;
                    acum3 += -p * (Math.log10(p) / Math.log10(2));
                }
            }

            double infoA = (total2 / total) * acum2;
            double infoB = (total3 / total) * acum3;

            splitInfo = 0;
            if ((int) total2 != 0)
                splitInfo += -(total2 / total) * (Math.log10(total2 / total) / Math.log10(2));

            if ((int) total3 != 0)
                splitInfo += -(total3 / total) * (Math.log10(total3 / total) / Math.log10(2));

            if (splitInfo > maxSplitInfo) {
                maxInfo = infoA + infoB;
                int k = i + 1;
                Iterable<List<Attribute>> subSet = ds.sortOver(i + 1, hi, fieldIndex);
                for (List<Attribute> subSetRecord : subSet) {
                    double nextValue = ((ContinuousAttribute) subSetRecord.get(fieldIndex)).getValue();
                    if (value != nextValue) {
                        bestSplitValue = (value + nextValue) / 2;
                        bestIndex = k;
                        break;
                    }
                    k++;
                }

                if (k == hi) {
                    bestSplitValue = value;
                    bestIndex = hi - 1;
                }

                maxSplitInfo = splitInfo;
            }
            i++;
        }

        ContinuousEntropyInformation result = new ContinuousEntropyInformation();
        result.maxInfo = maxInfo;
        result.maxSplitInfo = maxSplitInfo;
        result.splitValue = bestSplitValue;
        result.indexOfSplitValue = bestIndex;

        return result;
    }

    private double info(DataSet ds, int lo, int hi, int fieldIndex) {
        HashMap<Attribute, Integer> freq = ds.getFrequencies(lo, hi, fieldIndex);

        double total = hi - lo;
        double acum = 0;
        for (Attribute e : freq.keySet()) {
            int f = freq.get(e);
            if (f != 0) {
                double p = (f / total);
                acum += -p * (Math.log10(p) / Math.log10(2));
            }
        }

        return acum;
    }

    public double error(DataSet ds) {
        int errorCount = 0;
        for (List<Attribute> record : ds) {
            if ((eval(record, ds).compareTo(record.get(ds.getOutputIndex())) != 0)) {
                errorCount++;
            }
        }
        return errorCount / (double) ds.getItemsCount();
    }

    public C45 prune(DataSet ds, int type) {
        //first of all, evaluate all the data set over the tree, and keep track of the results.
        int outputIndex = ds.getOutputIndex();
        for (List<Attribute> record : ds)
            eval(record, true, record.get(outputIndex), ds);

        prune(type);

        return this;
    }

    private void prune(int prunningType) {
        if (isLeaf()) {
            if (prunningType == QUINLANS_PRUNE)
                error = confidenceError(1.0 / (double) (bad + good), good / (double) (bad + good));
            else if (prunningType == LAPLACE_PRUNE)
                error = laplaceError(samplesCount, mostCommonLeafFreq, samplesFreq.size());
        } else {
            backedUpError = 0;
            for (Pair<Attribute, C45> c : childs) {
                c.second.prune(prunningType);
                if (prunningType == QUINLANS_PRUNE) {
                    good += c.second.good;
                    bad += c.second.bad;
                    backedUpError += c.second.error * (c.second.good + c.second.bad);
                } else if (prunningType == LAPLACE_PRUNE) {
                    backedUpError += c.second.error * c.second.samplesCount;
                }
            }

            if (prunningType == QUINLANS_PRUNE) {
                error = confidenceError(1.0 / (double) (bad + good), good / (double) (bad + good));
                backedUpError /= (double) (good + bad);
            } else if (prunningType == LAPLACE_PRUNE) {
                error = laplaceError(samplesCount, mostCommonLeafFreq, samplesFreq.size());
                backedUpError /= (double) samplesCount;
            }

            if (error < backedUpError) {
                childs = null;
                output = mostCommonLeaf;
            }

            error = Math.min(error, backedUpError);
        }
    }

    private double laplaceError(int N, int n, int k) {
        return (double) (N - n + k - 1) / (double) (N + k);
    }

    // QUINLAN'S prunning functions
    public void setConfidence(double c) {
        confidence = c;

        double a = 0;
        double b = 99;
        double upperLimit = doLeft(b);

        for (int index = 0; a <= 3; a += 0.01, index++) {
            double sum = upperLimit - doLeft(a);
            sum = 1.0 - sum;
            if (sum >= c) {
                z = a;
                break;
            }
        }
        setZ(z);
    }

    private double doLeft(double z) {
        if (z < -6.5)
            return 0;
        if (z > 6.5)
            return 1;

        long factK = 1;
        double sum = 0;
        double term = 1;
        int k = 0;
        while (Math.abs(term) > Math.exp(-23)) {
            term = 0.3989422804 * Math.pow(-1, k) * Math.pow(z, k) / (2 * k + 1) / Math.pow(2, k) * Math.pow(z, k + 1) / factK;
            sum += term;
            k++;
            factK *= k;

        }
        sum += 1 / 2;
        if (sum < 1e-9)
            sum = 0;

        return sum;
    }

    private void setZ(double z) {
        this.z = z;
        if (!isLeaf()) {
            if (childs != null)
                for (Pair<Attribute, C45> c : childs) {
                    c.second.z = z;
                    c.second.setZ(z);
                }
        }
    }

    private double confidenceError(double invN, double f) {
        double z2 = z * z;
        double e = (f + (z2 * invN * 0.5) + z * Math.sqrt((f * invN) - (f * f * invN) + (z2 * invN * invN * 0.25))) / (1 + (z2 * invN));
        return e;
    }
	//end quinlan's

    //IO functions
    /**
     * Load a new C45 tree from the XML node root.
     */
    protected C45 load(Node root) {
        if (root.getNodeName().equals("node")) {
            Pair<Attribute, C45> childs[] = new Pair[Integer.parseInt(root.getAttributes().getNamedItem("splits").getTextContent())];
            NodeList aux = root.getChildNodes();
            int currentChild = 0;
            for (int i = 0, n = aux.getLength(); i < n; i++) {
                Node current = aux.item(i);
                if (current.getNodeName().equals("split")) {
                    Attribute att = Attribute.load(current);
                    for (; i < n; i++)
                        if ((current = aux.item(i)).getNodeName().equals("leaf")
                                || current.getNodeName().equals("node"))
                            break;
                    childs[currentChild++] = new Pair<Attribute, C45>(att, load(current));
                }
            }
            return new C45(childs);
        } else if (root.getNodeName().equals("leaf")) {
            return new C45(Attribute.load(root));
        }

        return null;
    }

    public boolean save(File path) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(path));
            out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            out.println("<" + getClass().getSimpleName() + ">");
            save(out, "\t");
            out.println("</" + getClass().getSimpleName() + ">");
            out.close();
            //safe format into a XML file.
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void save(PrintStream out, String indent) throws IOException {
        if (isLeaf()) {
            out.println(indent + "<leaf type=\"" + output.getClass().getName() + "\" name=\"" + output.getName() + "\"><![CDATA[" + output.getValue() + "]]></leaf>");
        } else {
            out.println(indent + "<node splits=\"" + childs.length + "\">");
            for (Pair<Attribute, C45> p : childs) {
                out.println(indent + "\t<split type=\"" + p.first.getClass().getName() + "\" name=\"" + p.first.getName() + "\"><![CDATA[" + p.first.getValue() + "]]></split>");
                p.second.save(out, indent + "\t");
            }
            out.println(indent + "</node>");
        }
    }

    /**
     * Print the tree over the standard output. Alias for <code>print("")</code>
     */
    public void print() {
        print("");
    }

    /**
     * Print the tree over the standard output using an initial indent string.
     * With each new level, an \t is appended to the indent string.
     *
     * @params indent Initial string for indentation.
     */
    private void print(String indent) {
        if (isLeaf()) {
            System.out.println(indent + "[" + output + " " + samplesFreq + " e: " + error + "]");
        } else {
            for (Pair<Attribute, C45> p : childs) {
                if (p.first.isCategorical())
                    System.out.println(indent + "[" + p.first.getName() + " = " + ((DiscreteAttribute) p.first).getValue() + " " + samplesFreq + " e: " + error + " be: " + backedUpError + "]");
                else
                    System.out.println(indent + "[" + p.first.getName() + (childs[0] == p ? " <= " : " > ") + ((ContinuousAttribute) p.first).getValue() + " " + samplesFreq + " be: " + backedUpError + "]");
                p.second.print(indent + "\t");
            }
        }
    }
    //end IO functions

    /**
     * Dummy function, just needed to be able to use the Pair structure.
     *
     * @param o the other object
     * @return always 0.
     */
    @Override
    public int compareTo(C45 o) {
        return 0;
    }
}

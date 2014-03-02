package libai.classifiers;

import java.util.*;

/**
 * DataSet is a container compound by examples. Each example (or DataRecord) is
 * a set of named attributes either discrete or continuous that represents a
 * particular case of some type of class object. This DataSet contain methods to
 * manipulate those DataRecord and calculate information gain, and entropy among
 * the dataset.
 * <pre>
 * TODO:
 * - methods to read from .names/.data files
 * - methods to export in format .names/.data
 * - methods to read from databases tables?
 * - optimizations in the sorting to avoid multiple sorting.
 * </pre>
 *
 * @author kronenthaler
 */
public class DataSet implements Iterable<DataRecord> {
	private Vector<DataRecord> data;
	private int output;
	private int attributeCount;
	private HashSet<Attribute> classes;
    
	/**
	 * Constructor. This constructor allows to create the DataRecords from
	 * unnamed attributes and setting the name positionally after the creation
	 * of the data set. Is a simplification of the next constructor.
	 *
	 * @param o	the index of the attribute considered as the output.
	 * @param names	an array with the names of each attribute
	 * @param data_	zero or more data records to build this data set.
	 */
	public DataSet(int o, String[] names, DataRecord... data_) {
		this(o, data_);
		for (DataRecord d : data) {
			for (int i = 0; i < d.getAttributeCount(); i++)
				d.get(i).setName(names[i]);
		}
	}

	/**
	 * Constructor. This constructor allows to create the DataRecords from named
	 * attributes. Also get some basic values used in the other methods.
	 *
	 * @param o	the index of the attribute considered as the output.
	 * @param data_	zero or more data records to build this data set.
	 */
	public DataSet(int o, DataRecord... data_) {
		data = new Vector<DataRecord>();
		classes = new HashSet<Attribute>();
		for (DataRecord d : data_) {
			data.add(d);
			Attribute outputClass = d.get(o);
			classes.add(outputClass);
		}

		output = o;

		if (data_.length > 0)
			attributeCount = data.get(0).getAttributeCount();
	}

	/**
	 * Constructor. Create a data set from another data set. The new dataset
	 * will be the slice from
	 * <code>src[lo,hi)</code>
	 *
	 * @param src Original data set
	 * @param lo Lower bound (inclusive)
	 * @param hi Upper bound (exclusive)
	 */
	public DataSet(DataSet src, int lo, int hi) {
		if (lo < 0)
			throw new IllegalArgumentException("lo < 0, should be non-negative");
        
        classes = new HashSet<Attribute>();
        output = src.output;
		attributeCount = src.attributeCount;
        
		//take the info from other dataset.
		data = new Vector<DataRecord>();
		for (int i = lo; i < hi; i++){
			data.add(src.data.get(i));
            classes.add(src.data.get(i).get(output));
        }
	}

	/**
	 * Add a new DataRecord to this DataSet.
	 *
	 * @param r the DataRecord to be added.
	 */
	public void addRecord(DataRecord r) {
		data.add(r);
		attributeCount = r.getAttributeCount();
		classes.add(r.get(output));
	}

	/**
	 * Add a new DataRecord to this DataSet and set the names to the attributes.
	 *
	 * @param names the names to be set.
	 * @param r the DataRecord to be added.
	 */
	public void addRecord(String[] names, DataRecord r) {
		addRecord(r);
		for (int i = 0; i < names.length; i++)
			r.get(i).setName(names[i]);
	}

	/**
	 * Sort this DataSet over a particular attribute
	 * <code>a</code>. As the attributes are comparable, the final sorting is
	 * decided by that function.
	 *
	 * @param a column to order by
	 */
	public void sortOver(final int a) {
		Collections.sort(data, new Comparator<DataRecord>() {
			@Override
			public int compare(DataRecord o1, DataRecord o2) {
				int ret = o1.get(a).compareTo(o2.get(a));
				if (ret == 0)
					return o1.get(output).compareTo(o2.get(output));
				return ret;
			}
		});
	}

	/**
	 * Calculate the information gain among the DataRecord's between
	 * <code>[lo,hi)</code> over the attribute
	 * <code>a</code>. Returns an array with the following values:{total
	 * entropy,amount of elements}
	 *
	 * @param lo	Lower bound (inclusive)
	 * @param hi	Upper bound (exclusive)
	 * @param a	Attribute to compare.
	 * @return	an array with the total entropy and the amount of elements
	 * involved (hi-lo).
	 */
	private double[] info(int lo, int hi, int a) {
		HashMap<String, Integer> freq = new HashMap<String, Integer>();
		for (int i = lo; i < hi; i++) {
			if (!(data.get(i).get(a) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute) data.get(i).get(a)).getValue();
			if (freq.get(v) == null)
				freq.put(v, 0);
			freq.put(v, freq.get(v) + 1);
		}

		double total = hi - lo;
		double acum = 0;
		for (String e : freq.keySet()) {
			int f = freq.get(e);
			if (f != 0) {
				double p = (f / total);
				acum += -p * (Math.log10(p) / Math.log10(2));
			}
		}
		return new double[]{acum, total};
	}

	/**
	 * Calculate the average information gain among the DataRecord's in
	 * <code>[lo,hi)</code> over the attribute
	 * <code>a</code>. The array returned depends of the nature of the attribute
	 * <code>a</code>. If
	 * <code>a</code> is discrete, the array will be the returned by
	 * <code>infoAvgDiscrete</code> otherwise, will be the returned by
	 * <code>infoAvgContinuous</code>
	 *
	 * @param lo	Lower bound (inclusive)
	 * @param hi	Upper bound (exclusive)
	 * @param a	Attribute to compare.
	 * @return an array depending of the type of <code>a</code>
	 */
	private double[] infoAvg(int lo, int hi, int a) {
		sortOver(a);

		if (data.get(lo).get(a) instanceof DiscreteAttribute)
			return infoAvgDiscrete(lo, hi, a);
		else
			return infoAvgContinuous(lo, hi, a);
	}

	/**
	 * Calculate the average information gain among the DataRecord's
	 * <code>[lo,hi)</code> over a discrete attribute
	 * <code>a</code>. Return an array with two positions: {average info gain,
	 * splitinfo gain}. The split information gain, is how much will be gained
	 * if you split the dataset over tha attribute
	 * <code>a</code>. Useful to calculate the Gain Ratio.
	 *
	 * @param lo	Lower bound (inclusive)
	 * @param hi	Upper bound (exclusive)
	 * @param a	Attribute to compare.
	 * @return an array with the average information gain and the split
	 * information gain.
	 */
	private double[] infoAvgDiscrete(int lo, int hi, int a) {
		double acum = 0;
		double splitInfo = 0;
		double total = hi - lo;
		for (int i = lo; i < hi;) {
			int j = 0;
			int nlo = i;

			for (j = i; j < hi - 1; j++, i++)
				if (!data.get(j).get(a).equals(data.get(j + 1).get(a)))
					break;
			i++;

			double[] res = info(nlo, i, output);
			acum += res[0] * (res[1] / total);
			splitInfo += -(res[1] / total) * (Math.log10(res[1] / total) / Math.log10(2));
		}
		return new double[]{acum, splitInfo};
	}

	/**
	 * Calculate the average information gain among the DataRecord's
	 * <code>[lo,hi)</code> over a continuous attribute
	 * <code>a</code>. Return an array with four positions: {maximum information
	 * gain possible, maximum split information gain, best value for the split,
	 * index of the best value}. Over continuous attributes, the information
	 * gain is used in a binary way, lower than the best split and greater than
	 * the best split. To calculate the best split possible is necessary test
	 * all the possible splits in the continuous samples.
	 *
	 * @param lo	Lower bound (inclusive)
	 * @param hi	Upper bound (exclusive)
	 * @param a	Attribute to compare.
	 * @return an array with the average information gain and the split
	 * information gain.
	 */
	private double[] infoAvgContinuous(int lo, int hi, int a) {
		double splitInfo = 0;
		double total = hi - lo;

        //1. Calculate the total frequencies; they are the same for all the attributes.
        //   Bottleneck #1, how to avoid to recalculate the frequencies in each creation.
        //   => the frequencies can be recalculated when the spil is done, substracting the 
        //   amount of elements that are left out on this range.
		HashMap<String, Integer> totalFreq = new HashMap<String, Integer>();
		HashMap<Double, HashMap<String, Integer>> freqAcum = new HashMap<Double, HashMap<String, Integer>>();

		for (Attribute att : classes) {
			totalFreq.put(((DiscreteAttribute) att).getValue(), 0);
		}

		for (int i = lo; i < hi; i++) {
			if (!(data.get(i).get(output) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute) data.get(i).get(output)).getValue();
			if (totalFreq.get(v) == null)
				totalFreq.put(v, 0);
			totalFreq.put(v, totalFreq.get(v) + 1);

			double va = ((ContinuousAttribute) data.get(i).get(a)).getValue();
			if (freqAcum.get(va) == null) {
				freqAcum.put(va, new HashMap<String, Integer>());
				for (Attribute e : classes) {
					if (i - 1 < 0)
						freqAcum.get(va).put(((DiscreteAttribute) e).getValue(), 0);
					else {
						double pva = ((ContinuousAttribute) data.get(i - 1).get(a)).getValue();
						freqAcum.get(va).put(((DiscreteAttribute) e).getValue(), freqAcum.get(pva).get(((DiscreteAttribute) e).getValue()));
					}
				}
			}
			freqAcum.get(va).put(v, freqAcum.get(va).get(v) + 1);
		}

		double maxInfo = -Double.MIN_VALUE;
		double maxSplitInfo = -Double.MIN_VALUE;
		double bestSplitValue = Integer.MAX_VALUE;
		int bestIndex = -1000;

        //2. Create a table to know the accumulated frequencies until certain index. (same value, same previous table.)
		for (int i = lo; i < hi; i++) {
			double value = ((ContinuousAttribute) data.get(i).get(a)).getValue();

			HashMap<String, Integer> freq = freqAcum.get(value);

			double total2 = i - lo + 1;
			double acum2 = 0;

			double total3 = total - total2;
			double acum3 = 0;

			for (String e : freq.keySet()) {
				int f = freq.get(e);
				if (f != 0) {
					double p = (f / total2);
					acum2 += -p * (Math.log10(p) / Math.log10(2));
				}

				f = totalFreq.get(e) - freq.get(e);
				if (f != 0) {
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
				int k = 0;
				for (k = i + 1; k < hi; k++) {
					double nextValue = ((ContinuousAttribute) data.get(k).get(a)).getValue();
					if (value != nextValue) {
						bestSplitValue = (value + nextValue) / 2;
						bestIndex = k;
						break;
					}
				}

				if (k == hi) {
					bestSplitValue = value;
					bestIndex = hi - 1;
				}

				maxSplitInfo = splitInfo;
			}
		}
		return new double[]{maxInfo, maxSplitInfo, bestSplitValue, bestIndex};
	}

	/**
	 * Calculate the absolute information gain among the elements in
	 * <code>[lo,hi)</code> over the attribute
	 * <code>a</code>. Returns an array with 4 or 6 positions
	 * (2+length(infoAvgDiscrete|infoAvgContinuous)): {absolute gain, gain
	 * ratio, values of infoAvgDiscrete|Continuous}
	 *
	 * @param lo	Lower bound (inclusive)
	 * @param hi	Upper bound (exclusive)
	 * @param a	Attribute to compare.
	 * @return an array with absolute gain, gain ratio plus the infoAvg* values.
	 */
	public double[] gain(int lo, int hi, int a) {
		double info[] = infoAvg(lo, hi, a);
		double gain = info(0, data.size(), output)[0] - info[0];
		double gainRatio = gain / info[1];

		if (info.length > 2)
			return new double[]{gain, gainRatio, info[0], info[1], info[2], info[3]};
		else
			return new double[]{gain, gainRatio, info[0], info[1]};
	}

	/**
	 * @return the attribute index used as output.
	 */
	public int getOutputIndex() {
		return output;
	}

	/**
	 * @return the number of attributes in the DataSet.
	 */
	public int getAttributeCount() {
		return attributeCount;
	}
	
	/**
	 * @return the number of DataRecord's in this DataSet.
	 */
	public int getItemsCount() {
		return data.size();
	}

	/**
	 * @param index DataRecord to retrieve
	 * @return DataRecord in the <code>index</code> position.
	 */
	public DataRecord get(int index) {
		return data.get(index);
	}

	/**
	 * @return A set with all the different output classes in this DataSet.
	 */
	public Set<Attribute> getClasses() {
		return Collections.unmodifiableSet(classes);
	}

	/**
	 * @return <code>true</code> if all the DataRecords have the same
	 * output, <code>false</code> otherwise.
	 */
	public boolean allTheSameOutput() {
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).get(output).equals(data.get(0).get(output)))
				return false;
		}
		return true;
	}

	/**
	 * Check if all the DataRecord are equal in the DataSet. Return the most
	 * common output class.
	 *
	 * @return the most common output class if all are equal, <code>null</code>
	 * otherwise.
	 */
	public Attribute allTheSame() {
		HashMap<String, Integer> freq = new HashMap<String, Integer>();

		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < attributeCount; j++) {
				if (j != output && 
					!data.get(i).get(j).equals(data.get(0).get(j)))
					return null;
			}

			if (!(data.get(i).get(output) instanceof DiscreteAttribute))
				throw new IllegalArgumentException("The output attribute must be discrete");

			String v = ((DiscreteAttribute) data.get(i).get(output)).getValue();
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
		return new DiscreteAttribute(((DiscreteAttribute) data.get(0).get(output)).getName(),
				mostCommon);
	}

	/**
	 * Split this data set into two new dataset where the proportion between the
	 * output classes is kept. The first dataset contains the
	 * <code>proportion</code> of the original data set, for instance, if the
	 * data set has 100 elements distributed between 2 classes, in a 60/40
	 * proportion, this first set will contain (60*proportion + 40*proportion)
	 * elements and the second data set will contain the rest. This method is
	 * useful to generate training/test sets from one massive data set.
	 *
	 * @param proportion the percentage of element to be keep of each class for
	 * the first data set.
	 * @return an array of 2 positions with the dataset as described above.
	 */
	public DataSet[] splitKeepingRelation(double proportion) {
		sortOver(output);

		DataSet a = new DataSet(output);
		DataSet b = new DataSet(output);

		for (int i = 0; i < data.size();) {
			int lo = i;
			while (i < data.size()
					&& data.get(lo).get(output).compareTo(data.get(i++).get(output)) == 0) {
			}
			int hi = i;

			DataSet temp = new DataSet(this, lo, hi);
			Collections.shuffle(temp.data);
			int j, n;
			for (j = 0, n = (int) (temp.data.size() * proportion); j < n; j++)
				a.addRecord(temp.data.get(j));

			for (n = temp.data.size(); j < n; j++)
				b.addRecord(temp.data.get(j));
		}
		return new DataSet[]{a, b};
	}

	/**
	 * Print to the stderr this dataset in a human readable form.
	 *
	 * @param deep the start string to be prepend on each line.
	 */
	public void print(String deep) {
		System.err.println(deep + "DATASET");
		for (int i = 0; i < data.size(); i++) {
			System.err.print(deep);
			for (int j = 0; j < attributeCount; j++)
				System.err.print(data.get(i).get(j) + "\t\t");
			System.err.println("");
		}
		System.err.println(deep + "END DATASET");
	}

    @Override
    public Iterator<DataRecord> iterator() {
        return data.iterator();
    }
}
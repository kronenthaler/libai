package libai.classifiers.refactor;

import java.util.*;
import libai.classifiers.Attribute;
import libai.classifiers.DataRecord;

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
public interface DataSet extends Iterable<DataRecord> {
    public int getOutputIndex();
    public Set<Attribute> getClasses();
    public int getItemsCount();
    public MetaData getMetaData(); //columns definitions
    
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
	public DataSet[] splitKeepingRelation(double proportion);
}

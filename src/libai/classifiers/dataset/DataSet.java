package libai.classifiers.dataset;

import java.util.*;
import libai.classifiers.Attribute;
import libai.common.*;

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
public interface DataSet extends Iterable<List<Attribute>> {

    /**
     * @return The index of the field used as output.
     */
    public int getOutputIndex();

    /**
     * @return The total count of elements in this data set.
     */
    public int getItemsCount();

    /**
     * @return A MetaData object containing information about the attributes on
     * the data set.
     */
    public MetaData getMetaData(); //columns definitions

    /**
     * Sort the data set over the fieldIndex as primary key and the output index
     * to break any ties. This index is remember for future internal references.
     *
     * @param fieldIndex The field to sort over
     * @return A iterable representation of this data set sorted over the field
     * index.
     */
    public Iterable<List<Attribute>> sortOver(int fieldIndex);

    /**
     * Sort the data set over the fieldIndex as primary key and the output index
     * to break any ties, and limit the elements to [lo, hi). This index is
     * remember for future internal references.
     *
     * @param lo The lower bound (inclusive) of the data set to be returned
     * @param hi The upper bound (exclusive) of the data set to be returned.
     * @param fieldIndex The field to sort over
     * @return A iterable representation of this data set sorted over the field
     * index from [lo, hi).
     */
    public Iterable<List<Attribute>> sortOver(int lo, int hi, int fieldIndex);

    /**
     * Create a slice of this data set as a new data set from [lo, hi).
     *
     * @param lo The lower bound (inclusive) of the data set to be returned
     * @param hi The upper bound (exclusive) of the data set to be returned.
     * @return A new data set that is the copy of this from [lo, hi)
     */
    public DataSet getSubset(int lo, int hi);

    /**
     * @return True if all the classes (value of output index) are the same.
     */
    public boolean allTheSameOutput();

    /**
     * Returns the most common output attribute if the rest of the attributes
     * are exactly the same over the whole data set. If there is one single
     * record with one single attribute different from the rest, then this
     * method will return null.
     *
     * @return The most common output attribute or null if there is one record
     * different from the rest.
     */
    public Attribute allTheSame();

    /**
     * Splits this data set into two new dataset where the proportion between
     * the output classes is kept. The first dataset contains the
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

    /**
     * Gets a map between the different values of the attribute at the
     * fieldIndex and their respective frequencies. It limits the count space to
     * [lo, hi).
     *
     * @param lo The lower bound (inclusive) of the data set to be returned
     * @param hi The upper bound (exclusive) of the data set to be returned.
     * @param fieldIndex The field to count
     * @return a map with the different values and their respective frequencies.
     */
    public HashMap<Attribute, Integer> getFrequencies(int lo, int hi, int fieldIndex);

    /**
     * Closes the underlying data source if possible.
     */
    public void close();

    public int getFrecuencyOf(Pair<Integer, Attribute>... values);
    
    public Iterable<List<Attribute>> getCombinedValuesOf(int... values);
}

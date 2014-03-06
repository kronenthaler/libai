package libai.classifiers.refactor;

import java.util.Set;
import libai.classifiers.Attribute;

/**
 *
 * @author kronenthaler
 */
public interface MetaData {
    public boolean isCategorical(int fieldIndex);
    public String getAttributeName(int fieldIndex);
    public int getAttributeCount();
    public Set<Attribute> getClasses();
}
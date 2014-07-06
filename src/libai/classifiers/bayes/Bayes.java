package libai.classifiers.bayes;

import java.io.File;
import java.util.List;
import libai.classifiers.Attribute;
import libai.common.dataset.*;
import org.w3c.dom.Node;
/**
 *
 * @author kronenthaler
 */
public abstract class Bayes {
    protected int outputIndex; //can be removed when tge CPT is used, it can be part of the BIF 0.50 specification.
    protected int totalCount; //can be removed when the CPT is used.
    protected MetaData metadata; //can be removed when the CPT is used.
    protected CountTree countTree;
    
    // include a method to create a frequency tree and use it for every counting related activity.
    protected void initCountTree(DataSet ds){
        countTree = new CountTree(ds);
    }
    
    /** 
     * Train a Bayes system given a dataset. Depending of the implementation it
     * might train structure and weight alike.
     * @params ds DataSet to learn the Bayes system from.
     * @return An instance of the same type ready to evaluate vectors of evidence.
     */
    public abstract Bayes train(DataSet ds);
    
    /** Calculates the condition probability of h given the vector of evidence x. */
    protected abstract double P(Attribute h, List<Attribute> x);
    
    //this might be reimplemented here if both algorithms use the CPTable structure.
    public abstract Attribute eval(int outputIndex, List<Attribute> x);
    
    //need some factory methods.
    // implement http://www.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/ XMLBIF
    public abstract boolean save(File path);
    
    protected abstract Bayes load(Node root);
}

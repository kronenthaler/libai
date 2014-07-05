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
    protected int outputIndex;
    protected int totalCount;
    protected MetaData metadata;
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
    
    /** 
     * Calculates the maximum posterior probability this data record (x) in the data set
     * against all possible output classes, returns the most likely output using: 
     * P(Ci|x) > P(Cj|x) 1 <= j < m, i!=j
     * @param x Vector of evidences
     * @return The most likely output class for the given evidence.
     */
    public Attribute eval(List<Attribute> x) {
        Attribute winner = null;
        double max = -Double.MAX_VALUE;
        for (Attribute c : metadata.getClasses()) {
            double tmp = P(c, x);
            if (tmp > max) {
                max = tmp;
                winner = c;
            }
        }
        
        return winner;
    }
    
    //need some factory methods.
    // implement http://www.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/ XMLBIF
    public abstract boolean save(File path);
    
    protected abstract Bayes load(Node root);
}

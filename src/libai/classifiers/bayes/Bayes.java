package libai.classifiers.bayes;

/**
 *
 * @author kronenthaler
 */
public class Bayes {
    //add methods to calculate the P(A|C) and so on, take them from the naive bayes implementation and improve the calculation where possible.
    //precalculate:
    //frequencies of each categorical attribute
    //mean & SD of the continuos attributes
    //frequencies of pair of attributes (too much memory?)
    //frecuencies of triplets of attributes (too much memory?) 
    //find a way to calculate the I(x,y|C) using a single query... => move the calculation to the DataSet
    
    //P(x,y) = P(x|y)
    //P(x,y,c) = P(x,y|c)
    //P(x)
}

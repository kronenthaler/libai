package libai.classifiers.bayes;

import java.util.List;
import libai.classifiers.Attribute;
import libai.common.dataset.DataSet;

/**
 *
 * @author kronenthaler
 */
public class BayesNetworkK2 extends BayesNetwork{
    private List<Integer> ordering;
    
    public BayesNetworkK2(){
    }
    
    public BayesNetworkK2(List<Integer> ordering){
        this.ordering = ordering;
    }
    
    @Override
    public BayesNetworkK2 train(DataSet ds) {
        //do it.
        return null;
    }
}

package libai.classifiers.bayes;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import libai.classifiers.*;
import libai.common.*;
import libai.common.dataset.DataSet;
import libai.common.dataset.MySQLDataSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author kronenthaler
 */
public class K2 extends BayesNetwork{
    private List<Integer> ordering;
    private int upperBound;
    
    public K2(){
    }
    
    public K2(List<Integer> ordering, int upperBound){
        this();
        this.ordering = ordering;
        this.upperBound = upperBound;
    }
    
    @Override
    protected Graph getStructure(DataSet ds, double eps) {    
        Graph G = new Graph(ds.getMetaData().getAttributeCount());
        int N = G.getVertexCount();
        for(int i=0;i<N;i++){
            Set<Integer> parents = new HashSet<Integer>();
            double Pold = f(ds, i, parents);
            boolean okToProceed = true;
            while(okToProceed && parents.size() < upperBound){
                //argmax_z{f(i,parents + Pred(xi))}
                int bestZ = 0;
                double Pnew = -Double.MAX_VALUE;
                for(Integer z : ordering){
                    if(z == i) break;
                    if(parents.contains(z)) continue;
                    
                    parents.add(z);
                    double f = f(ds, i, parents);
                    if(f > Pnew){
                        Pnew = f;
                        bestZ = z;
                    }
                    parents.remove(z);
                }
                
                if(Pnew > Pold){
                    Pold = Pnew;
                    parents.add(bestZ);
                }else
                    okToProceed = false;
            }
            
            //parents of Xi = parents.
            for(Integer pi : parents){
                G.addEdge(pi, i, false);
            }
        } 
        
        return G;
    }
    
    private double f(DataSet ds, int i, Set<Integer> parents){
        int N = ds.getItemsCount();
        Map<Attribute,Integer> R = ds.getFrequencies(0, N, i);
        double result = 0;
        
        if(!parents.isEmpty()){
            int phi[] = new int[parents.size()];
            int k=0;
            for(int v : parents)
                phi[k++] = v;
            
            for(List<Attribute> record : ds.getCombinedValuesOf(phi)){
                List<Pair<Integer,Attribute>> alpha = new ArrayList<Pair<Integer,Attribute>>();
                for(int t=0;t<phi.length;t++)
                    alpha.add(new Pair<Integer,Attribute>(phi[t], record.get(phi[t])));

                result += count(R,i,alpha);
            }
        }else{
            List<Pair<Integer,Attribute>> alpha = new ArrayList<Pair<Integer,Attribute>>();
            result += count(R,i,alpha);
        }
        
        return result;
    }
    
    private double count(Map<Attribute,Integer> R, int i, List<Pair<Integer, Attribute>> alpha){
        long Nij = 0;
        double alphaijk = 0;
        for(Attribute ri : R.keySet()){
            alpha.add(new Pair<Integer, Attribute>(i, ri));
            int freq = countTree.getCount(alpha.toArray(new Pair[0]));
            Nij += freq; 
            alphaijk += factorialLog(freq);
            alpha.remove(alpha.size()-1);
        }

        double nominator = factorialLog(R.size()-1);
        double denominator = factorialLog(Nij + R.size() - 1);

        return nominator - denominator + alphaijk;
    }
    
    private double factorialLog(long n){
        double fact = 0;
        for(long i=n;i>=1;i--) 
            fact += Math.log(i);
        return fact;
    }
    
    //Factories
    public static K2 getInstance(File path) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(path));
            Node root = doc.getElementsByTagName("NaiveBayes").item(0);

            return (K2)new K2().load(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static K2 getInstance(DataSet ds) {
        return (K2)new K2().train(ds);
    }
    
    public static void main(String arg[])throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "asia", 0);
        
        List<Integer> ordering = new ArrayList<Integer>();
        //[Visit to Asia, Tuberculosis, Smoking, Lung Cancer, TuberculosisorCancer, X-ray results, Bronchitis, Dyspnea
        //2,3,0, 1, 4, 5, 6, 7
        ordering.add(2);
        ordering.add(3);
        ordering.add(0);
        ordering.add(1);
        ordering.add(4);
        ordering.add(5);
        ordering.add(6);
        ordering.add(7);
//        ordering.add(0);
//        ordering.add(1);
//        ordering.add(2);
                
        K2 bn = new K2(ordering, 1);
        bn.train(ds);
    }
}

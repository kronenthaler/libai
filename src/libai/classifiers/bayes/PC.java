package libai.classifiers.bayes;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import libai.classifiers.*;

import libai.common.*;
import libai.common.dataset.DataSet;
import libai.common.dataset.MySQLDataSet;


/**
 *
 * @author kronenthaler
 */
public class PC extends BayesSystem {
    private Map<Pair<Integer, Integer>, Set<Integer>> SepSet = new HashMap<Pair<Integer, Integer>, Set<Integer>>();
    private Map<Pair<Integer, Integer>, Double> cacheCorrelation = new HashMap<Pair<Integer, Integer>, Double>();
    private Map<Triplet<Integer, Integer, Integer>, Double> cacheCorrelation3 = new HashMap<Triplet<Integer, Integer, Integer>, Double>();

    @Override
    protected Graph getStructure(DataSet ds, double eps) {
        int N = ds.getMetaData().getAttributeCount();
        Graph G = new Graph(N);
        G.getM().setValue(1); //fully connected.
        for(int i=0;i<N;i++)
            G.removeEdge(i, i);

        int k = 0;
        boolean arcRemoved = true;
        while(arcRemoved){
            Graph Gk = new Graph(G);
            arcRemoved = false;
            for (int x = 0; x < N; x++) {
                for (int y = x; y < N; y++) {
                    if (x == y || !G.isEdge(x, y, true))
                        continue;
                    
                    Set<Integer> Aab = G.neighbors(x, true);
                    Aab.addAll(G.neighbors(y, true));
                    Aab.remove(y);
                    Aab.remove(x);
                    
                    Set<Integer> Uab = G.adjacencyPath(x, y, true, true);
                    Set<Integer> intersection = new HashSet<Integer>(Aab);
                    intersection.retainAll(Uab);
                    
                    if (intersection.size() >= k && Aab.size() >= k) { 
                        if (isDSeparable(ds, G, x, y, k+1, intersection, eps)) { //check all subsets of S 
                            Gk.removeEdge(x, y, true);
                            arcRemoved = true;
                        }
                    }
                }
            }
            //swap Gaux to G
            G = Gk;
            k++;
        }
        
        return orientEdges(G);
    }

    private Graph orientEdges(Graph G) {
        int N = G.getVertexCount();
        
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                if (x == y)
                    continue;
                for (int z = 0; z < N; z++) {
                    if (x == z || z == y)
                        continue;
                    if (G.isEdge(x, y, true)
                        && G.isEdge(z, y, true)
                        && !G.isEdge(x, z, true)) {
                        
                        Set<Integer> C = SepSet.get(new Pair<Integer, Integer>(x, z));
                        if (C != null && !C.contains(y)) {
                            G.removeEdge(x, y, true);
                            G.removeEdge(z, y, true);

                            G.addEdge(x, y, false);
                            G.addEdge(z, y, false);
                        }
                    }
                }
            }
        }
        
        for(int x=0;x<N;x++){
            for(int y=0;y<N;y++){
                if(x==y) continue;
                for(int z=0;z<N;z++){
                    if(x==z || y==z) continue;
                    if(G.isParent(x, y) &&
                       G.isEdge(y, z, true) &&
                       !G.isEdge(x, z, true) &&
                       !G.isOriented(y, z)){
                        G.removeEdge(y, z, true);
                        G.addEdge(y, z, false);
                    }
                }
            }
        }
        
        for(int x=0;x<N;x++){
            for(int y=0;y<N;y++){
                if(x==y || !G.isEdge(x, y, true)) continue;
                if(!G.isOriented(x, y) && G.adjacencyPath(x, y, false).size() > 0){
                    G.removeEdge(x,y,true);
                    G.addEdge(x,y,false);
                }
            }
        }
        
        return G;
    }

    private boolean isDSeparable(DataSet ds, Graph G, int X, int Y, int k, Set<Integer> adjacencies, double eps) {
        List<Set<Integer>> adjs = getSubsets(adjacencies, k);
        for (Set<Integer> S : adjs) {
            if (I(ds, X, Y, S, eps)) {
                SepSet.put(new Pair<Integer, Integer>(X, Y), S);
                SepSet.put(new Pair<Integer, Integer>(Y, X), S);
                return true;
            }
        }

        return false;
    }

    private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current, List<Set<Integer>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<Integer>(current));
            return;
        }

        //unseccessful stop clause 
        if (idx == superSet.size())
            return;

        Integer x = superSet.get(idx);
        current.add(x);

        //"guess" x is in the subset 
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);

        //"guess" x is not in the subset 
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    public static List<Set<Integer>> getSubsets(Set<Integer> superSet, int k) {
        List<Set<Integer>> res = new ArrayList<Set<Integer>>();
        getSubsets(new ArrayList<Integer>(superSet), k, 0, new HashSet<Integer>(), res);
        return res;
    }
    
    private boolean I(DataSet ds, int X, int Y, Set<Integer> Z, double eps){
        int N = ds.getItemsCount();
        Map<Attribute, Integer> x = ds.getFrequencies(0, N, X);
        Map<Attribute, Integer> y = ds.getFrequencies(0, N, Y);
        int df = (x.size()-1)*(y.size()-1);
        double chi = 0;
        
        if(Z.isEmpty()){
            chi = chiSquared(ds, X, Y, new ArrayList<Pair<Integer, Attribute>>());
            return pValue(chi, df) <= eps;
        }
        
        int[] z = new int[Z.size()];
        int i=0;
        for(Integer z1 : Z){
            z[i++] = z1;
        }
        
        for(List<Attribute> record : ds.getCombinedValuesOf(z)){
            List<Pair<Integer, Attribute>> xyz = new ArrayList<Pair<Integer, Attribute>>();
            for(int k=0;k<z.length;k++){
                xyz.add(new Pair<Integer, Attribute>(z[k], record.get(z[k])));
            }
            chi = chiSquared(ds, X, Y, xyz);
            if(pValue(chi, df) < eps)
                return false;
        }
        
        return true;
    }
    
    private double chiSquared(DataSet ds, int X, int Y, List<Pair<Integer, Attribute>> xyz){
        double chi = 0;
        int M = countTree.getCount(xyz.toArray(new Pair[0]));
        int N = ds.getItemsCount();
        Map<Attribute, Integer> x = ds.getFrequencies(0, N, X);
        Map<Attribute, Integer> y = ds.getFrequencies(0, N, Y);
        
        for(Attribute xi : x.keySet()){
            for(Attribute yj : y.keySet()){
                xyz.add(new Pair<Integer, Attribute>(X, xi));
                double pi = countTree.getCount(xyz.toArray(new Pair[0]));
                xyz.remove(xyz.size()-1);

                xyz.add(new Pair<Integer, Attribute>(Y, yj));
                double pj = countTree.getCount(xyz.toArray(new Pair[0]));

                xyz.add(new Pair<Integer, Attribute>(X, xi));
                double pij = countTree.getCount(xyz.toArray(new Pair[0]));
                xyz.remove(xyz.size()-1); //x
                xyz.remove(xyz.size()-1); //y
                
                double Eij = (pi * pj) / (double)M;
                if(Eij > 1.e-7){
                    chi += ((pij - Eij)*(pij - Eij)) / Eij;
                }
            }
        }
        return chi;
    }
    
    private double pValue(double x, double k){
        return 1 - Gamma.incompleteGamma(x/2.0, k/2.0);
    }
    
    public static PC getInstance(File xmlbif){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(xmlbif));
            Node root = doc.getElementsByTagName("NETWORK").item(0);

            return (PC)new PC().load(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static PC getInstance(DataSet ds) {
        return (PC)new PC().train(ds);
    }
}

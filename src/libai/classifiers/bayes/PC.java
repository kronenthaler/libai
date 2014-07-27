package libai.classifiers.bayes;

import com.sun.corba.se.impl.util.RepositoryId;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import libai.classifiers.Attribute;

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
            arcRemoved = false;
            for (int x = 0; x < N; x++) {
                for (int y = 0; y < N; y++) {
                    if (x == y || !G.isEdge(x, y, false))
                        continue;
                    Set<Integer> adjacencies = G.neighbors(x, true);
                    adjacencies.remove(y);
                    if (adjacencies.size() >= k) { 
                        if (isDSeparable(ds, G, x, y, k, adjacencies)) { //check all subsets of S 
                            G.removeEdge(x, y, true);
                            arcRemoved = true;
                        }
                    }
                }
            }
            k++;
        }
        
        System.err.println("orienting");
        G.saveAsDot(new File("plain.dot"), false, names);
        
        return orientEdges(G);
    }

    private Graph orientEdges(Graph G) {
        int N = G.getVertexCount();
        System.err.println("Identifying colliders ");
        //C. identify colliders: for each triple x,y,z that: x-y and y-z but not x-z and Y not in sepset(x,z) then orient x->y<-z
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
                            System.err.printf("%s->%s<-%s\n", names[x],names[y],names[z]);
                            G.removeEdge(x, y, true);
                            G.removeEdge(z, y, true);

                            G.addEdge(x, y, false);
                            G.addEdge(z, y, false);
                        }
                    }
                }
            }
        }
        
        System.err.println("the rest");
        //D. repeat
        //If A -> B, B and C are adjacent, A and C are not adjacent, and there is no
        //arrowhead at B, then orient B - C as B -> C.
        //If there is a directed path from A to B, and an edge between A and B, then orient
        //A - B as A -> B.
        //until no more edges can be oriented.
        /*
        //check this part only.
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int a = 0; a < N; a++) {
                for (int b = 0; b < N; b++) {
                    if (a == b || !G.isEdge(a, b, true))
                        continue;

                    if (G.isOriented(a, b)) {
                        for (int c : G.neighbors(b, true)) {
                            if (c == a)
                                continue;
                            if (!G.isEdge(a, c, true)
                                    && !G.isEdge(c, b, false)) {
                                G.removeEdge(b, c, true);
                                G.addEdge(b, c, false);

                                changed = true;
                            }
                        }
                    } else {
                        if (!G.adjacencyPath(a, b, false).isEmpty()) {
                            G.removeEdge(a, b, true);
                            G.addEdge(a, b, false);

                            changed = true;
                        }
                    }

                }
            }
        }
        */
        return G;
    }

    private boolean isDSeparable(DataSet ds, Graph G, int X, int Y, int k, Set<Integer> adjacencies) {
        System.err.printf("isD-separable(%d,%d,%d,%s)\n",k,X,Y,adjacencies.toString());
        List<Set<Integer>> adjs = getSubsets(adjacencies, k);
        System.err.printf("All subsets: %s\n", adjs.toString());
        for (Set<Integer> S : adjs) {
            int freedom = S.isEmpty() ? 2 : 3;
            double Rxys = correlation(ds, X, Y, S);
            double t = Rxys * Math.sqrt(G.getVertexCount()-freedom);
            t /= Math.sqrt(1-(Rxys * Rxys));
            if (Math.abs(t) < 0.05) { //0.05 from the documentation, lower than that is exagerated.
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

    private double correlation(DataSet ds, int X, int Y, Set<Integer> Z) {
        if (Z.isEmpty()) {
            return correlation(ds, X, Y);
        }

        if (Z.size() == 1) {
            int z = Z.iterator().next();
            Triplet<Integer,Integer,Integer> key = new Triplet<Integer,Integer,Integer>(X,Y,z);
            Triplet<Integer,Integer,Integer> key2 = new Triplet<Integer,Integer,Integer>(Y,X,z);

            if (cacheCorrelation3.containsKey(key))
                return cacheCorrelation3.get(key);

            double pxy = correlation(ds, X, Y);
            double pxz = correlation(ds, X, z);
            double pyz = correlation(ds, Y, z);

            double result = (pxy - (pxz * pyz)) / (Math.sqrt(1 - (pxz * pxz)) * Math.sqrt(1 - (pyz * pyz)));
            cacheCorrelation3.put(key, result);
            cacheCorrelation3.put(key2, result);
            return result;
        }

        int z0 = Z.iterator().next();
        Z.remove(z0);

        double pxyz = correlation(ds, X, Y, Z);
        double pxz0z = correlation(ds, X, z0, Z);
        double pyz0z = correlation(ds, Y, z0, Z);

        double result = (pxyz - (pxz0z * pyz0z)) / (Math.sqrt(1 - (pxz0z * pxz0z)) * Math.sqrt(1 - (pyz0z * pyz0z)));
        return result;
    }

    //n * sum(xi*yi) - sum(xi) * sum(yi)
    //-----------------------------------
    //sqrt(n * sum(xi^2)-sum(xi)^2) * sqrt(n * sum(yi^2) - sum(yi)^2)
    private double correlation(DataSet ds, int X, int Y) {
        Pair<Integer, Integer> key = new Pair<Integer, Integer>(X,Y);
        Pair<Integer, Integer> key2 = new Pair<Integer, Integer>(X,Y);

        if (cacheCorrelation.containsKey(key))
            return cacheCorrelation.get(key);

        double xy = 0;
        double x = 0;
        double y = 0;
        double x2 = 0;
        double y2 = 0;

        int N = ds.getItemsCount();
        //iterate over the data set?
        Map<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        Map<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);

        Map<Attribute, Integer> lookupX = new HashMap<Attribute, Integer>();
        Map<Attribute, Integer> lookupY = new HashMap<Attribute, Integer>();

        for (Attribute v : freqX.keySet()) {
            lookupX.put(v, lookupX.size() + 1);
            x += freqX.get(v) * lookupX.get(v);
            x2 += freqX.get(v) * (lookupX.get(v) * lookupX.get(v));
        }

        for (Attribute v : freqY.keySet()) {
            lookupY.put(v, lookupY.size() + 1);
            y += freqY.get(v) * lookupY.get(v);
            y2 += freqY.get(v) * (lookupY.get(v) * lookupY.get(v));
        }

        for (List<Attribute> record : ds.getCombinedValuesOf(X, Y)) {
            //count the combinations of xiyi and multiply for the lookup values
            Pair<Integer, Attribute> xi = new Pair<Integer, Attribute>(X, record.get(X));
            Pair<Integer, Attribute> yi = new Pair<Integer, Attribute>(Y, record.get(Y));
            xy += countTree.getCount(xi, yi) * lookupX.get(record.get(X)) * lookupY.get(record.get(Y));
        }

        double result = (N * xy - (x * y)) / (Math.sqrt((N * x2) - (x * x)) * Math.sqrt((N * y2) - (y * y)));
        cacheCorrelation.put(key, result);
        cacheCorrelation.put(key2, result);
        return result;
    }

    
static String[] names; 
    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "asia", 0);
        
        names = new String[ds.getMetaData().getAttributeCount()];
        for (int i = 0; i < names.length; i++)
            names[i] = ds.getMetaData().getAttributeName(i).replace("-", "_");//
        
        int N = ds.getMetaData().getAttributeCount();
        PC pc = new PC();
        pc.initCountTree(ds);
        Set<Integer> z = new HashSet<Integer>();
        for(int i=0;i<N;i++)
            for(int j=0;j<N;j++)
                for(int k=0;k<N;k++){
                    if(i!=j && i!=k && j!=k){
                        z.clear();
                        z.add(k);
                        System.err.printf("C(%d,%d|%s) = %f\n",i,j,z.toString(),pc.correlation(ds, i, j, z));
                    }
                }
        //if(true) return;
        Graph G = pc.getStructure(ds, 0.01);
               
        
        
        G.saveAsDot(new File("oriented.dot"), true, names);
    }
}

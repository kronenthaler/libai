package libai.classifiers.bayes;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
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
            //copy G to auxG
            Graph Gk = new Graph(G);
            System.err.println("K: "+k);
            arcRemoved = false;
            int t=0;
            for (int x = 0; x < N; x++) {
                for (int y = x; y < N; y++) {
                    if (x == y || !G.isEdge(x, y, true))
                        continue;
                    
                    if(k==2 && x==4 && y == 5)
                        System.err.println("break");
                    
                    if(++t%100==0)
                        System.err.println(t+" / "+(N*N));
                    
                    Set<Integer> Aab = G.neighbors(x, true);
                    Aab.addAll(G.neighbors(y, true));
                    Aab.remove(y);
                    Aab.remove(x);
                    
                    Set<Integer> Uab = G.adjacencyPath(x, y, true, true);
                    Set<Integer> intersection = new HashSet<Integer>(Aab);
                    intersection.retainAll(Uab);
                    
                    //if (adjacencies.size() >= k) { 
                    if (intersection.size() >= k && Aab.size() >= k) { 
                        //if (isDSeparable(ds, G, x, y, k, adjacencies)) { //check all subsets of S 
                        if (isDSeparable(ds, G, x, y, k+1, intersection, eps)) { //check all subsets of S 
                            Gk.removeEdge(x, y, true);
                            arcRemoved = true;
                        }
                    }
                }
            }
            //swap Gaux to G
            //G.saveAsDot(new File(k+"graph.dot"), false, names);
            G = Gk;
            k++;
        }
        
        System.err.println("orienting");
        return orientEdges(G);
    }

    private Graph orientEdges(Graph G) {
        int N = G.getVertexCount();
        
        System.err.println("Identifying colliders "+G.getEdgeCount());
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
                            System.err.printf("%s -> %s <- %s\n", names[x],names[y],names[z]);
                            G.removeEdge(x, y, true);
                            G.removeEdge(z, y, true);

                            G.addEdge(x, y, false);
                            G.addEdge(z, y, false);
                        }
                    }
                }
            }
        }
        
        System.err.println("the rest "+G.getEdgeCount());
        //D. repeat
        //If A -> B, B and C are adjacent, A and C are not adjacent, and there is no
        //arrowhead at B, then orient B - C as B -> C.
        //If there is a directed path from A to B, and an edge between A and B, then orient
        //A - B as A -> B.
        //until no more edges can be oriented.
        //check this part only.
        
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
        /*boolean changed = true;
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
                                    && !G.isOriented(c, b)) {
                                G.removeEdge(b, c, true);
                                G.addEdge(b, c, false);

                                changed = true;
                            }
                        }
                    } else {
                        if(!G.adjacencyPath(a, b, false).isEmpty()) {
                            G.removeEdge(a, b, true);
                            G.addEdge(a, b, false);

                            changed = true;
                        }
                    }
                }
            }
        }
        //*/
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
    
    static String[] names; 
    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "alarm4", 0);
        
        names = new String[ds.getMetaData().getAttributeCount()];
        for (int i = 0; i < names.length; i++)
            names[i] = ""+(i+1);//*/ds.getMetaData().getAttributeName(i).replace("-", "_");//
        
        int N = ds.getMetaData().getAttributeCount();
        PC pc = new PC();
        pc.initCountTree(ds);
        
        Graph G = pc.getStructure(ds, 0.01);
        G.saveAsDot(new File("oriented.dot"), true, names);
        
        if(N < 37)
            return;
        
        Graph alarm = new Graph(new Matrix(37, 37, new double[]{
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,
        0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,
        1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,}));
        alarm.saveAsDot(new File("alarm.dot"), true, names);
        
        
        Set<Pair<Integer, Integer>> correct= new HashSet<Pair<Integer,Integer>>();
        Set<Pair<Integer, Integer>> missing= new HashSet<Pair<Integer,Integer>>();
        Set<Pair<Integer, Integer>> extra= new HashSet<Pair<Integer,Integer>>();
        Set<Pair<Integer, Integer>> misoriented= new HashSet<Pair<Integer,Integer>>();
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(alarm.isEdge(i, j, false) && G.isEdge(i, j, false)) correct.add(new Pair<Integer, Integer>(i,j));
                else if(alarm.isEdge(i, j, false) && !G.isEdge(i, j, false)) missing.add(new Pair<Integer, Integer>(i,j));
                else if(!alarm.isEdge(i, j, false) && G.isEdge(i, j, false)) extra.add(new Pair<Integer, Integer>(i,j));
                else if(alarm.isEdge(i, j, false) && G.isEdge(j, i, false)) misoriented.add(new Pair<Integer, Integer>(i,j));
            }
        }
        
        System.err.printf(
                "Total: %d\n"+
                "Correct: %d / %d\n"
                + "Missing: %d : %s\n"
                + "Extra: %d : %s\n"
                + "Missoriented: %d : %s\n",
                G.getEdgeCount(), 
                correct.size(), 
                alarm.getEdgeCount(),
                missing.size(), missing, 
                extra.size(), extra,
                misoriented.size(), misoriented);
    }
}

package libai.classifiers.bayes;

import libai.common.dataset.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import libai.classifiers.*;
import libai.classifiers.bayes.Bayes;
import libai.common.*;
import org.w3c.dom.Node;

/**
 * Implementation based on the technical report of Jie-Cheng for Bayes
 * PowerConstruct.
 *
 * @author kronenthaler
 */
public class BayesNetwork extends Bayes {
    public static final double EPSILON = 0.01;
    private Graph structure;
    private Map<String, Double> weights[];
    
    @Override
    public BayesNetwork train(DataSet ds) {
        outputIndex = ds.getOutputIndex();
        totalCount = ds.getItemsCount();
        metadata = ds.getMetaData();
        
        structure = getStructure(ds, EPSILON);
        weights = new HashMap[structure.getVertexCount()];
        
        //for each vertex, calculate the weight given the structure.
        for(int i=0;i<weights.length;i++){
            weights[i] = learnWeights(i, structure, ds);
        }
        
        return this;
    }
    
    private Map<String, Double> learnWeights(int vertex, Graph g, DataSet ds){
        Map<String, Double> result = new HashMap<String, Double>();
        
        //get the parents of vertex.
        List<Integer> parents = new ArrayList<Integer>();
        for(int i=0;i<g.getVertexCount();i++){
            if(i == vertex) continue;
            if(g.isParent(i, vertex)){ //i is parent of vertex
                parents.add(i);
            }
        }
        Collections.sort(parents);
        
        if(parents.size() == 0){
            int N = ds.getItemsCount();
            Map<Attribute, Integer> freq = ds.getFrequencies(0, N, vertex);
            for(Attribute v : freq.keySet()){
                double Pv = freq.get(v) / (double)N;
                result.put(new Pair<Attribute, List<Pair<Integer, Attribute>>>(v, null).toString(), Pv);
            }
            
            return result;
        }
        
        return permutations(ds, parents, 0, new ArrayList<Pair<Integer, Attribute>>(), vertex);
    }
    
    private Map<String, Double> permutations(DataSet ds, List<Integer> parents, int currentIndex, List<Pair<Integer, Attribute>> values, int vertex){
        Map<String, Double> buffer = new HashMap<String, Double>();
        int N = ds.getItemsCount();
        
        if(currentIndex >= parents.size()){
            Map<Attribute, Integer> freq = ds.getFrequencies(0, N, vertex);
            int acumCount = 0;
            for(Attribute value : freq.keySet()){
                values.add(new Pair<Integer, Attribute>(vertex, value));
                int frequency = ds.getFrecuencyOf(values.toArray(new Pair[0]));
                acumCount += frequency;
                values.remove(values.size()-1);
                
                Pair<Attribute, List<Pair<Integer, Attribute>>> key = new Pair<Attribute, List<Pair<Integer, Attribute>>>(value, values);
                buffer.put(key.toString(), (double)frequency);
            }
            
            for(String key: buffer.keySet()){
                buffer.put(key, (buffer.get(key) + 1) / (double)(acumCount + freq.size()));
            }
            
            return buffer;
        }
        
        Map<Attribute, Integer> parentValues = ds.getFrequencies(0, N, parents.get(currentIndex));
        for(Attribute value : parentValues.keySet()){
            Pair<Integer, Attribute> v = new Pair<Integer, Attribute>(parents.get(currentIndex), value);
            List<Pair<Integer, Attribute>> newValues = new ArrayList<Pair<Integer, Attribute>>(values);
            newValues.add(v);
            buffer.putAll(permutations(ds, parents, currentIndex+1, newValues, vertex));
        }
        
        return buffer;
    }
    
    @Override
    protected double P(Attribute c, List<Attribute> x){
        //iterate over all parents of outputIndex, all the way up, until there is no more parents.
        //acumulate the results multiplying them.
        double p = 1;
        x.remove(outputIndex); //remove placeholder attribute
        x.add(outputIndex, c); //insert the current value in use/
        for(int current = 0;current < structure.getVertexCount();current++){
            Attribute z = x.get(current);
            
            List<Pair<Integer, Attribute>> parents = null;
            for(int i=0;i<structure.getVertexCount();i++){
                if(structure.isParent(i, current)){
                    if(parents == null)
                        parents = new ArrayList<Pair<Integer, Attribute>>();
                    parents.add(new Pair<Integer,Attribute>(i,x.get(i)));
                }
            }
            
            Pair<Attribute, List<Pair<Integer, Attribute>>> key = new Pair<Attribute, List<Pair<Integer, Attribute>>>(z, parents);
            Double d = weights[current].get(key.toString());
            p *= d;
        }
        
        return p;
    }
    
    private Graph getStructure(DataSet ds, double eps) {
        String[] names = new String[ds.getMetaData().getAttributeCount()];
        for (int i = 0; i < names.length; i++)
            names[i] = ds.getMetaData().getAttributeName(i).replace("-", "_");

        Graph G = new Graph(ds.getMetaData().getAttributeCount());
        List<Pair<Double, Pair<Integer, Integer>>> L = new ArrayList<Pair<Double, Pair<Integer, Integer>>>();

        //1. Drafting
        int N = G.getVertexCount();
        //calculate the mutual information between every pair of nodes first.
        for (int x = 0; x < N - 1; x++) {
            for (int y = x + 1; y < N; y++) {
                double info = I(ds, x, y);
                if (x != y && info > eps) {
                    L.add(new Pair<Double, Pair<Integer, Integer>>(info, new Pair<Integer, Integer>(x, y)));
                    L.add(new Pair<Double, Pair<Integer, Integer>>(info, new Pair<Integer, Integer>(y, x)));
                }
            }
        }

        Collections.sort(L);
        Collections.reverse(L);

        //take the first 2 edges and add them to E, remove'em from L
        G.addEdge(L.remove(0).second, true);
        G.addEdge(L.remove(0).second, true);

        //for the rest: while number of edges is N-1 or L is empty:
        //if there is no adjacency path between x,y, add to E, remove it anyway
        int head = 0;
        while (!L.isEmpty() && head < L.size() && G.getEdgeCount() / 2 < N - 1) {
            Pair<Integer, Integer> e = L.get(head).second;
            if (G.adjacencyPath(e.first, e.second, true).isEmpty()) {
                G.addEdge(e, true);
                L.remove(head);
            } else
                head++;
        }
        //G.saveAsDot(new File("drafting.dot"), false, names);
        //System.err.println("Thickering: " + G.getEdgeCount() / 2);

        //2. Thickering
        while (!L.isEmpty()) {
            Pair<Integer, Integer> e = L.remove(0).second;
            if (!tryToSeparateA(G, e, ds, eps)) {
                G.addEdge(e, true);
            }
        }
        //G.saveAsDot(new File("thickering.dot"), false, names);
        //System.err.println("Thinning 1/2: " + G.getEdgeCount() / 2);
        //3. Thinning
        for (int i = 0; i < N-1; i++) {
            for (int j = i+1; j < N; j++) {
                if (G.isEdge(i, j, true)) {
                    Pair<Integer, Integer> e = new Pair<Integer, Integer>(i, j);
                    if (G.pathExists(i, j, e)) { //this might be the problem
                        G.removeEdge(i, j, true);
                        if (!tryToSeparateA(G, e, ds, eps))
                            G.addEdge(e, true);
                    }
                }
            }
        }
        //System.err.println("Thinning 2/2: " + G.getEdgeCount() / 2);
        //G.saveAsDot(new File("thinning1.dot"), false, names);
        for (int i = 0; i < N-1; i++) {
            for (int j = i+1; j < N; j++) {
                if (G.isEdge(i, j, true)) {
                    Pair<Integer, Integer> e = new Pair<Integer, Integer>(i, j);
                    if (G.pathExists(i, j, e)) {
                        G.removeEdge(i, j, true);
                        if (!tryToSeparateB(G, e, ds, eps))
                            G.addEdge(e, true);
                    }
                }
            }
        }
        //G.saveAsDot(new File("thinning2.dot"), false, names);//*/
        //System.err.println("orienting: " + G.getEdgeCount() / 2);
        //4. orient edges
        return orientEdges(G, ds, eps); //also weird behaviour here, more edges are created
    }

    private boolean tryToSeparateA(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        //check this again, the intersection are making the |c| = 1 and breaking.
        Set<Integer> path = G.adjacencyPath(X, Y, true);
        Set<Integer> N1 = G.neighbors(X, true);
        N1.retainAll(path);
        Set<Integer> N2 = G.neighbors(Y, true);
        N2.retainAll(path);

        Set<Integer>[] order = new Set[]{
            N1.size() > N2.size() ? N2 : N1,
            N1.size() <= N2.size() ? N1 : N2
        };

        for (Set<Integer> C : order) {
            double v = I(ds, X, Y, C);
            if (v < eps)
                return true;

            while (C.size() > 1) {
                Set<Integer> Cx[] = new Set[C.size()];
                double vx[] = new double[C.size()];
                double min = Double.MAX_VALUE;
                int i = 0, m = -1;
                for (Integer x : C) {
                    Cx[i] = new HashSet<Integer>(C);
                    Cx[i].remove(x);
                    vx[i] = I(ds, X, Y, Cx[i]);
                    if (vx[i] < min) {
                        min = vx[i];
                        m = i;
                    }
                }

                if (vx[m] < eps) {
                    return true;
                } else if (vx[m] > v) {
                    break;
                } else {
                    C = Cx[m];
                }
            }
        }

        return false;
    }

    private boolean tryToSeparateB(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        Set<Integer> path = G.adjacencyPath(X, Y, true);
        Set<Integer> N1 = sequence(X, path, G);
        Set<Integer> N2 = sequence(Y, path, G);
        Set<Integer> C = N1.size() < N2.size() ? N1 : N2;
        Set<Integer> Cs;

        while (true) {
            double v = I(ds, X, Y, C);
            if (v < eps)
                return true;
            else if (C.size() == 1)
                return false;

            Cs = new HashSet<Integer>(C);
            for (Integer x : C) {
                Set<Integer> tmp = new HashSet<Integer>(C);
                tmp.remove(x);
                double vi = I(ds, X, Y, tmp);
                if (vi < eps)
                    return true;
                if (vi <= v + eps)
                    Cs.remove(x);
            }

            if (Cs.size() < C.size())
                C = Cs;
            else
                break;
        }

        return false;
    }

    private Set<Integer> sequence(int X, Set<Integer> adjPath, Graph G) {
        //TODO: double/triple check this function, something seem off...
        Set<Integer> Sx = G.neighbors(X, true);
        Sx.retainAll(adjPath);
        Set<Integer> Sx1 = new HashSet<Integer>();
        for (Integer x : Sx) {
            Set<Integer> aux = G.neighbors(x, true);
            aux.removeAll(Sx);
            Sx1.addAll(aux);
        }
        Sx.addAll(Sx1);

        return Sx;
    }

    public Graph orientEdges(Graph G, DataSet ds, double eps) {
        //1. For any two nodes s1 and s2
        int N = G.getVertexCount();
        for (int s1 = 0; s1 < N; s1++) {
            for (int s2 = 0; s2 < N; s2++) {
                int step = ((s1 * N) + s2);

                if (s1 == s2)
                    continue;

                //that are not directly connected 
                if (G.isEdge(s1, s2, true))
                    continue;

                //and where there is at least one node that is the neighbor of both s1 and s2
                Set<Integer> N1 = G.neighbors(s1, true);
                Set<Integer> N2 = G.neighbors(s2, true);

                N1.retainAll(N2);
                if (N1.size() < 1)
                    continue;

                //find the neighbors of s1 and s2 that are on the adjacency paths between s1 and s2.
                //Put them into two sets N1 and N2 respectively.
                Set<Integer> path = G.adjacencyPath(s1, s2, true);

                //2. Find the neighbors of the nodes in N1 that are on the adjacency paths between s1 and s2, and do not belong to
                //N1. Put them into set N1’.
                N1 = sequence(s1, path, G);

                //3. Find the neighbors of the nodes in N2 that are on the adjacency paths between s1 and s2, and do not belong to
                //N2. Put them into set N2’.
                N2 = sequence(s2, path, G);

                //4. If |N1+N1’| < |N2+N2’| let set C=N1+N1’ else let C=N2+N2’.
                Set<Integer> C = (N1.size() < N2.size()) ? N1 : N2;
                Set<Integer> Cs;

                while (C.size() > 0) {
                    //5. Conduct a CI test using Equation (2.2). Let v = I(s1,s2|C). If v < ε , go to step 8; 
                    double v = I(ds, s1, s2, C);
                    if (v < eps)
                        break; //go to 8

                    //otherwise, if |C|=1, let s1 and s2 be parents of the node in C, go to step 8.
                    if (C.size() == 1) {
                        for (Integer x : C) {
                            G.removeEdge(s1, x, true);
                            G.removeEdge(s2, x, true);

                            G.addEdge(s1, x, false); //give direction s1->x
                            G.addEdge(s2, x, false); //give direction s2->x
                        }
                        break; //go to 8
                    }

                    //6. Let C’=C. For each i ∈ [ 1 , C ] , let C i =C \ {the i th node of C}, v i = I(s1,s2| C i ). If v i ≤ v+e then C’=C’\{the
                    //i th node of C}, let s1 and s2 be parents of the i node of C if the i node is a neighbor of both s1 and s2. If v i <
                    //ε , go to step 8. (e is a small value.)
                    Cs = new HashSet<Integer>(C);
                    boolean skipTo8 = false;
                    for (Integer x : C) {
                        Set<Integer> tmp = new HashSet<Integer>(C);
                        tmp.remove(x);
                        double vi = I(ds, s1, s2, tmp);
                        if (vi <= v + eps) {
                            Cs = tmp;
                            if (G.isEdge(s1, x, true) && G.isEdge(s2, x, true)) {
                                G.removeEdge(s1, x, true);
                                G.removeEdge(s2, x, true);

                                G.addEdge(s1, x, false); //give direction s1->x
                                G.addEdge(s2, x, false); //give direction s2->x
                            }
                        } else if (vi < eps) {
                            skipTo8 = true;
                            break;
                        }
                    }
                    if (skipTo8)
                        break;

                    //7. If |C’|<|C| then let C=C’, if |C|>0, go to step 5.
                    if (Cs.size() < C.size())
                        C = Cs;
                }
                //8. Go back to step1 and repeat until all pairs of nodes are examined.
            }
        }

        boolean canOrientAEdge = true;
        while (canOrientAEdge) {
            canOrientAEdge = false;
            //9. For any three nodes a, b, c, if , b and c are adjacent, and a and c are not adjacent and edge (b, c)
            //is not oriented, let b be a parent of c.
            for (int a = 0; a < N; a++) {
                for (int b = 0; b < N; b++) {
                    if (a == b)
                        continue;
                    for (int c = 0; c < N; c++) {
                        if (a == c || c == b)
                            continue;
                        if (G.isParent(a, b) && //a is a parent of b
                                G.isEdge(b, c, true) && //b and c are adjacent
                                !G.isEdge(a, c, true) && //a and c are not adjacent
                                !G.isOriented(b, c)) { //edge (b, c) is not oriented
                            G.removeEdge(b, c, true);
                            G.addEdge(b, c, false);
                            canOrientAEdge = true;
                        }
                    }
                }
            }

            //10. For any edge (a, b) that is not oriented, if there is a directed path from a to b, let a be a parent of b.
            for (int a = 0; a < N; a++) {
                for (int b = 0; b < N; b++) {
                    if (a == b || !G.isEdge(a, b, true))
                        continue;
                    if (!G.isOriented(a, b) && G.adjacencyPath(a, b, false).size() > 0) {
                        G.removeEdge(a, b, true);
                        G.addEdge(a, b, false);
                        canOrientAEdge = true;
                    }
                }
            }
        }
        //11. Go back to step 9, and repeat until no more edges can be oriented.

        return G;
    }

    private double I(DataSet ds, int X, int Y, Set<Integer> Z) {
        double info = 0;
        int N = ds.getItemsCount();

        int xyz[] = new int[Z.size() + 2];
        int j = 0;
        for (int i : Z) {
            xyz[j++] = i;
        }
        xyz[j++] = X;
        xyz[j++] = Y;

        for (List<Attribute> record : ds.getCombinedValuesOf(xyz)) {
            Attribute valuex = record.get(X);
            Attribute valuey = record.get(Y);
            
            Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
            Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
            ArrayList<Pair<Integer, Attribute>> z = new ArrayList<Pair<Integer, Attribute>>();
            for (int i = 0; i < xyz.length - 2; i++) {
                Attribute valuez = record.get(xyz[i]);
                Pair<Integer, Attribute> vz = new Pair<Integer, Attribute>(xyz[i], valuez);
                z.add(vz);
            }
            double Pz = (ds.getFrecuencyOf(z.toArray(new Pair[0])) / (double) N);

            z.add(x);
            double Pxz = (ds.getFrecuencyOf(z.toArray(new Pair[0])) / (double) N);
            z.remove(z.size() - 1);

            z.add(y);
            double Pyz = (ds.getFrecuencyOf(z.toArray(new Pair[0])) / (double) N);

            z.add(x);
            double Pxyz = (ds.getFrecuencyOf(z.toArray(new Pair[0])) / (double) N);

            info += (Pxyz) * Math.log((Pxyz * Pz) / (Pxz * Pyz));
        }
        return info;
    }
   
    private double I(DataSet ds, int X, int Y) {
        double info = 0;
        int N = ds.getItemsCount();
        Map<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        Map<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        for (List<Attribute> record : ds.getCombinedValuesOf(X, Y)) {
            Attribute valuex = record.get(X);
            Attribute valuey = record.get(Y);
            Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
            Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
            double Px = freqX.get(valuex) / (double) N;
            double Py = freqY.get(valuey) / (double) N;
            double Pxy = ((ds.getFrecuencyOf(x, y) / (double) N));
            info += Pxy * Math.log(Pxy / (Px * Py));
        }
        return info;
    }

    @Override
    public boolean save(File path) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Bayes load(Node root) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "asia", 0);
        BayesNetwork bn = new BayesNetwork().train(ds);
    }
}
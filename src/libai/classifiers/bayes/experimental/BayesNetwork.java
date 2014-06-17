package libai.classifiers.bayes.experimental;

import libai.common.dataset.MySQLDataSet;
import libai.common.dataset.DataSet;
import java.io.*;
import java.sql.*;
import java.util.*;
import libai.classifiers.*;
import libai.classifiers.bayes.Bayes;
import libai.common.*;

/**
 * Implementation based on the technical report of Jie-Cheng for Bayes
 * PowerConstruct.
 *
 * @author kronenthaler
 */
public class BayesNetwork extends Bayes {
    public static final double EPSILON = 0.01;
    private Graph structure;
    //private ? weight, parameters depend on the number of values that the attribute can have

    public Graph train(DataSet ds, double eps) {
        //this function should first recover the structure and then 
        //calculate the table of parameters.
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
        G.saveAsDot(new File("drafting.dot"), false, names);
        System.err.println("Thickering: " + G.getEdgeCount() / 2);

        //2. Thickering
        while (!L.isEmpty()) {
            Pair<Integer, Integer> e = L.remove(0).second;
            if (!tryToSeparateA(G, e, ds, eps)) {
                G.addEdge(e, true);
            }
        }
        G.saveAsDot(new File("thickering.dot"), false, names);
        System.err.println("Thinning 1/2: " + G.getEdgeCount() / 2);
        //3. Thinning
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
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
        System.err.println("Thinning 2/2: " + G.getEdgeCount() / 2);
        G.saveAsDot(new File("thinning1.dot"), false, names);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
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
        G.saveAsDot(new File("thinning2.dot"), false, names);
        System.err.println("orienting: " + G.getEdgeCount() / 2);
        //4. orient edges
        return orientEdges(G, ds, eps); //also weird behaviour here, more edges are created
    }

    private boolean tryToSeparateA(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
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

    //this function is the problem for sure!
    //P(x,y|C0,C1,C2...) <- YES this has to be done!!!
    // P(C) = P(c0, c1, ... cn) ?
    // So, the expressions need to be rewritten to reflect the combination of multiple elements in C.
    private double I(DataSet ds, int X, int Y, Set<Integer> C) {
        double info = 0;
        int N = ds.getItemsCount();

        int xyz[] = new int[C.size() + 2];
        int j = 0;
        for (int i : C) {
            xyz[j++] = i;
        }
        xyz[j++] = X;
        xyz[j++] = Y;

        for (List<Attribute> record : ds.getCombinedValuesOf(xyz)) {
            Attribute valuex = record.get(X);
            Attribute valuey = record.get(Y);
            //Attribute valuec = record.get(ci);
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
            //check: http://en.wikipedia.org/wiki/Conditional_mutual_information 
        }
        return info;
    }

    private double I(DataSet ds, int X, int Y) {
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
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

    private double IH(DataSet ds, int X, int Y, Set<Integer> C) {
        System.err.printf("I(%d,%d,%s)\n", X, Y, C.toString());
        double Hx = 0, Hy = 0, Hz = 0, Hxy = 0, Hzx = 0, Hzy = 0, Hzxy = 0;

        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        HashSet<Triplet<Attribute, Attribute, Long>> visitedY = new HashSet<Triplet<Attribute, Attribute, Long>>();
        HashSet<Triplet<Attribute, Attribute, Long>> visitedX = new HashSet<Triplet<Attribute, Attribute, Long>>();
        for (Integer Z : C) {
            HashMap<Attribute, Integer> freqZ = ds.getFrequencies(0, N, Z);

            for (Attribute z : freqZ.keySet()) {
                Pair<Integer, Attribute> vz = new Pair<Integer, Attribute>(Z, z);
                double Pz = freqZ.get(z) / (double) N;
                Hz += -Pz * Math.log(Pz);

                for (Attribute x : freqX.keySet()) {
                    Pair<Integer, Attribute> vx = new Pair<Integer, Attribute>(X, x);
                    if (!visitedX.contains(new Triplet<Attribute, Attribute, Long>(z, x, 0l))) {
                        visitedX.add(new Triplet<Attribute, Attribute, Long>(z, x, 0l));
                        //Hzx 
                        double Pzx = ds.getFrecuencyOf(vx, vz) / (double) N;
                        if (Pzx > EPSILON)
                            Hzx += -Pzx * Math.log(Pzx);
                    }
                    for (Attribute y : freqY.keySet()) {
                        Pair<Integer, Attribute> vy = new Pair<Integer, Attribute>(Y, y);
                        //double Py = freqY.get(y) / (double)N;
                        if (!visitedY.contains(new Triplet<Attribute, Attribute, Long>(z, y, 0l))) {
                            visitedY.add(new Triplet<Attribute, Attribute, Long>(z, y, 0l));

                            //Hzy 
                            double Pzy = ds.getFrecuencyOf(vy, vz) / (double) N;
                            if (Pzy > EPSILON)
                                Hzy += -Pzy * Math.log(Pzy);
                        }

                        //Hzyx
                        double Pzxy = ds.getFrecuencyOf(vx, vy, vz) / (double) N;
                        if (Pzxy > EPSILON)
                            Hzxy += -Pzxy * Math.log(Pzxy);
                    }
                }
            }
        }
        /*System.err.println(visitedY);
         System.err.printf("H(%d,%d)=%f\n",X,1, Hzx);
         System.err.printf("H(%d,%d)=%f\n",Y,1, Hzy);
         System.err.printf("H(%d,%d,%d)=%f\n",X,Y,1, Hzxy);
         System.err.printf("H(1)=%f\n", Hz);*/
        return Hzx + Hzy - Hzxy - Hz;
    }

    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "asia", 0);

        /*BayesNetwork bn =new BayesNetwork();
         Set<Integer> C = new HashSet<Integer>();
         C.add(1);// |C| = 1
         for(int i=0,n=ds.getMetaData().getAttributeCount();i<n;i++){
         for(int j=0;j<n;j++){
         if(i!=j){
         System.err.printf("I(%d,%d,%s) = %f ? %f\n", i,j,C.toString(), bn.I(ds, i, j, C), bn.IH(ds, i, j, C));
         }
         }
         }
        
         if(true) return;//*/
        Graph control = new BayesNetwork().train(ds, EPSILON);
        System.err.println(control);
        String[] names = new String[ds.getMetaData().getAttributeCount()];
        for (int i = 0; i < names.length; i++)
            names[i] = ds.getMetaData().getAttributeName(i).replace("-", "_");
        control.saveAsDot(new java.io.File("oriented.dot"), true, names);//*/
    }
}

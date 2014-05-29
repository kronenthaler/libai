package libai.classifiers.bayes;

import libai.common.dataset.MySQLDataSet;
import libai.common.dataset.DataSet;
import java.sql.*;
import java.util.*;
import libai.classifiers.*;
import libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class BayesNetwork extends Bayes {
    public static double EPSILON = 0.01;
    Set<Integer> cutSet = new HashSet<Integer>();
    HashMap<Pair<Integer, Integer>, Double> cacheInformation = new HashMap<Pair<Integer, Integer>, Double>();

    public Graph train(DataSet ds, double eps) {
        //this function should first recover the structure and then 
        //calculate the table of parameters.
        
        Graph G = new Graph(ds.getMetaData().getAttributeCount());
        Set<Pair<Integer, Integer>> L = new HashSet<Pair<Integer, Integer>>();

        //1. build a list of pairs where there is information flow between them
        for (int x = 0, n = G.getVertexCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (x != y && I(ds, x, y) > eps) {
                    L.add(new Pair<Integer, Integer>(x, y));
                    G.addEdge(x, y);
                }
            }
        }

        System.err.println("step 1/3");
        System.err.println(G);
        //2. thickering
        for (Pair<Integer, Integer> e : L) {
            if (edgeNeeded(G, e, ds, eps)) {
                G.addEdge(e);
            }
        }

        System.err.println("step 2/3");
        System.err.println(G);
        
        //3: Thinning
        for (int i = 0, n = G.getVertexCount(); i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (G.isEdge(i, j, true)) {
                    Pair<Integer, Integer> e = new Pair<Integer, Integer>(i, j);
                    if (G.pathExists(i, j, e)) {
                        G.removeEdge(i, j);
                        if (edgeNeeded(G, e, ds, eps))
                            G.addEdge(i, j);
                    }
                }
            }
        }

        System.err.println("step 3/3");
        System.err.println(G);
        //4: orient edges
        return orientEdges(G, ds);
    }

    public boolean edgeNeeded(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        Set<Integer> path = G.adjacencyPath(X, Y, true);
        Set<Integer> Sx = sequence(X, path, G);
        Set<Integer> Sy = sequence(Y, path, G);

        Set<Integer> C = Sx.size() < Sy.size() ? Sx : Sy;
        double s = I(ds, X, Y, C);
        if (s < eps) {
            cutSet.add(X);
            cutSet.add(Y);
            cutSet.addAll(C);
            return false;
        }

        while (C.size() > 1) {
            double S[] = new double[C.size()];
            int i = 0, m = 0;
            double min = Double.MAX_VALUE;
            Set<Integer>[] Cs = new Set[C.size()];
            for (Integer ci : C) {
                Cs[i] = new HashSet<Integer>(C);
                Cs[i].remove(ci);
                S[i] = I(ds, X, Y, Cs[i]);
                if (S[i] < min) {
                    min = S[i];
                    m = i;
                }
                i++;
            }

            if (S[m] < eps) {
                cutSet.add(X);
                cutSet.add(Y);
                cutSet.addAll(Cs[m]);
                return false;
            } else if (S[m] > s) {
                break;
            } else {
                s = S[m];
                C = Cs[m];
            }
        }

        return true;
    }

    public Set<Integer> sequence(int X, Set<Integer> adjPath, Graph G) {
        Set<Integer> Sx = G.neighbors(X, true);
        Sx.retainAll(adjPath);
        Set<Integer> Sx1 = new HashSet<Integer>();
        for (Integer x : Sx) {
            Set<Integer> aux = G.neighbors(x, true);
            aux.removeAll(Sx);
            Sx1.addAll(aux);
        }
        Sx1.retainAll(adjPath);
        Sx.addAll(Sx1);

        return Sx;
    }

    public Graph orientEdges(Graph G, DataSet ds) {
        Set<Pair<Integer, Integer>> oriented = new HashSet<Pair<Integer, Integer>>();
        
        for (int x = 0, n = G.getVertexCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < n; z++) {
                    if (G.isEdge(x, y, true) 
                            && G.isEdge(y, z, true) 
                            && !G.isEdge(x, z, true)) {
                        if ((cutSet.contains(x) && cutSet.contains(z) && !cutSet.contains(y)) 
                                || (!cutSet.contains(x) && !cutSet.contains(z))) {
                            G.removeEdge(x, y, true);
                            G.addEdge(x, y);

                            G.removeEdge(z, y, true);
                            G.addEdge(z, y);

                            oriented.add(new Pair<Integer, Integer>(x, y));
                            oriented.add(new Pair<Integer, Integer>(z, y));
                        }
                    }
                }
            }
        }
        for (int x = 0, n = G.getVertexCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < n; z++) {
                    if (G.isEdge(x, y, false)
                            && G.isEdge(y, z, true)
                            && !G.isEdge(x, z, true)
                            && !oriented.contains(new Pair<Integer, Integer>(y, z))) {
                        G.removeEdge(y, z, true);
                        G.addEdge(y, z);
                        oriented.add(new Pair<Integer, Integer>(y, z));
                    }
                }
            }
        }

        for (int x = 0, n = G.getVertexCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (G.isEdge(x, y, true) && !oriented.contains(new Pair<Integer, Integer>(x, y))) {
                    if (G.adjacencyPath(x, y, false).size() > 0) {
                        G.removeEdge(x, y, true);
                        G.addEdge(x, y);
                        oriented.add(new Pair<Integer, Integer>(x, y));
                    }
                }
            }
        }

        return G;
    }

    public double I(DataSet ds, int X, int Y, Set<Integer> C) {
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        for (Integer ci : C) {
            HashMap<Attribute, Integer> freqC = ds.getFrequencies(0, N, ci);
            for (List<Attribute> record : ds.getCombinedValuesOf(X, Y, ci)) {
                Attribute valuex = record.get(X);
                Attribute valuey = record.get(Y);
                Attribute valuec = record.get(ci);
                Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
                Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
                Pair<Integer, Attribute> z = new Pair<Integer, Attribute>(ci, valuec);
                double Pc = freqC.get(valuec) / (double) N;
                double Pabc = (ds.getFrecuencyOf(x, y, z) / (double) N);
                double Pac = (ds.getFrecuencyOf(x, z) / (double) N);
                double Pbc = (ds.getFrecuencyOf(y, z) / (double) N);
                info += Pabc * Math.log(Pabc / (Pac * Pbc));
            }
        }

        return info;
    }

    public double I(DataSet ds, int X, int Y) {
        Pair<Integer, Integer> edge = new Pair<Integer, Integer>(X, Y);
        if (cacheInformation.get(edge) != null)
            return cacheInformation.get(edge);

        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        //give me all the records with different values of x, y
        for (List<Attribute> record : ds.getCombinedValuesOf(X, Y)) {
            Attribute valuex = record.get(X);
            Attribute valuey = record.get(Y);
            Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
            Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
            double Px = freqX.get(valuex) / (double) N;
            double Pxy = (ds.getFrecuencyOf(x, y) / (double) N);
            double Py = freqY.get(valuey) / (double) N;
            info += Pxy * Math.log(Pxy / (Px * Py));
        }

        cacheInformation.put(edge, info);
        cacheInformation.put(new Pair<Integer, Integer>(Y, X), info);

        return info;
    }

    //TODO: try it using one of the know databases like alarm, the structure should be recovered somehow correctly.
    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "reorder_iris", 0);

        BayesNetwork bn = new BayesNetwork();
        System.err.println(bn.train(ds, EPSILON));
    }
}

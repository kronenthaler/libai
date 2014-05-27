package libai.classifiers.bayes;

import java.sql.*;
import java.util.*;
import libai.classifiers.*;
import libai.classifiers.dataset.*;
import libai.common.*;

/**
 *
 * @author kronenthaler
 */
public class BayesNetwork {
    public static double EPSILON = 0.01;
    Set<Integer> cutSet = new HashSet<Integer>();
    HashMap<Pair<Integer,Integer>, Double> cacheInformation = new HashMap<Pair<Integer,Integer>, Double>();
    
    public List<Pair<Integer, Integer>> train(DataSet ds, double eps) {
        List<Pair<Integer, Integer>> E = new Vector<Pair<Integer, Integer>>(); //the list should be a set to avoid multiarcs

        //1. build a list of pairs where there is information flow between them
        for (int x = 0, n = ds.getMetaData().getAttributeCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (x != y && I(ds, x, y) > eps) {
                    E.add(new Pair<Integer, Integer>(x, y));
                }
            }
        }
        System.err.println("step 1/3");
        //2. thickering [ToDo: is this correct? E should be pass below?]
        for (Pair<Integer, Integer> e : E) { //careful the edges may fuck with the iterator.
            if (edgeNeeded(E, e, ds, eps)) {
                E.add(e);
            }
        }
        System.err.println("step 2/3");
        //3: Thinning
        for (Pair<Integer, Integer> e : E) {
            if (isPath(e.first, e.second, e)) {
                List<Pair<Integer, Integer>> E1 = new Vector<Pair<Integer, Integer>>(E);
                E1.remove(e);
                if (!edgeNeeded(E1, e, ds, eps))
                    E = E1;
            }
        }
        System.err.println("step 3/3");
        //4: orient edges
        return orientEdges(E, ds);
    }

    public boolean edgeNeeded(List<Pair<Integer, Integer>> E, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        Set<Integer> path = adjPath(X, Y, E);
        Set<Integer> Sx = sequence(X, path, E);
        Set<Integer> Sy = sequence(Y, path, E);
        
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
    
    public Set<Integer> sequence(int X, Set<Integer> adjPath, List<Pair<Integer,Integer>> E){
        Set<Integer> Sx = neighbors(X, E);
        Sx.retainAll(adjPath);
        Set<Integer> Sx1 = new HashSet<Integer>();
        for (Integer x : Sx) {
            Set<Integer> aux = neighbors(x, E);
            aux.removeAll(Sx);
            Sx1.addAll(aux);
        }
        Sx1.retainAll(adjPath);
        Sx.addAll(Sx1);
        
        return Sx;
    }

    private Set<Integer> adjPath(int X, int Y, List<Pair<Integer, Integer>> E) {
        class node {
            int x;
            node prev;

            node(int _x) {
                x = _x;
            }

            node(int _x, node _p) {
                x = _x;
                prev = _p;
            }
        }

        Set<Integer> path = new HashSet<Integer>();
        HashMap<Pair<Integer, Integer>, Boolean> visited = new HashMap<Pair<Integer, Integer>, Boolean>();
        List<node> queue = new ArrayList<node>();
        queue.add(new node(X));

        while (!queue.isEmpty()) {
            node v = queue.remove((int) 0);
            if (v.x == Y) {
                while (v != null) {
                    path.add(v.x);
                    v = v.prev;
                }
                path.add(X);
                path.add(Y);
                return path;
            }

            for (Pair<Integer, Integer> e : E) {
                if (visited.get(e) == null && (e.first == v.x || e.second == v.x)){
                    visited.put(e, Boolean.TRUE);
                    queue.add(new node(e.first == v.x ? e.second : e.first, v));
                }
            }
        }
        return path;
    }

    public boolean isPath(int x, int y, Pair<Integer, Integer> e) {
        return false;
    }

    public List<Pair<Integer, Integer>> orientEdges(List<Pair<Integer, Integer>> E, DataSet ds) {
        return E;
    }

    private Set<Integer> neighbors(int X, List<Pair<Integer, Integer>> E) {
        Set<Integer> ngbrs = new HashSet<Integer>();
        for (Pair<Integer, Integer> e : E) {
            if (e.first == X)
                ngbrs.add(e.second);
            if (e.second == X)
                ngbrs.add(e.first);
        }
        return ngbrs;
    }

    public double I(DataSet ds, int X, int Y, Set<Integer> C) {
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        for (Integer ci : C) {
            HashMap<Attribute, Integer> freqC = ds.getFrequencies(0, N, ci);
            for(List<Attribute> record : ds.getCombinedValuesOf(X, Y, ci)){
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
        Pair<Integer,Integer> edge = new Pair<Integer,Integer>(X,Y);
        if(cacheInformation.get(edge)!=null)
            return cacheInformation.get(edge);
        
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        
        //give me all the records with different values of x, y
        for(List<Attribute> record : ds.getCombinedValuesOf(X, Y)){
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
        cacheInformation.put(new Pair<Integer,Integer>(Y,X), info);
        
        return info;
    }

    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "iris", 0);

        BayesNetwork bn = new BayesNetwork();
        System.err.println("Graph: " + bn.train(ds, EPSILON));
        /*
        for (int x = 0, n = ds.getMetaData().getAttributeCount(); x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (x != y) {
                    System.err.println(bn.I(ds, x, y));
                }
            }
        }*/
    }
}

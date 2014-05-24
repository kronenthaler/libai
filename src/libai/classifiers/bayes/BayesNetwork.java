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
    
    public ArrayList<Pair<Integer,Integer>> train(DataSet ds, double eps){
        ArrayList<Pair<Integer,Integer>> L = new ArrayList<Pair<Integer,Integer>>();
        ArrayList<Pair<Integer,Integer>> E = new ArrayList<Pair<Integer,Integer>>();
        
        //1. build a list of pairs where there is information flow between them
        for(int x=0,n=ds.getMetaData().getAttributeCount();x<n;x++){
            for(int y=0;y<n;y++){
                if(x!=y && I(ds, x, y) > eps){
                    L.add(new Pair<Integer,Integer>(x,y));
                }
            }
        }
        
        //2. thickering [ToDo: is this correct? E should be pass below?]
        for(Pair<Integer, Integer> e : L){
            if(edgeNeeded(E, e, ds, eps)){
                E.add(e);
            }
        }
        
        //3: Thinning
        for (Pair<Integer, Integer> e : E) {
            if (isPath(e.first, e.second, e)) {
                ArrayList<Pair<Integer, Integer>> E1 = new ArrayList<Pair<Integer, Integer>>(E);
                E1.remove(e);
                if (!edgeNeeded(E1, e, ds, eps))
                    E = E1;
            }
        }

        //4: orient edges
        return orientEdges(E, ds);
    }
    
    public boolean edgeNeeded(ArrayList<Pair<Integer,Integer>> E, Pair<Integer,Integer> e, DataSet ds, double eps){
        int X = e.first;
        int Y = e.second;
        Set<Integer> path = adjPath(X, Y, E); //check adjPath accoding to the paper (using E for it sounds weird)
        Set<Integer> Sx = neighbors(X, E); 
        Sx.retainAll(path);
        Set<Integer> Sx1 = new HashSet<Integer>();
        for(Integer x : Sx){
            Set<Integer> aux = neighbors(x, E);
            aux.removeAll(Sx);
            Sx1.addAll(aux);
        }
        Sx1.retainAll(path);
        Sx.addAll(Sx1);
        
        Set<Integer> Sy = neighbors(Y, E); 
        Sy.retainAll(path);
        Set<Integer> Sy1 = new HashSet<Integer>();
        for(Integer x : Sy){
            Set<Integer> aux = neighbors(x, E);
            aux.removeAll(Sy);
            Sy1.addAll(aux);
        }
        Sy1.retainAll(path);
        Sy.addAll(Sy1);
        
        Set<Integer> C = Sx.size() < Sy.size() ? Sx : Sy;
        double s = I(ds, X, Y, C);
        if(s < eps){
            cutSet.add(X);
            cutSet.add(Y);
            cutSet.addAll(C);
            return false;
        }
        
        while(C.size() > 1){
            double S[] = new double[C.size()];
            int i=0, m=0;
            double min = Double.MAX_VALUE;
            Set<Integer>[] Cs = new Set[C.size()];
            for(Integer ci : C){
                Cs[i] = new HashSet<Integer>(C);
                Cs[i].remove(ci);
                S[i] = I(ds, X, Y, Cs[i]);
                if(S[i] < min){
                    min = S[i];
                    m = i;
                }
                i++;
            }
            
            if(S[m] < eps){
                cutSet.add(X);
                cutSet.add(Y);
                cutSet.addAll(C);
                return false;
            }else if(S[m] > s){
                break;
            }else{
                s = S[m];
                C = Cs[m];
            }
        }
        
        return true;
    }
    
    public Set<Integer> adjPath(int X, int Y, ArrayList<Pair<Integer, Integer>> E){
        class node{
            int x;
            node prev;
            node(int _x){x=_x;}
            node(int _x, node _p){x=_x;prev=_p;}
        }
        
        Set<Integer> path = new HashSet<Integer>();
        HashMap<Pair<Integer, Integer>, Boolean> visited = new HashMap<Pair<Integer, Integer>, Boolean>();
        List<node> queue = new ArrayList<node>();
        queue.add(new node(X));
        
        while(!queue.isEmpty()){
            node v = queue.remove((int)0);
            if(v.x == Y){
                while(v != null){
                    path.add(v.x);
                    v = v.prev;
                }
                path.add(X);
                path.add(Y);
                return path;
            }
            
            for(Pair<Integer, Integer> e : E){
                if(visited.get(e)!=null) continue;
                
                visited.put(e, Boolean.TRUE);
                int next = -1;
                if(e.first == v.x) next = e.second;
                if(e.second == v.x) next = e.first;
                if(next > -1)
                    queue.add(new node(next, v));
            }
        }
        return path;
    }
    
    public boolean isPath(int x, int y, Pair<Integer, Integer> e){
        return false;
    }
    
    public ArrayList<Pair<Integer,Integer>> orientEdges(ArrayList<Pair<Integer,Integer>> E, DataSet ds){
        return E;
    }
    
    public Set<Integer> neighbors(int X, ArrayList<Pair<Integer, Integer>> E){
        Set<Integer> ngbrs = new HashSet<Integer>();
        for(Pair<Integer, Integer> e : E){
            if(e.first == X)
                ngbrs.add(e.second);
            if(e.second == X)
                ngbrs.add(e.first);
        }
        return ngbrs;
    }
    
    public double I(DataSet ds, int X, int Y, Set<Integer> C){
        //P(a,b|c) log(P(a,b|c)/P(a|c)P(b|c)) 
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        for(Integer c : C){
            for(Attribute valuex : freqX.keySet()){
                for(Attribute valuey : freqY.keySet()){
                    double Pabc = 0;
                    double Pac = 0;
                    double Pbc = 0;
                    if(Math.abs(Pabc) > EPSILON)
                       info += Pabc * Math.log(Pabc / (Pac * Pbc)); 
                }
            }
        }
        return info;
    }
    
    public double I(DataSet ds, int X, int Y){
        //can be cached, I(X,Y) = I(Y,X) can be recycled
        double info = 0;
        int N = ds.getItemsCount();
        HashMap<Attribute, Integer> freqX = ds.getFrequencies(0, N, X);
        HashMap<Attribute, Integer> freqY = ds.getFrequencies(0, N, Y);
        
        for(Attribute valuex : freqX.keySet()){
            for(Attribute valuey : freqY.keySet()){
                Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
                Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
                double Pxy = ds.getFrecuencyOf(x, y) / (double) N;
                double Px = freqX.get(valuex) / (double) N;
                double Py = freqY.get(valuey) / (double) N;
                if(Math.abs(Pxy) > EPSILON)
                    info += Pxy * Math.log(Pxy / (Px*Py));
            }
        }
        
        return info;
    }
    
    public static void main(String arg[]) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "iris", 4);
        
        BayesNetwork bn = new BayesNetwork();
        System.err.println("Graph: "+bn.train(ds, EPSILON));
    }
}
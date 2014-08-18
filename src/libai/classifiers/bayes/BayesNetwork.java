package libai.classifiers.bayes;

import libai.common.dataset.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import libai.classifiers.*;
import libai.classifiers.bayes.BayesSystem;
import libai.common.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TODO: 
 * - make it work with the same results as in the paper
 * - double check the paper for hidden details in the first part of the algorithm
 * - implement the variant that receives the ordering as parameter to check correctness.
 * 
 * Implementation based on the technical report of Jie-Cheng for BayesSystem
 PowerConstruct.
 * 
 * @author kronenthaler
 */
public class BayesNetwork extends BayesSystem {
    public static final double EPSILON = 0.01;
    private Set<Integer> childs[];
    private Map<Pair<Integer, Integer>, Set<Integer>> CutSet = new HashMap<Pair<Integer, Integer>, Set<Integer>>();
    
    @Override
    protected Graph getStructure(DataSet ds, double eps) {
        String[] names = new String[ds.getMetaData().getAttributeCount()];
        for (int i = 0; i < names.length; i++)
            names[i] = ""+(i+1);//*/ds.getMetaData().getAttributeName(i).replace("-", "_");//

        Graph G = new Graph(ds.getMetaData().getAttributeCount());
        int N = G.getVertexCount();
        
        //1. Drafting
        List<Pair<Double, Pair<Integer, Integer>>> L = new ArrayList<Pair<Double, Pair<Integer, Integer>>>();
        //calculate the mutual information between every pair of nodes first.
        for (int x = 0; x < N; x++) {
            for (int y = x + 1; y < N; y++) {
                double info = I(ds, x, y);
                if (x != y && info >= eps) {
                    L.add(new Pair<Double, Pair<Integer, Integer>>(info, new Pair<Integer, Integer>(x, y)));
                    L.add(new Pair<Double, Pair<Integer, Integer>>(info, new Pair<Integer, Integer>(y, x)));
                }
            }
        }

        Collections.sort(L);
        Collections.reverse(L);

        //take the first 2 edges and add them to E, remove'em from L
        //for the rest: while number of edges is N-1 or L is empty:
        //if there is no adjacency path between x,y, add to E, remove it
        int head = 0;
        while (!L.isEmpty() && head < L.size() && G.getEdgeCount() / 2 < N - 1) {
            Pair<Integer, Integer> e = L.get(head).second;
            if (G.adjacencyPath(e.first, e.second, true).isEmpty()) {
                G.addEdge(e, true);
                L.remove(head);
            } else
                head++;
        }
        G.saveAsDot(new File("1drafting.dot"), false, names);
        System.err.println("Drafting: " + G.getEdgeCount() / 2);
        
        //2. Thickening
        while (!L.isEmpty()) {
            System.err.println(L.size());
            Pair<Integer, Integer> e = L.remove(0).second;
            if (!edgeNeededHeuristic(G, e, ds, eps)) {
                G.addEdge(e, true);
            }
        }
        System.err.println("Thickening: " + G.getEdgeCount() / 2);
        G.saveAsDot(new File("2thickening.dot"), false, names);
        
        //3. Thinning
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i == j) continue;
                if (G.isEdge(i, j, true)) {
                    Pair<Integer, Integer> e = new Pair<Integer, Integer>(i, j);
                    if (G.pathExists(i, j, e)) {
                        G.removeEdge(i, j, true);
                        if (!edgeNeededHeuristic(G, e, ds, eps))
                            G.addEdge(e, true);
                    }
                }
            }
        }
        System.err.println("Thinning 1/2: " + G.getEdgeCount() / 2);
        G.saveAsDot(new File("3thinning1.dot"), false, names);
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i==j) continue;
                if (G.isEdge(i, j, true)) {
                    Pair<Integer, Integer> e = new Pair<Integer, Integer>(i, j);
                    if (G.pathExists(i, j, e)) {
                        G.removeEdge(i, j, true);
                        if (!edgeNeeded(G, e, ds, eps))
                            G.addEdge(e, true);
                    }
                }
            }
        }
        System.err.println("Thinning 2/2: " + G.getEdgeCount() / 2);
        G.saveAsDot(new File("4thinning2.dot"), false, names);        
        //4. orient edges
        G = orientEdges(G, ds, eps); //also weird behaviour here, more edges are created
        System.err.println("orienting: " + G.getEdgeCount());
        G.saveAsDot(new File("5oriented.dot"), false, names);
        return G;
    }

    private boolean edgeNeededHeuristic(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        //check this again, the intersection are making the |c| = 1 and breaking.
        //Set<Integer> path = G.adjacencyPath(X, Y, true); //get just the neighbors that can reach target!
        Set<Integer> N1 = neighborsToPath(X, Y, G);//G.neighbors(X, true);
        Set<Integer> N2 = neighborsToPath(Y, X, G);//G.neighbors(Y, true);
        
        Set<Integer>[] order = new Set[]{
            N1.size() > N2.size() ? N2 : N1,
            N1.size() <= N2.size() ? N1 : N2
        };
        
        for (Set<Integer> C : order) {
            double v = I(ds, X, Y, C);
            if (v < eps){
                return true;
            }

            while (C.size() > 1) { //condition set
                System.err.println("|C|: "+C.size());
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
                    v = vx[m];// from the paper.
                }
            }
        }

        return false;
    }

    private boolean edgeNeeded(Graph G, Pair<Integer, Integer> e, DataSet ds, double eps) {
        int X = e.first;
        int Y = e.second;
        //Set<Integer> path = G.adjacencyPath(X, Y, true);
        Set<Integer> N1 = neighborsOfNeighborsToPath(X,Y,G);//sequence(X, path, G);
        Set<Integer> N2 = neighborsOfNeighborsToPath(Y,X,G);//sequence(Y, path, G);
        Set<Integer> C = N1.size() < N2.size() ? N1 : N2;
        
        double s = I(ds, X, Y, C);
        if (s < eps){
            return true;
        }
        
        while (C.size() > 1) { //condition set
            System.err.println("|C|: "+C.size());
            Set<Integer> Cx[] = new Set[C.size()];
            double sm[] = new double[C.size()];
            double min = Double.MAX_VALUE;
            int i = 0, m = -1;
            for (Integer x : C) {
                Cx[i] = new HashSet<Integer>(C);
                Cx[i].remove(x);
                sm[i] = I(ds, X, Y, Cx[i]);
                if (sm[i] < min) {
                    min = sm[i];
                    m = i;
                }
            }

            if (sm[m] < eps) {
                return true;
            } else if (sm[m] > s) {
                break;
            } else {
                C = Cx[m];
                s = sm[m];// from the paper.
            }
        }
        
        return false;
    }

    private Set<Integer> neighborsToPath(int X, int Y, Graph G){
        Set<Integer> neighbors = new HashSet<Integer>();
        Set<Integer> Sx = G.neighbors(X, true);
        for(Integer neighbor : Sx){
            if(!G.adjacencyPath(neighbor, Y, true).isEmpty()){
                neighbors.add(neighbor);
            }
        }
        
        return neighbors;
    }
    
    private Set<Integer> neighborsOfNeighborsToPath(int X, int Y, Graph G){
        Set<Integer> neighbors = new HashSet<Integer>();
        Set<Integer> N = neighborsToPath(X, Y, G);
        for(Integer neighbor : N){
            neighbors.addAll(neighborsToPath(neighbor, Y, G));
        }
        neighbors.remove(X);
        neighbors.remove(Y);
        
        return neighbors;
    }
    
    private Set<Integer> sequence(int X, Set<Integer> adjPath, Graph G) {
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
                Set<Integer> path = G.adjacencyPath(s1, s2, true, true);

                //2. Find the neighbors of the nodes in N1 that are on the adjacency paths between s1 and s2, and do not belong to
                //N1. Put them into set N1’.
                N1 = sequence(s1, path, G);//sequence(s1, path, G);

                //3. Find the neighbors of the nodes in N2 that are on the adjacency paths between s1 and s2, and do not belong to
                //N2. Put them into set N2’.
                N2 = sequence(s2, path, G);//sequence(s2, path, G);

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

        boolean canOrientAnEdge = true;
        while (canOrientAnEdge) {
            canOrientAnEdge = false;
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
                            canOrientAnEdge = true;
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
                        canOrientAnEdge = true;
                    }
                }
            }
        }
        //11. Go back to step 9, and repeat until no more edges can be oriented.

        return G;
    }
    
    private Graph orientEdges2(Graph G, DataSet ds, double eps){
        int N = G.getVertexCount();
        for(int x=0;x<N;x++){
            for(int y=0;y<N;y++){
                if(x==y) continue;
                for(int z=0;z<N;z++){
                    if(x==z || y==z) continue;
                    if(G.isEdge(x, y, true) &&
                       G.isEdge(y, z, true) &&
                       !G.isEdge(x, z, true)){
                        Set<Integer> cs = CutSet.get(new Pair<Integer,Integer>(x,z));
                        if(cs==null || !cs.contains(y)){
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
            int M = (countTree.getCount(z.toArray(new Pair[0])));
            double Pz = ((M) / (double) N);

            z.add(x);
            double Pxz = (countTree.getCount(z.toArray(new Pair[0])) / (double) M);
            z.remove(z.size() - 1);

            z.add(y);
            double Pyz = (countTree.getCount(z.toArray(new Pair[0])) / (double) M);

            z.add(x);
            double Pxyz = (countTree.getCount(z.toArray(new Pair[0])) / (double) M);

            info += (Pxyz) * (Math.log(Pxyz * Pz) - Math.log(Pxz) - Math.log(Pyz));
        }
        return info;
    }
   
    private double I(DataSet ds, int X, int Y) {
        double info = 0;
        int N = ds.getItemsCount();
        for (List<Attribute> record : ds.getCombinedValuesOf(X, Y)) {
            Attribute valuex = record.get(X);
            Attribute valuey = record.get(Y);
            Pair<Integer, Attribute> x = new Pair<Integer, Attribute>(X, valuex);
            Pair<Integer, Attribute> y = new Pair<Integer, Attribute>(Y, valuey);
            double Px = countTree.getCount(x) / (double) N;
            double Py = countTree.getCount(y) / (double) N;
            double Pxy = countTree.getCount(x, y) / (double) N;
            info += Pxy * (Math.log(Pxy) - Math.log(Px) - Math.log(Py));
        }
        return info;
    }

    public static BayesNetwork getInstance(File xmlbif){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new FileInputStream(xmlbif));
            Node root = doc.getElementsByTagName("NETWORK").item(0);

            return (BayesNetwork)new BayesNetwork().load(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static BayesNetwork getInstance(DataSet ds) {
        return (BayesNetwork)new BayesNetwork().train(ds);
    }
    
    public static void main(String arg[]) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/iris", "root", "r00t");
        DataSet ds = new MySQLDataSet(conn, "alarm5", 0);
        int N = ds.getMetaData().getAttributeCount();
        BayesNetwork bn = new BayesNetwork();
        bn.initCountTree(ds);
        Graph G = bn.getStructure(ds, EPSILON);
        
        /*for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(i==j) continue;
                Set<Integer> adj = G.adjacencyPath(i, j, true);
                Set<Integer> N1 = G.neighbors(i, true);
                N1.retainAll(adj);
                
                Set<Integer> N2 = bn.neighborsToPath(i, j, G);
                
                System.err.println(N2+" "+N1);
            }
        }*/
        
        //BayesNetwork.getInstance(ds);
    }
}   
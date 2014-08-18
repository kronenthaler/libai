/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libai.common;

import libai.common.*;
import java.io.*;
import java.util.*;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * It represent the problem being solved. It is composed of a Matrix containing
 * the relation between nodes in the graph.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public class Graph {
    /**
     * The most important piece of information of this class: the matrix which
     * represent the problem graph
     */
    private Matrix M;
    /**
     * If the graph is loaded from a file, this will be a holder for the file's
     * source location
     */
    protected String fileSource = "";

    /**
     * Constructor. Creates a matrix from file <code>fileSource</code>
     *
     * @param fileSource location of file
     * @throws Exception if an error occurred loading file.
     */
    public Graph(String fileSource) throws Exception {
        this.setFileSource(fileSource);
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileSource));
        Matrix m = (Matrix) in.readObject();
        in.close();
        this.setM(m);
    }

    /**
     * Constructor. Creates a graph (matrix) and initialize with the data on
     * <code>data</code>
     *
     * @param r number of rows
     * @param c number of cols
     * @param d values to initialize the matrix.
     */
    public Graph(int r, int c, double[] d) {
        this.setM(new Matrix(r, c, d));
    }

    public Graph(int vertices) {
        M = new Matrix(vertices, vertices);
        M.setValue(0);
    }
    
    public Graph(Graph g){
        this(g.getVertexCount());
        g.getM().copy(M);
    }
    
    public Graph(Matrix m){
        this(m.getRows());
        m.copy(this.M);
    }
    
    /**
     * @return The matrix M
     */
    public Matrix getM() {
        return M;
    }

    public int getVertexCount() {
        return M.getColumns();
    }

    public int getEdgeCount() {
        int count = 0;
        for (int i = 0, n = M.getRows(); i < n; i++) {
            for (int j = 0, m = M.getColumns(); j < m; j++) {
                if (M.position(i, j) > 0)
                    count++;
            }
        }
        return count;
    }

    public void addEdge(Pair<Integer, Integer> e, double cost) {
        addEdge(e.first, e.second, cost, false);
    }

    public void addEdge(Pair<Integer, Integer> e, double cost, boolean ignoreDirection) {
        addEdge(e.first, e.second, cost, ignoreDirection);
    }

    public void addEdge(Pair<Integer, Integer> e) {
        addEdge(e.first, e.second, false);
    }

    public void addEdge(Pair<Integer, Integer> e, boolean ignoreDirection) {
        addEdge(e.first, e.second, ignoreDirection);
    }

    public void addEdge(int X, int Y) {
        addEdge(X, Y, 1);
    }

    public void addEdge(int X, int Y, boolean ignoreDirection) {
        addEdge(X, Y, 1, ignoreDirection);
    }

    public void addEdge(int X, int Y, double cost) {
        addEdge(X, Y, cost, false);
    }

    public void addEdge(int X, int Y, double cost, boolean ignoreDirection) {
        M.position(X, Y, cost);
        if (ignoreDirection)
            M.position(Y, X, cost);
    }

    public void removeEdge(Pair<Integer, Integer> e) {
        removeEdge(e.first, e.second);
    }

    public void removeEdge(int X, int Y) {
        removeEdge(X, Y, false);
    }

    public void removeEdge(int X, int Y, boolean ignoreDirection) {
        M.position(X, Y, 0);
        if (ignoreDirection)
            M.position(Y, X, 0);
    }

    /**
     * Sets the Matrix M which holds the graph information
     *
     * @param M contains a matrix with the graph
     */
    private void setM(Matrix M) {
        //recorrer todas las posiciones y todas aquellas que sean 0 ponerlas en Integer.MAX_VALUE
        for (int i = 0; i < M.getRows(); i++)
            for (int j = 0; j < M.getColumns(); j++)
                if (M.position(i, j) <= 0)
                    M.position(i, j, Integer.MAX_VALUE);
        this.M = M;
    }

    /**
     * @return The file source
     */
    public String getFileSource() {
        return fileSource;
    }

    /**
     * Sets the file source of the matrix to be loaded
     *
     * @param fileSource contains the location of the file
     */
    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

    public void save(String path) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.writeObject(M);
        out.close();
    }

    public Set<Integer> neighbors(int X, boolean ignoreDirection) {
        Set<Integer> neighbors = new HashSet<Integer>();
        for (int i = 0; i < M.getColumns(); i++) {
            if (isEdge(X, i, ignoreDirection))
                neighbors.add(i);
        }
        return neighbors;
    }

    public Set<Integer> adjacencyPath(int X, int Y, boolean ignoreDirection) {
        return adjacencyPath(X, Y, ignoreDirection, false);
    }
    
    public Set<Integer> adjacencyPath(int X, int Y, boolean ignoreDirection, boolean all) {
        Set<Integer> path = new HashSet<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        List<Pair<Integer, Pair>> queue = new ArrayList<Pair<Integer, Pair>>();
        queue.add(new Pair<Integer, Pair>(X, null));

        while (!queue.isEmpty()) {
            Pair<Integer, Pair> current = queue.remove(0);
            if (current.first == Y) { //what happen if there is more than one path?
                while (current != null) {
                    path.add(current.first);
                    current = current.second;
                }
                if(all)
                    continue;// all paths
                else
                    break;
            }
            
            visited.add(current.first);
            for (int i = 0; i < M.getColumns(); i++) {
                if (!visited.contains(i)) {
                    if (isEdge(current.first, i, ignoreDirection)) {
                        queue.add(new Pair<Integer, Pair>(i, current));
                    }
                }
            }
        }

        return path;
    }

    public boolean pathExists(int X, int Y, Pair<Integer, Integer> e) {
        boolean xy = isEdge(e.first, e.second, false);
        boolean yx = isEdge(e.second, e.first, false);
        removeEdge(e.first, e.second, true);

        Set<Integer> path = adjacencyPath(X, Y, true);
        boolean pathExists = path.size() > 0;

        if (xy)
            addEdge(e.first, e.second);
        if (yx)
            addEdge(e.second, e.first);

        return pathExists;
    }

    public boolean isEdge(int x, int y, boolean ignoreDirection) {
        return M.position(x, y) > 0 || (ignoreDirection && M.position(y, x) > 0);
    }

    public boolean isParent(int x, int y) {
        return isEdge(x, y, false) && !isEdge(y, x, false);
    }

    public boolean isOriented(int x, int y) {
        boolean a = isEdge(x, y, false);
        boolean b = isEdge(y, x, false);
        return isEdge(x, y, true) && a ^ b;
    }

    public String toString() {
        return M.toString();
    }

    public void saveAsDot(File path, boolean directed, String[] names) {
        try {
            PrintStream out = new PrintStream(path);
            String name = "graph";
            String separator = " -- ";
            
            if(directed){
                name = "digraph";
                separator = " -> ";
            }
            
            out.println(name + " G {");
            for (int i = 0, n = getVertexCount(); i < n; i++) {
                for (int j = i; j < n; j++) {
                    if (!isEdge(i, j, true))
                        continue;
                    int x = i;
                    int y = j;
                    
                    if(isEdge(i, j, false) && isEdge(j,i,false)){
                        if(directed){
                            if (names == null)
                                out.println(y + separator + x + ";");
                            else
                                out.println(names[y] + separator + names[x] + ";");
                        }
                    }else if(isEdge(j, i, false)){
                        x=j;
                        y=i;
                    }
                    
                    if (names == null)
                        out.println(x + separator + y + ";");
                    else
                        out.println(names[x] + separator + names[y] + ";");
                }
            }
            out.println("}");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

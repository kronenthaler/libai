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
    
    public Graph(int vertices){
        M = new Matrix(vertices, vertices);
        M.setValue(0);
    }
    
    /**
     * @return The matrix M
     */
    public Matrix getM() {
        return M;
    }
    
    public int getVertexCount(){
        return M.getColumns();
    }
    
    public void addEdge(Pair<Integer, Integer> e, double cost){
        addEdge(e.first, e.second, cost);
    }
    
    public void addEdge(Pair<Integer, Integer> e){
        addEdge(e.first, e.second);
    }
    
    public void addEdge(int X, int Y){
        addEdge(X, Y, 1);
    }
    
    public void addEdge(int X, int Y, double cost){
        M.position(X, Y, cost);
    }
    
    public void removeEdge(Pair<Integer, Integer> e){
        removeEdge(e.first, e.second);
    }
    
    public void removeEdge(int X, int Y){
        removeEdge(X, Y, false);
    }
    
    public void removeEdge(int X, int Y, boolean ignoreDirection){
        M.position(X, Y, 0);
        if(ignoreDirection)
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
    
    public Set<Integer> neighbors(int X, boolean ignoreDirection){
        Set<Integer> neighbors = new HashSet<Integer>();
        for(int i=0;i<M.getColumns();i++){
            if(isEdge(X, i, ignoreDirection))
                neighbors.add(i);
        }
        return neighbors;
    }
    
    public Set<Integer> adjacencyPath(int X, int Y, boolean ignoreDirection){
        Set<Integer> path = new HashSet<Integer>();
        HashSet<Integer> visited = new HashSet<Integer>();
        List<Pair<Integer, Pair>> queue = new ArrayList<Pair<Integer, Pair>>();
        queue.add(new Pair<Integer, Pair>(X,null));
        
        while(!queue.isEmpty()){
            Pair<Integer, Pair> current = queue.remove(0);
            if(current.first == Y){
                while(current!=null){
                    path.add(current.first);
                    current = current.second;
                }
                break;
            }
            
            for(int i=0;i<M.getColumns();i++){
                if(!visited.contains(i)){
                    if(isEdge(current.first, i, ignoreDirection)){
                        visited.add(i);
                        queue.add(new Pair<Integer, Pair>(i, current));
                    }
                }
            }
        }
        
        return path;
    }
    
    /**
     * If there is another path between X and Y besides this edge e.
     */
    public boolean pathExists(int X, int Y, Pair<Integer, Integer> e){
        removeEdge(e);
        boolean pathExists = adjacencyPath(X, Y, true).size() > 0;
        addEdge(e);
        
        return pathExists;
    }
    
    public boolean isEdge(int x, int y, boolean ignoreDirection){
        return M.position(x, y) > 0 || (ignoreDirection && M.position(y, x) > 0);
    }
    
    public String toString(){
        return M.toString();
    }
}

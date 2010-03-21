/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.libai.ants;

import java.io.*;
import net.sf.libai.common.*;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * It represent the problem being solved.
 * It is composed of a Matrix containing the relation between nodes in the graph.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public class Graph {

    /** The most important piece of information of this class: the matrix which represent the problem graph */
    private Matrix M;

    /** If the graph is loaded from a file, this will be a holder for the file's source location */
    protected String fileSource = "";

    /**
     *	Constructor. Creates a matrix from file <code>fileSource</code>
     *	@param fileSource location of file
     *	@throws Exception if an error ocurred loading file.
     */
    public Graph(String fileSource) throws Exception{
        this.setFileSource(fileSource);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileSource));
		Matrix m = (Matrix)in.readObject();
		in.close();
		this.setM(m);
    }

    /**
     *	Constructor. Creates a grpah (matrix) and initialize with the data on <code>data</code>
     *	@param r number of rows
     *	@param c number of cols
     *	@param d values to initialize the matrix.
     */
    public Graph(int r,int c,double[] d){
        this.setM(new Matrix(r,c,d));
    }

	/**
     * @return The matrix M
     */
    public Matrix getM() {
        return M;
    }

	/**
     * Sets the Matrix M which holds the graph information
     * @param M contains a matrix with the graph
     */
    private void setM(Matrix M) {
		//recorrer todas las posiciones y todas aquellas que sean 0 ponerlas en Integer.MAX_VALUE
		for(int i=0;i<M.getRows();i++)
			for(int j=0;j<M.getColumns();j++)
				if(M.position(i,j) <= 0)
					M.position(i,j,Integer.MAX_VALUE);
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
     * @param fileSource contains the location of the file
     */
    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

	public void save(String path) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
		out.writeObject(M);
		out.close();
	}
}

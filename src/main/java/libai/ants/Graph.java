/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Enrique Areyan <enrique3 at gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package libai.ants;

import libai.common.matrix.Matrix;

import java.io.*;

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
	 * Constructor. Creates a matrix from file
	 * <code>fileSource</code>
	 *
	 * @param fileSource location of file
	 * @throws Exception if an error ocurred loading file.
	 */
	public Graph(String fileSource) throws Exception {
		this.setFileSource(fileSource);
		
		try (FileInputStream fis = new FileInputStream(fileSource);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			Matrix m = (Matrix) ois.readObject();
			this.setM(m);
		}	
	}

	/**
	 * Constructor. Creates a grpah (matrix) and initialize with the data on
	 * <code>data</code>
	 *
	 * @param r number of rows
	 * @param c number of cols
	 * @param d values to initialize the matrix.
	 */
	public Graph(int r, int c, double[] d) {
		this.setM(new Matrix(r, c, d));
	}

	/**
	 * @return The matrix M
	 */
	public Matrix getM() {
		return M;
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
		try (FileOutputStream fos = new FileOutputStream(path);
			 ObjectOutputStream out = new ObjectOutputStream(fos)) {
			out.writeObject(M);
		}
	}
}

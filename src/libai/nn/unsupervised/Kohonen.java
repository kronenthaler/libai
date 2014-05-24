package libai.nn.unsupervised;

import libai.common.Matrix;
import libai.common.Pair;
import java.io.*;
import java.util.*;

import libai.nn.NeuralNetwork;

/**
 * Kohonen's Self-organizative Maps or SOM or Kohonen. This maps are one of the
 * most important unsupervised neural networks of the history. The most
 * important feature of the kohonen's maps is the possibility of transform any
 * multidimensional space into a R^2 space, providing a highly precise clusteing
 * method. One of the most famous examples for the kohonen's maps is the
 * transform the RGB color cube into a plane where the reds, greens, blues, etc
 * are clustered in a very similar way of the any color picker utility.
 *
 * @author kronenthaler
 */
public class Kohonen extends NeuralNetwork {
    private Matrix W[];					//array of weights ijk, with k positions.
    private int[][] map;				//map of the outputs
    private int[] nperlayer;			//array of 3 positions, {#inputs,#rows,#columns}
    private double neighborhood;
    private int stepsx[], stepsy[];

    public Kohonen() {
    }

    /**
     * Constructor. Creates a kohonen's map with nperlayer[0] inputs,
     * nperlayer[1] rows and nperlayer[2] columns. Additional set the initial
     * size of the neighborhood and the way in the neighbors are connected.
     *
     * @param nperlayer Number of neurons (input, rows and columns)
     * @param _neighborhood Initial size of the neighborhood
     * @param neighboursX neighbors along the X-axis
     * @param neighboursY neighbors along the Y-axis
     */
    public Kohonen(int[] nperlayer, double _neighborhood, int[] neighboursX, int[] neighboursY) {
        this.nperlayer = nperlayer;
        neighborhood = _neighborhood;

        W = new Matrix[nperlayer[1] * nperlayer[2]];
        stepsx = neighboursX;
        stepsy = neighboursY;

        for (int i = 0; i < nperlayer[1]; i++) {
            for (int j = 0; j < nperlayer[2]; j++) {
                W[(i * nperlayer[2]) + j] = new Matrix(nperlayer[0], 1);
                W[(i * nperlayer[2]) + j].setValue(0);
            }
        }
        map = new int[nperlayer[1]][nperlayer[2]];
        for (int i = 0; i < map.length; i++)
            Arrays.fill(map[i], -1);
    }

    /**
     * Constructor. Creates a kohonen's map using the standard neighborhood (up,
     * down, left, right). Alias of Kohonen(nperlayer, _neighborhood, new
     * int[]{0,0,1,-1}, new int[]{-1,1,0,0});
     *
     * @param nperlayer Number of neurons (input, rows and columns)
     * @param _neighborhood Initial size of the neighborhood
     */
    public Kohonen(int[] nperlayer, double _neighborhood) {
        this(nperlayer, _neighborhood, new int[]{0, 0, 1, -1}, new int[]{-1, 1, 0, 0});
    }

    /**
     * Train the map. The answers are omitted for the training process but are
     * necessary for the labeling of the map.
     *
     * @param patterns	The patterns to be learned.
     * @param answers The expected answers.
     * @param alpha	The learning rate.
     * @param epochs	The maximum number of iterations
     * @param offset	The first pattern position
     * @param length	How many patterns will be used.
     * @param minerror The minimal error expected.
     */
    @Override
    public void train(Matrix[] patterns, Matrix[] answers, double alpha, int epochs, int offset, int length, double minerror) {
        int curr_epoch = 0, ig = 0, jg = 0;
        double lamda = neighborhood;
        double alpha1 = alpha;

        Random rand = new Random();
        int[] sort = new int[length];
        for (int i = 0; i < length; sort[i] = i++);

        Matrix temp = new Matrix(nperlayer[0], 1);
        Matrix winner = new Matrix(2, 1);

        if (progress != null) {
            progress.setMaximum(epochs * 2);
            progress.setMinimum(0);
            progress.setValue(0);
        }

        while (curr_epoch++ < epochs) {
            //System.out.println("epoch: "+curr_epoch);
            //shuffle
            shuffle(sort);

            for (int k = 0; k < length; k++) {
                //Who is the winner
                simulate(patterns[sort[k] + offset], winner);

                ig = (int) winner.position(0, 0);
                jg = (int) winner.position(1, 0);

                //Update winner and neighbors.
                for (int i = 0; i < nperlayer[1]; i++) {
                    for (int j = 0; j < nperlayer[2]; j++) {
                        Matrix Mij = get(i, j);
                        patterns[sort[k] + offset].subtract(Mij, temp);
                        temp.multiply(alpha1 * neighbor(i, j, ig, jg), temp);
                        Mij.add(temp, Mij);
                    }
                }
            }

            //update neighborhood's ratio.
            if (neighborhood >= 0.5)
                neighborhood = lamda * Math.exp(-(float) curr_epoch / (float) epochs);

            //update alpha
            if (alpha1 > 0.001)
                alpha1 = alpha * Math.exp(-(float) curr_epoch / (float) epochs);

            if (progress != null)
                progress.setValue(curr_epoch);
        }

        expandMap(patterns, answers, offset, length);
        if (progress != null)
            progress.setValue(epochs * 2);
    }

    @Override
    public Matrix simulate(Matrix pattern) {
        Matrix ret = new Matrix(2, 1);
        simulate(pattern, ret);
        return ret;
    }

    @Override
    public void simulate(Matrix pattern, Matrix result) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < nperlayer[1]; i++) {
            for (int j = 0; j < nperlayer[2]; j++) {
                double temp = euclideanDistance2(pattern, get(i, j));
                if (temp < min) {
                    result.position(0, 0, i);
                    result.position(1, 0, j);
                    min = temp;
                }
            }
        }
    }

    /**
     * The error metric used for the Kohonen's map is the mean of the border
     * distance. For a projection point the shortest distance to the right
     * cluster. Average for each distance. Lower distance means less error
     * Higher distance means bigger error.
     *
     * @param patterns The array with the patterns to test
     * @param answers The array with the expected answers for the patterns.
     * @param offset The initial position inside the array.
     * @param length How many patterns must be taken from the offset.
     * @return The average border distance.
     */
    @Override
    public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
        double error = 0;

        Matrix winner = new Matrix(2, 1);
        for (int i = 0; i < length; i++) {
            //calculate the winner neuron
            simulate(patterns[i + offset], winner);

            int x = (int) winner.position(0, 0);
            int y = (int) winner.position(1, 0);

            //take the euclidean distance to the nearest neuron in the expected cluster.
            boolean[][] visited = new boolean[map.length][map[0].length];

            ArrayList<Pair<Integer, Integer>> q = new ArrayList<Pair<Integer, Integer>>();
            q.add(new Pair<Integer, Integer>(x, y));
            visited[x][y] = true;

            while (!q.isEmpty()) {
                Pair<Integer, Integer> current = q.remove(0);

                if (map[current.first][current.second] == (int) answers[i + offset].position(0, 0)) {
                    error += Math.sqrt((current.first - x) * (current.first - x) + (current.second - y) * (current.second - y));
                    break;
                }

                for (int k = 0; k < stepsx.length; k++) {
                    int ii = current.first + stepsx[k];
                    int ij = current.second + stepsy[k];
                    if (ii >= 0 && ii < nperlayer[1] && ij >= 0 && ij < nperlayer[2] && !visited[ii][ij]) {
                        q.add(new Pair<Integer, Integer>(ii, ij));
                        visited[ii][ij] = true;
                    }
                }
            }
        }

        error /= length;
        return error;
    }

    private Matrix get(int i, int j) {
        return W[(i * nperlayer[2]) + j];
    }

    private double neighbor(int i, int j, int ig, int jg) {
        return gaussian(distance(i, j, ig, jg), neighborhood * neighborhood);
    }

    private double distance(int i, int j, int ig, int jg) {
        return (((i - ig) * (i - ig)) + ((j - jg) * (j - jg)));
    }

    /**
     * @return The label map.
     */
    public int[][] getMap() {
        return map;
    }

    /**
     * Label the output for the patterns and expand the results through the
     * neighbors until the map is completely fill. NOTE: The expansion isn't an
     * standard process but is very helpful to avoid unknown answers.
     *
     * @param patterns The patterns to label
     * @param answers	The expected answer for the patterns
     * @param offset	The initial pattern position
     * @param length	How many patterns to label.
     */
    private void expandMap(Matrix[] patterns, Matrix[] answers, int offset, int length) {
        Matrix winner = new Matrix(2, 1);
        //System.out.println("labelling...");
        for (int k = 0; k < length; k++) {
            simulate(patterns[k + offset], winner);

            int i = (int) winner.position(0, 0);
            int j = (int) winner.position(1, 0);

            if (map[i][j] == -1) //no overlapping
                map[i][j] = (int) answers[k + offset].position(0, 0); //must have just one position and should be an integer
        }

        ArrayList<Pair<Integer, Integer>> q = new ArrayList<Pair<Integer, Integer>>();

        for (int i = 0; i < nperlayer[1]; i++)
            for (int j = 0; j < nperlayer[2]; j++)
                if (map[i][j] != -1)
                    q.add(new Pair<Integer, Integer>(i, j));

        //System.out.println("BFS...");
        while (!q.isEmpty()) {
            Pair<Integer, Integer> current = q.remove(0);
            int c = map[current.first][current.second];

            for (int k = 0; k < stepsx.length; k++) {
                int i = current.first + stepsx[k];
                int j = current.second + stepsy[k];
                if (i >= 0 && i < nperlayer[1] && j >= 0 && j < nperlayer[2] && map[i][j] == -1) {
                    q.add(new Pair<Integer, Integer>(i, j));
                    map[i][j] = c;
                }
            }
        }
    }

    public static Kohonen open(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            Kohonen p = (Kohonen) in.readObject();
            in.close();
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

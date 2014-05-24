package libai.nn.unsupervised;

import libai.common.Matrix;
import libai.common.functions.Sign;
import java.io.*;
import java.util.*;

import libai.nn.NeuralNetwork;

/**
 * Hebbian networks are one of the few autoassociative neural networks. An
 * autoassociative network consists in to learn the same input pattern as output
 * pattern. This networks just is able to learn binary patterns because its
 * output function (sign). The Hebbian networks uses the Hebb's rule for
 * trainning. The Hebb's rule is one of the most important training rules in
 * unsupervised networks. Other algorithms like Kohonen uses this rule as base.
 *
 * @author kronenthaler
 */
public class Hebb extends NeuralNetwork {
    protected double phi;
    protected Matrix W;
    protected static Sign sign = new Sign();

    public Hebb() {
    }

    /**
     * Constructor. Creates a Hebbian network with the equals number of inputs
     * and outputs. Set the decay constant to zero to eliminate it. Alias of
     * this(inputs, 0);
     *
     * @param inputs Number of inputs for the network.
     */
    public Hebb(int inputs) {
        this(inputs, 0);
    }

    /**
     * Constructor. Creates a Hebbian network with the equals number of inputs
     * and outputs. Set the constant for decay <code>phi</code>. If phi = 0 the
     * network don't forget anything, if phi = 1 the network just remember the
     * las pattern.
     *
     * @param inputs Number of inputs and outputs for the networks.
     * @param phi Decay constant.
     */
    public Hebb(int inputs, double phi) {
        this.phi = 1 - phi; //precalculation for the decay 1-phi
        W = new Matrix(inputs, inputs);
        W.fill();
    }

    /**
     * Train the network using the Hebb's rule with decay. The hebb's rule,
     * consist on reinforce the right connections if they produce a correct
     * answer and inhibit the others. The decay term has an influence in how
     * much affects the previous knowledge to the reinforcement.
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
        Random rand = new Random();
        int[] sort = new int[length];
        Matrix Y = new Matrix(W.getRows(), 1);
        Matrix temp = new Matrix(W.getRows(), W.getColumns());

        Matrix[] patternsT = new Matrix[length];
        for (int i = 0; i < length; i++) {
            patternsT[i] = patterns[i + offset].transpose();
            sort[i] = i;
        }

        if (progress != null) {
            progress.setMaximum(0);
            progress.setMinimum(-epochs);
            progress.setValue(-epochs);
        }

        while (epochs-- > 0) {
            //shuffle patterns
            shuffle(sort);

            for (int i = 0; i < length; i++) {
                //F(wx)
                simulate(patterns[sort[i] + offset], Y);

                //W=(1-phi)*W + alpha*Y*pt;
                //W.multiply(phi,W);
                //Y.multiply(patternsT[sort[i]],temp);
                //temp.multiply(alpha,temp);
                //W.add(temp,W);
                //alternative rule: no just have decay term, also inhibit the connections
                //Wij=Wij+(phi*yi*(alpha/phi*xi - Wij))
                //require 2 cicles to update properly the weights
                for (int k = 0; k < W.getRows(); k++) {
                    for (int j = 0; j < W.getColumns(); j++) {
                        W.position(k, j, W.position(k, j) + phi * Y.position(k, 0) * (((alpha / phi) * patterns[sort[i] + offset].position(k, 0)) - W.position(k, j)));
                    }
                }
            }
            if (progress != null)
                progress.setValue(-epochs);
        }
        if (progress != null)
            progress.setValue(1);
    }

    @Override
    public Matrix simulate(Matrix pattern) {
        Matrix ret = new Matrix(pattern.getRows(), pattern.getColumns());
        simulate(pattern, ret);
        return ret;
    }

    /**
     * Calculate the output for the pattern and left the result on result.
     * result = sign(W * pattern)
     *
     * @param pattern The input pattern
     * @param result The output result.
     */
    @Override
    public void simulate(Matrix pattern, Matrix result) {
        W.multiply(pattern, result);
        result.apply(sign, result);
    }

    /**
     * Calculate the error of the networks using the Hamming distance instead
     * the Euclidean. Hamming distance is defined as the number of bits
     * different between to bits chains,e.g. 7=111 2=010 the hamming distance
     * between H(7,2) = 2, the first and third bits differs.
     *
     * @param patterns The array with the patterns to test
     * @param answers The array with the expected answers for the patterns.
     * @param offset The initial position inside the array.
     * @param length How many patterns must be taken from the offset.
     * @return The average hamming distance.
     */
    @Override
    public double error(Matrix[] patterns, Matrix[] answers, int offset, int length) {
        Matrix X = new Matrix(patterns[0].getRows(), patterns[0].getColumns());
        Matrix Y = new Matrix(patterns[0].getRows(), patterns[0].getColumns());

        double error = 0;
        for (int i = 0; i < length; i++) {
            simulate(patterns[i + offset], X);
            patterns[i + offset].apply(sign, Y);

            for (int j = 0; j < X.getRows(); j++)
                error += Math.pow(Y.position(j, 0) - X.position(j, 0), 2);//0-1=1, 0-0=0, 1-1=0, 1-0=1
        }

        return error / (double) (length * patterns[0].getRows());
    }

    public static Hebb open(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            Hebb p = (Hebb) in.readObject();
            in.close();
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

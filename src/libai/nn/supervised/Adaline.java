package libai.nn.supervised;

import java.io.*;
import libai.common.Matrix;

/**
 * <b>Ada</b>ptative <b>Line</b>ar neural network. Is a special case of the
 * single layer Perceptron. Uses a identity as exit function. The only diference
 * between the trainning algorithms is the 2*alpha multiplication. Because of
 * this, the Adaline implementation is a subclass of perceptron single layer.
 *
 * @author kronenthaler
 */
public class Adaline extends Perceptron {
    public Adaline() {
    }

    /**
     * Constructor.
     *
     * @param ins Number of inputs for the network = number of elements in the
     * patterns.
     * @param outs Number of outputs for the network.
     */
    public Adaline(int ins, int outs) {
        super(ins, outs);
    }

    /**
     * Alias of super.train(patterns, answers, 2*alpha, epochs, offset, length,
     * minerror);
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
        super.train(patterns, answers, 2 * alpha, epochs, offset, length, minerror);
    }

    /**
     * Calculate the output for the pattern and left the result on result.
     * result = W * pattern + b
     *
     * @param pattern The input pattern
     * @param result The output result.
     */
    @Override
    public void simulate(Matrix pattern, Matrix result) {
        W.multiply(pattern, result);	//inner product
        result.add(b, result);		//bias
    }

    public static Adaline open(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            Adaline p = (Adaline) in.readObject();
            in.close();
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

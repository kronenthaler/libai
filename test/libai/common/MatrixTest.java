/*
 * MIT License
 *
 * Copyright (c) 2016 Federico Vera <https://github.com/dktcoding>
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
package libai.common;

import java.util.Random;
import libai.common.functions.Function;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal dktcoding [at] gmail}
 */
public class MatrixTest {
    private static final double DELTA = 1e-12;
    
    public MatrixTest() {
    }
    
    @Test
    public void testConstructorNotIdentity1() {
        Matrix m = new Matrix(5, 5, false);
        for (int i = 0; i < m.getRows(); i++) {
            assertArrayEquals(new double[]{0,0,0,0,0}, m.getRow(i), DELTA);
        }
    }
    
    @Test
    public void testConstructorNotIdentity2() {
        Matrix m = new Matrix(15, 5, false);
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                assertEquals(0, m.position(i, j), DELTA);
            }
        }
    }
    
    @Test
    public void testConstructorIdentity1() {
        Matrix m = new Matrix(5, 5, true);
        for (int i = 0; i < m.getRows(); i++) {
            double[] test = new double[5];
            test[i] = 1;
            assertArrayEquals(test, m.getRow(i), DELTA);
        }
    }
    
    @Test
    public void testConstructorIdentity2() {
        Matrix m = new Matrix(15, 5, true);
        for (int i = 0; i < m.getColumns(); i++) {
            double[] test = new double[5];
            test[i] = 1;
            assertArrayEquals(test, m.getRow(i), DELTA);
        }
    }
    
    @Test
    public void testConstructorIdentity3() {
        Matrix m = new Matrix(5, 15, true);
        for (int i = 0; i < m.getRows(); i++) {
            double[] test = new double[15];
            test[i] = 1;
            assertArrayEquals(test, m.getRow(i), DELTA);
        }
    }

    @Test
    public void testRandom_3args() {
        Matrix a = Matrix.random(50, 100, true);
        Matrix b = Matrix.random(50, 100, false);
        assertNotEquals(a, b); //<- yes... this test could actually fail...
        
        boolean hasNegativeA = false;
        boolean hasNegativeB = false;
        
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA = hasNegativeA || a.position(i, j) < 0;
                hasNegativeB = hasNegativeB || b.position(i, j) < 0;
            }
        }
        
        assertTrue (hasNegativeA);
        assertFalse(hasNegativeB);
    }

    @Test
    public void testRandomIntInt() {
        Matrix a = Matrix.random(50, 100);
        Matrix b = Matrix.random(50, 100);
        assertNotEquals(a, b); //<- yes... this test could actually fail...
    }

    @Test
    public void testMultiplyDoubleMatrix() {
        Matrix a = Matrix.random(10, 40);
        Matrix b = new Matrix(10, 40);
        Matrix c = new Matrix(10, 40);
        a.multiply(0, b);
        assertNotEquals(a, b);
        assertEquals(b, c);
        a.multiply(1, b);
        assertEquals(a, b);
        a.multiply(2, b);
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                assertEquals(2 * a.position(i, j), b.position(i, j), DELTA);
            }
        }
    }

    @Test
    public void testMultiplyMatrixMatrix() {
        Matrix a = new Matrix(10, 10, true);
        Matrix b = new Matrix(10, 10, true);
        Matrix c = new Matrix(10, 10, true);
        a.multiply(b, c);
        assertEquals(a, b);
        assertEquals(a, c);
        a.multiply(a, a);
        assertEquals(a, a);
    }

    @Test
    public void testMultiplyMatrixMatrix1() {
        Matrix a = new Matrix(10, 10, true);
        Matrix b = Matrix.random(10, 40);
        Matrix c = new Matrix(10, 40);
        a.multiply(b, c);
        assertEquals(b, c);
        assertNotEquals(a, c);
    }

    @Test
    public void testMultiplyMatrixMatrix2() {
        //d1_data was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(2, 5, a1_data);
        Matrix b = new Matrix(5, 2, b1_data);
        Matrix c = new Matrix(2, 2);
        Matrix d = new Matrix(2, 2, d1_data);
        a.multiply(b, c);
        assertEquals(c, d);
    }
    double[] a1_data = {
        -0.007765831782573, -0.008486990926655, -0.009829972068673, 0.000304058679693, 
        -0.002138442736117,  0.007523070921871,  0.005449501255788, 0.005930977488527, 
         0.005607489427692,  0.002720446955797, 
    };
    double[] b1_data = {
        -0.007765831782573, -0.008486990926655, 
        -0.009829972068673,  0.000304058679693, 
        -0.002138442736117,  0.007523070921871, 
         0.005449501255788,  0.005930977488527, 
         0.005607489427692,  0.002720446955797,
    };
    double[] d1_data = {
        0.000154421532520, -0.000014637731259, 
       -0.000078861505907,  0.000023086622987,
    };

    @Test
    public void testMultiplyMatrixMatrix3() {
        //d2_data was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(1, 10, a2_data);
        Matrix b = new Matrix(10, 1, b2_data);
        Matrix c = new Matrix(1, 1);
        Matrix d = new Matrix(1, 1, d2_data);
        a.multiply(b, c);
        assertEquals(c, d);
    }
    double[] a2_data = {
        -0.007253997338875, -0.009757740181475,  0.009202997305386,  0.009821458200041, 
         0.006434371877310,  0.008282702832958, -0.008951885947104, -0.005934105911485,
        -0.006161133826338, -0.002859944646273, 
    };
    double[] b2_data = {
        -0.007253997338875, -0.009757740181475,  0.009202997305386, 0.009821458200041, 
         0.006434371877310,  0.008282702832958, -0.008951885947104,-0.005934105911485, 
        -0.006161133826338, -0.002859944646273, 
    };
    double[] d2_data = {0.000600483207479};

    @Test
    public void testApply() {
        Function f = new Function() {
            @Override
            public double eval(double x) {
                return 0;
            }
            @Override public Function getDerivate() {return null;}
        };
        Matrix a = Matrix.random(10, 10);
        Matrix b = new Matrix(10, 10);
        Matrix c = new Matrix(10, 10);
        a.apply(f, b);
        assertEquals(c, b);
    }
    
    @Test
    public void testApply1() {
        Function f = new Function() {
            @Override
            public double eval(double x) {
                return x;
            }
            @Override public Function getDerivate() {return null;}
        };
        Matrix a = Matrix.random(10, 10);
        Matrix b = new Matrix(10, 10);
        a.apply(f, b);
        assertEquals(a, b);
    }
    
    @Test
    public void testApply2() {
        Function f = new Function() {
            @Override
            public double eval(double x) {
                return x == 1 ? 0 : 1;
            }
            @Override public Function getDerivate() {return null;}
        };
        Matrix a = new Matrix(5, 5, true);
        Matrix b = new Matrix(5, 5);
        a.apply(f, b);
        assertArrayEquals(new double[]{0,1,1,1,1}, b.getRow(0), DELTA);
        assertArrayEquals(new double[]{1,0,1,1,1}, b.getRow(1), DELTA);
        assertArrayEquals(new double[]{1,1,0,1,1}, b.getRow(2), DELTA);
        assertArrayEquals(new double[]{1,1,1,0,1}, b.getRow(3), DELTA);
        assertArrayEquals(new double[]{1,1,1,1,0}, b.getRow(4), DELTA);
    }

    @Test
    public void testCopy() {
        Matrix a = Matrix.random(5, 10);
        Matrix b = new Matrix(5, 10);
        a.copy(b);
        assertEquals(a, b);
        a.position(0, 0, 0);
        assertNotEquals(a, b);
    }
    
    @Test
    public void testCopy1() {
        Matrix a = Matrix.random(10, 5);
        Matrix b = new Matrix(10, 5);
        a.copy(b);
        assertEquals(a, b);
        a.position(0, 0, 0);
        assertNotEquals(a, b);
    }

    @Test
    public void testTransposeMatrix() {
        Matrix a = new Matrix(5, 15, true);
        Matrix b = new Matrix(15, 5, true);
        a.transpose(b);
        assertNotEquals(a, b);
        assertEquals(a, b.transpose());
        assertEquals(a.getColumns(), b.getRows());
        assertEquals(a.getRows(), b.getColumns());
    }

    @Test
    public void testTranspose() {
        Matrix a = new Matrix(5, 5, true);
        Matrix b = new Matrix(5, 5, true);
        Matrix c = new Matrix(5, 5, true);
        a.transpose(b);
        assertEquals(a, b);
        a.fill();
        assertNotEquals(a, b);
        a.transpose(b);
        b.transpose(c);
        assertEquals(a, c);
        assertNotEquals(a, b);
    }

    @Test
    public void testSetValue() {
        Matrix a = Matrix.random(10, 15);
        Matrix b = Matrix.random(10, 15);
        b.position(0, 0, 1000); // <- Random always creates the same matrix!
        assertNotEquals(a, b);
        a.setValue(Math.PI);
        b.setValue(Math.PI);
        assertEquals(a, b);
        a.setValue(0);
        assertEquals(a, new Matrix(10, 15));
    }
    
    @Test
    public void testIncrement() {
        Matrix m = new Matrix(5, 5, true);
        Random rand = new Random();
        for (int k = 0; k < 10; k++) {
            int i = rand.nextInt(5), j = rand.nextInt(5);
            double inc = rand.nextDouble() * 2 - 1;
            double prev = m.position(i, j);
            m.increment(i, j, inc);
            assertEquals(prev, m.position(i, j) - inc, DELTA);
            assertNotEquals(prev, m.position(i, j), DELTA);
        }
    }

    @Test
    public void testIncrement1() {
        Matrix m = new Matrix(15, 5, true);
        Random rand = new Random();
        for (int k = 0; k < 10; k++) {
            int i = rand.nextInt(15), j = rand.nextInt(5);
            double inc = rand.nextDouble() * 2 - 1;
            double prev = m.position(i, j);
            m.increment(i, j, inc);
            assertEquals(prev, m.position(i, j) - inc, DELTA);
            assertNotEquals(prev, m.position(i, j), DELTA);
        }
    }

    @Test
    public void testIncrement2() {
        Matrix m = new Matrix(5, 15, true);
        Random rand = new Random();
        for (int k = 0; k < 10; k++) {
            int i = rand.nextInt(5), j = rand.nextInt(15);
            double inc = rand.nextDouble() * 2 - 1;
            double prev = m.position(i, j);
            m.increment(i, j, inc);
            assertEquals(prev, m.position(i, j) - inc, DELTA);
            assertNotEquals(prev, m.position(i, j), DELTA);
        }
    }

    @Test
    public void testSwap() {
        Matrix m = new Matrix(5, 5, true);
        double[] prev_row = m.getRow(3);
        assertArrayEquals(new double[]{0,0,0,1,0}, prev_row, DELTA);
        m.swap(0, 3);
        assertArrayEquals(prev_row, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{0,0,0,1,0}, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{1,0,0,0,0}, m.getRow(3), DELTA);
    }

    @Test
    public void testSwap1() {
        Matrix m = new Matrix(15, 5, true);
        double[] prev_row = m.getRow(3);
        assertArrayEquals(new double[]{0,0,0,1,0}, prev_row, DELTA);
        m.swap(0, 3);
        assertArrayEquals(prev_row, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{0,0,0,1,0}, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{1,0,0,0,0}, m.getRow(3), DELTA);
    }

    @Test
    public void testSwap2() {
        Matrix m = new Matrix(5, 10, true);
        double[] prev_row = m.getRow(3);
        assertArrayEquals(new double[]{0,0,0,1,0,0,0,0,0,0}, prev_row, DELTA);
        m.swap(0, 3);
        assertArrayEquals(prev_row, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{0,0,0,1,0,0,0,0,0,0}, m.getRow(0), DELTA);
        assertArrayEquals(new double[]{1,0,0,0,0,0,0,0,0,0}, m.getRow(3), DELTA);
    }

    @Test
    public void testEquals() {
        Matrix a = Matrix.random(10, 10);
        Matrix b = new Matrix(10, 10);
        Matrix c = new Matrix(1, 10);
        
        a.copy(b);
        assertEquals(a, b);
        b.position(1, 1, 1);
        assertNotEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(b, c);
        assertEquals(a, a);
        assertEquals(b, b);
        assertEquals(c, c);
    }

    @Test
    public void testEquals1() {
        Matrix a = new Matrix(10, 1);
        Matrix b = new Matrix(1, 10);
        
        assertNotEquals(a, b);
        b = b.transpose();
        assertEquals(a, b);
    }

    @Test
    public void testEquals2() {
        Matrix a = new Matrix(10, 10, true);
        Matrix b = new Matrix(10, 10, true);
        
        assertEquals(a, b);
        b = b.transpose();
        assertEquals(a, b);
    }

    @Test
    public void testGetRows() {
        Matrix m = new Matrix(20, 10);
        assertEquals(20, m.getRows());
    }

    @Test
    public void testGetColumns() {
        Matrix m = new Matrix(20, 10);
        assertEquals(10, m.getColumns());
    }
}

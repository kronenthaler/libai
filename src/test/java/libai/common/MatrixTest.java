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

import java.util.HashMap;
import java.util.Random;
import libai.common.functions.Function;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class MatrixTest {
    private static final double DELTA = 1e-12;

    public MatrixTest() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail1() {
        Matrix m = new Matrix(-5, 5, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail2() {
        Matrix m = new Matrix(5, -5, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail3() {
        Matrix m = new Matrix(0, 1, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail4() {
        Matrix m = new Matrix(5, 5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFail5() {
        Matrix m = new Matrix(5, 5, new double[5*4]);
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
    public void testMatrixRandom() {
        Matrix a = Matrix.random(50, 100, true);
        Matrix b = Matrix.random(50, 100, false);
        assertNotEquals(a, b);

        boolean hasNegativeA = false;
        boolean hasNegativeB = false;

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertTrue (hasNegativeA);
        assertFalse(hasNegativeB);
    }

    @Test
    public void testMatrixRandom2() {
        Random rand1 = new Random(0);
        Random rand2 = new Random(0);
        Matrix a = Matrix.random(50, 100, true,  rand1);
        Matrix b = Matrix.random(50, 100, false, rand2);
        assertNotEquals(a, b);

        boolean hasNegativeA = false;
        boolean hasNegativeB = false;

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertTrue (hasNegativeA);
        assertFalse(hasNegativeB);
    }

    @Test
    public void testMatrixRandom3() {
        Random rand = new Random(0);
        Matrix a = Matrix.random(50, 100, true,  rand);
        Matrix b = Matrix.random(50, 100, true, rand);
        assertNotEquals(a, b);
        a = Matrix.random(50, 100, true, new Random(0));
        b = Matrix.random(50, 100, true, new Random(0));
        assertEquals(a, b);
    }

    @Test
    public void testRandomIntInt() {
        Matrix a = Matrix.random(50, 100);
        Matrix b = Matrix.random(50, 100);
        assertNotEquals(a, b);
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
    public final  double[] a1_data = {
        -0.007765831782573, -0.008486990926655, -0.009829972068673, 0.000304058679693,
        -0.002138442736117,  0.007523070921871,  0.005449501255788, 0.005930977488527,
         0.005607489427692,  0.002720446955797,
    };
    public final  double[] b1_data = {
        -0.007765831782573, -0.008486990926655,
        -0.009829972068673,  0.000304058679693,
        -0.002138442736117,  0.007523070921871,
         0.005449501255788,  0.005930977488527,
         0.005607489427692,  0.002720446955797,
    };
    public final  double[] d1_data = {
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
    public final  double[] a2_data = {
        -0.007253997338875, -0.009757740181475,  0.009202997305386,  0.009821458200041,
         0.006434371877310,  0.008282702832958, -0.008951885947104, -0.005934105911485,
        -0.006161133826338, -0.002859944646273,
    };
    public final  double[] b2_data = {
        -0.007253997338875, -0.009757740181475,  0.009202997305386, 0.009821458200041,
         0.006434371877310,  0.008282702832958, -0.008951885947104,-0.005934105911485,
        -0.006161133826338, -0.002859944646273,
    };
    public final double[] d2_data = {0.000600483207479};

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
        assertNotEquals(a, b);
        a.copy(b);
        assertEquals(a, b);
        a.position(0, 0, 0);
        assertNotEquals(a, b);
    }

    @Test
    public void testCopy1() {
        Matrix a = Matrix.random(10, 5);
        Matrix b = new Matrix(10, 5);
        assertNotEquals(a, b);
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
    public void testEquals3() {
        Matrix a = new Matrix(10, 10, true);

        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
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

    @Test
    public void testPosition() {
        Matrix a = new Matrix(5, 5);
        Matrix b = new Matrix(5, 5, true);
        assertNotEquals(a, b);
        a.position(0, 0, 1);
        a.position(1, 1, 1);
        a.position(2, 2, 1);
        a.position(3, 3, 1);
        a.position(4, 4, 1);
        assertEquals(a, b);
    }

    @Test
    public void testPosition1() {
        Matrix a = Matrix.random(2, 5);
        assertArrayNotEquals(new double[]{1,2,3,4,5}, a.getRow(0), DELTA);
        a.position(0, 0, 1);
        a.position(0, 1, 2);
        a.position(0, 2, 3);
        a.position(0, 3, 4);
        a.position(0, 4, 5);
        assertArrayEquals(new double[]{1,2,3,4,5}, a.getRow(0), DELTA);
    }

    @Test
    public void testPosition2() {
        Matrix a = Matrix.random(5, 1);
        assertArrayNotEquals(new double[]{1,2,3,4,5}, a.getCol(0), DELTA);
        a.position(0, 0, 1);
        a.position(1, 0, 2);
        a.position(2, 0, 3);
        a.position(3, 0, 4);
        a.position(4, 0, 5);
        assertArrayEquals(new double[]{1,2,3,4,5}, a.getCol(0), DELTA);
    }

    @Test
    public void testPosition3() {
        Matrix a = new Matrix(10, 10, true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(i==j?1:0, a.position(i, j), DELTA);
            }
        }
    }

    @Test
    public void testPosition4() {
        Matrix a = Matrix.random(5, 1);
        assertArrayNotEquals(new double[]{1,2,3,4,5}, a.getCol(0), DELTA);
        a.position(0, 0, 1);
        a.position(1, 0, 2);
        a.position(2, 0, 3);
        a.position(3, 0, 4);
        a.position(4, 0, 5);
        assertEquals(1, a.position(0, 0), DELTA);
        assertEquals(2, a.position(1, 0), DELTA);
        assertEquals(3, a.position(2, 0), DELTA);
        assertEquals(4, a.position(3, 0), DELTA);
        assertEquals(5, a.position(4, 0), DELTA);
    }

    @Test
    public void testPosition5() {
        Matrix a = Matrix.random(1, 5);
        assertArrayNotEquals(new double[]{1,2,3,4,5}, a.getRow(0), DELTA);
        a.position(0, 0, 1);
        a.position(0, 1, 2);
        a.position(0, 2, 3);
        a.position(0, 3, 4);
        a.position(0, 4, 5);
        assertEquals(1, a.position(0, 0), DELTA);
        assertEquals(2, a.position(0, 1), DELTA);
        assertEquals(3, a.position(0, 2), DELTA);
        assertEquals(4, a.position(0, 3), DELTA);
        assertEquals(5, a.position(0, 4), DELTA);
    }

    @Test
    public void testAdd() {
        Matrix a = new Matrix(10, 10, true);
        Matrix b = new Matrix(10, 10, true);
        assertEquals(a, b);
        a.add(a, b);
        assertNotEquals(a, b);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(i==j?2:0, b.position(i, j), DELTA);
            }
        }
    }

    @Test
    public void testAdd1() {
        Matrix a = Matrix.random(5, 15);
        Matrix b = new Matrix(5, 15, true);
        Matrix c = new Matrix(5, 15);
        a.add(b, c);
        assertNotEquals(a, b);
        assertNotEquals(b, c);
        assertNotEquals(a, c);
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                double av = a.position(i, j);
                assertEquals(i==j ? av + 1 : av, c.position(i, j), DELTA);
            }
        }
    }

    @Test
    public void testSubtract() {
        Matrix a = new Matrix(10, 10, true);
        Matrix b = new Matrix(10, 10, true);
        assertEquals(a, b);
        a.subtract(a, b);
        assertNotEquals(a, b);
        assertEquals(new Matrix(10, 10), b);
    }

    @Test
    public void testSubtract1() {
        Matrix a = Matrix.random(5, 15);
        Matrix b = new Matrix(5, 15, true);
        Matrix c = new Matrix(5, 15);
        a.subtract(b, c);
        assertNotEquals(a, b);
        assertNotEquals(b, c);
        assertNotEquals(a, c);
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                double av = a.position(i, j);
                assertEquals(i==j ? av - 1 : av, c.position(i, j), DELTA);
            }
        }
    }

    @Test
    public void testFillBooleanRandom() {
        Matrix a = new Matrix(5, 10);
        Matrix b = new Matrix(5, 10);
        assertEquals(a, b);
        a.fill(true);
        assertNotEquals(a, b);
        b.fill(true);
        assertNotEquals(a, b);

        boolean hasNegativeA = false;
        boolean hasNegativeB = false;

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertTrue(hasNegativeA);
        assertTrue(hasNegativeB);

        hasNegativeA = false;
        hasNegativeB = false;
        a.fill(false);
        b.fill(false);
        assertNotEquals(a, b);

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertFalse(hasNegativeA);
        assertFalse(hasNegativeB);
    }

    @Test
    public void testFillBooleanRandom2() {
        Random rand1 = new Random(0);
        Random rand2 = new Random(0);
        Matrix a = new Matrix(5, 10);
        Matrix b = new Matrix(5, 10);
        assertEquals(a, b);
        a.fill(true, rand1);
        assertNotEquals(a, b);
        b.fill(true, rand2);
        assertEquals(a, b);

        boolean hasNegativeA = false;
        boolean hasNegativeB = false;

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertTrue(hasNegativeA);
        assertTrue(hasNegativeB);

        hasNegativeA = false;
        hasNegativeB = false;
        a.fill(false, rand1);
        b.fill(false, rand2);
        assertEquals(a, b);

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertFalse(hasNegativeA);
        assertFalse(hasNegativeB);
    }

    @Test
    public void testFillBooleanRandom3() {
        Random rand = new Random(0);
        Matrix a = new Matrix(5, 10);
        Matrix b = new Matrix(5, 10);
        assertEquals(a, b);
        a.fill(true, rand);
        assertNotEquals(a, b);
        b.fill(true, rand);
        assertNotEquals(a, b);
    }

    @Test
    public void testFillBooleanRandom4() {
        Matrix a = new Matrix(5, 10);
        Matrix b = new Matrix(5, 10);
        assertEquals(a, b);
        a.fill(true);
        assertNotEquals(a, b);
        b.fill(true);
        assertNotEquals(a, b);
    }

    @Test
    public void testFill() {
        Matrix a = new Matrix(5, 10);
        Matrix b = new Matrix(5, 10);
        assertEquals(a, b);
        a.fill();
        assertNotEquals(a, b);
        b.fill();
        assertNotEquals(a, b);

        boolean hasNegativeA = false;
        boolean hasNegativeB = false;

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                hasNegativeA |= a.position(i, j) < 0;
                hasNegativeB |= b.position(i, j) < 0;
            }
        }

        assertTrue(hasNegativeA);
        assertTrue(hasNegativeB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDotProductError1() {
        Matrix a = new Matrix(2, 10, a3_data);
        Matrix b = new Matrix(10, 2, b3_data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDotProductError2() {
        Matrix a = new Matrix(1, 10, a3_data);
        Matrix b = new Matrix(10, 2, b3_data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDotProductError3() {
        Matrix a = new Matrix(2, 10, a3_data);
        Matrix b = new Matrix(10, 1, b3_data);
    }

    @Test
    public void testDotProduct() {
        //product was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(1, 10, a3_data);
        Matrix b = new Matrix(1, 10, b3_data);
        double product = a.dotProduct(b);
        assertEquals(result, product, DELTA);
    }

    @Test
    public void testDotProduct1() {
        //product was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(10, 1, a3_data);
        Matrix b = new Matrix(10, 1, b3_data);
        double product = a.dotProduct(b);
        assertEquals(result, product, DELTA);
    }

    @Test
    public void testDotProduct2() {
        //product was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(1, 10, a3_data);
        Matrix b = new Matrix(10, 1, b3_data);
        double product = a.dotProduct(b);
        assertEquals(result, product, DELTA);
    }

    @Test
    public void testDotProduct3() {
        //product was calculated with GNU-Octave 4.0.2 (x86_64-pc-linux-gnu)
        Matrix a = new Matrix(10, 1, a3_data);
        Matrix b = new Matrix(1, 10, b3_data);
        double product = a.dotProduct(b);
        assertEquals(result, product, DELTA);
    }
    public double[] a3_data = {
        0.008836540313295,  0.005681073763455,  0.006258204252537, 0.008306752574195,  0.000227322631636,
       -0.002351919190952, -0.002525625167333, -0.006263386710295, 0.002164190369792, -0.006667604294500,
    };
    public double[] b3_data = {
         0.001482773049231, -0.001347142606673,  0.007705866269837,  0.009591078927415, -0.000875775794850,
        -0.007544329405404, -0.005619909996736, -0.001928699595623, -0.006332203264881, -0.001393287402837,
    };
    public double result = 1.7274931467510147E-4;

    @Test
    public void testSetGetRow() {
        Matrix a = Matrix.random(3, 5);
        double[] row = new double[]{1,2,3,4,5};
        assertArrayNotEquals(row, a.getRow(0), DELTA);
        assertArrayNotEquals(row, a.getRow(1), DELTA);
        assertArrayNotEquals(row, a.getRow(2), DELTA);
        a.setRow(2, row);
        assertArrayNotEquals(row, a.getRow(0), DELTA);
        assertArrayNotEquals(row, a.getRow(1), DELTA);
        assertArrayEquals   (row, a.getRow(2), DELTA);
    }

    @Test
    public void testGetCol() {
        Matrix a = Matrix.random(10, 15);
        assertArrayEquals(a.getCol(12), a.transpose().getRow(12), DELTA);
        for (int i = 0; i < 10; i++) {
            a.position(i, 9, 0);
        }
        assertArrayEquals(new double[10], a.getCol(9), DELTA);
        for (int i = 0; i < 10; i++) {
            a.position(i, 3, i);
        }
        assertArrayEquals(new double[]{0,1,2,3,4,5,6,7,8,9}, a.getCol(3), DELTA);
    }

    @Test
    public void testSubtractAndCopy() {
        Matrix a = Matrix.random(5, 10);
        Matrix b = Matrix.random(5, 10);
        Matrix c = Matrix.random(5, 10);
        Matrix d = Matrix.random(5, 10);
        assertNotEquals(a, d);
        a.subtractAndCopy(b, c, d);
        assertEquals(a, d);
        a.subtract(b, d);
        assertEquals(c, d);
        a.subtractAndCopy(a, b, c);
        assertEquals(a, c);
        assertEquals(new Matrix(5, 10), b);
    }

    @Test
    public void testMultiplyAndAdd() {
        Matrix a = Matrix.random(10, 20);
        Matrix b = Matrix.random(10, 20);
        Matrix c = new Matrix(10, 20);
        Matrix d = new Matrix(10, 20);
        assertNotEquals(a, b);
        assertNotEquals(a, c);
        a.multiplyAndAdd(0, b, c);
        assertEquals(b, c);
        double scale = Math.random();
        a.multiply(scale, d);
        d.add(b, d);
        a.multiplyAndAdd(scale, b, c);
        assertEquals(c, d);
    }

    @Test
    public void testApplyInIdentity() {
        Function f = new Function() {
            @Override
            public double eval(double x) {
                return 0;
            }
            @Override public Function getDerivate() {return null;}
        };
        Matrix a = Matrix.random(10, 10);
        Matrix b = new Matrix(10, 1);
        b.applyInIdentity(f, a);
        for (int i = 0; i < a.getRows(); i++) {
            assertEquals(0, a.position(i, i), DELTA);
        }
        b.setValue(5);
        f = new Function() {
            @Override
            public double eval(double x) {
                return x;
            }
            @Override public Function getDerivate() {return null;}
        };
        b.applyInIdentity(f, a);
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < a.getColumns(); j++) {
                if (i == j) {
                    assertEquals(5, a.position(i, j), DELTA);
                } else {
                    assertNotEquals(5, a.position(i, j), DELTA);
                }
            }
        }
    }

	@Test(expected = IllegalArgumentException.class)
	public void testDotProductThisNotRow() {
		Matrix a = Matrix.random(2, 10);
		Matrix b = Matrix.random(1, 20);
		a.dotProduct(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDotProductThisNotColumn() {
		Matrix a = Matrix.random(10, 2);
		Matrix b = Matrix.random(1, 20);
		a.dotProduct(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDotProductOtherNotRow() {
		Matrix a = Matrix.random(1, 20);
		Matrix b = Matrix.random(2, 10);
		a.dotProduct(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDotProductOtherNotColumn() {
		Matrix a = Matrix.random(1, 20);
		Matrix b = Matrix.random(10, 2);
		a.dotProduct(b);
	}

	@Test
	public void testHashCode() {
		Matrix a = Matrix.random(10, 10);
		Matrix b = Matrix.random(10, 10);
		Matrix c = Matrix.random(10, 5);
		Matrix d = Matrix.random( 5, 10);

		HashMap<Matrix, Matrix> map = new HashMap<>(4);
		map.put(a, a);
		map.put(b, b);
		map.put(c, c);
		map.put(d, d);

		assertTrue(a == map.get(a));
		assertTrue(b == map.get(b));
		assertTrue(c == map.get(c));
		assertTrue(d == map.get(d));
	}

	@Test
	public void testHashCode2() {
		Matrix a = Matrix.random(10, 10);
		assertEquals(a.hashCode(), a.hashCode());
		Matrix b = new Matrix(10, 10);
		a.copy(b);
		assertEquals(a.hashCode(), b.hashCode());
		b.position(0, 0, b.position(0, 0) + 1);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void testToString() {
		Matrix a = new Matrix(2, 1, true);
		assertEquals("1.00000000 \n0.00000000 \n\n", a.toString());
		a = new Matrix(1, 2, true);
		assertEquals("1.00000000 0.00000000 \n\n", a.toString());
	}

    private void assertArrayNotEquals(double[] a, double[] b, double DELTA) {
        try {
            assertArrayEquals(a, b, DELTA);
            throw new AssertionError("Both arrays are equal!");
        } catch (AssertionError ignoreMe) {}
    }
}

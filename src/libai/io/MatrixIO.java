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
package libai.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import libai.common.Matrix;

/**
 * This class implements basic IO functions for {@link Matrix} objects.
 * 
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class MatrixIO {
    /**
     * Serialization target
     */ 
    public static enum Target {
        /**
         * Saves the matrix using the JVM serialization algorithm.
         */
        SERIAL,
        /**
         * Saves the matrix in {@code csv} (comma separated values) format.
         */
        CSV,
        /**
         * Saves the matrix in {@code tsv} (tab separated values) format.
         */
        TSV,
        /**
         * Saves the matrix in GNU Octave's binary ({@code mat}) format.
         * <p>The specification for Octave's {@code mat} format can be found a bit
         * <a href="http://octave.1599824.n4.nabble.com/Octave-binary-format-td1607907.html"> here
         * </a> and another bit 
         * <a href="http://octave.org/doxygen/4.0/de/d2d/ls-oct-binary_8cc_source.html"> here</a>
         * and yet another bit
         * <a href="https://lists.gnu.org/archive/html/help-octave/1995-03/msg00056.html">here</a>
         * </p><p>About this implementation:</p><ul>
         * <li>GNU Octave supports different matrix types (diagonal matrix, sparse 
         * matrix, etc), this method will always output a full/dense matrix format. 
         * You will be able to read it with GNU Octave, but there's a chance that if 
         * you <i>re-save</i> the matrix using GNU Octave the file won't be the same, 
         * since {@code libai} won't support the special cases.</li>
         * <li>Even though Java's default endianness is {@link ByteOrder#BIG_ENDIAN}, 
         * this matrices are saved with {@link ByteOrder#LITTLE_ENDIAN}.</li>
         * <li>This matrices will NOT be Matlab&reg; compatible (never).</li>
         * <li>Files will not be {@code gzipped} since not all versions of GNU Octave 
         * support it</li></ul>
         */
        OCTAVE
    }
    
    /**
     * Writes a {@link Matrix} object to a given {@link OutputStream}.<br><br>
     * <i>Note:</i> if the target is {@link Target#OCTAVE} then the default variable name will be
     * {@code 'a'}.
     * 
     * @param output The {@link OutputStream} in which to save this {@code Matrix} object
     * @param m The {@link Matrix} object to write
     * @param t {@link Target}ed format, the default value (if {@code null}) is
     * {@link Target#SERIAL}
     * @throws IllegalArgumentException if either {@code output} or {@code m} are {@code null}
     * @throws IOException if an I/O error occurs
     */
    public static void write(OutputStream output, 
                             Matrix m, Target t) throws IllegalArgumentException, IOException {
        if (m == null) {
            throw new IllegalArgumentException("The matrix can't be null");
        }
        
        HashMap<String, Matrix> map = new HashMap<>(1);
        map.put("a", m);
        
        write(output, map, t);
    }
    
    /**
     * Writes a set of {@link Matrix} objects to a given {@link OutputStream}.<br><br>
     * <i>Note:</i> When saving to {@link Target#CSV} and {@link Target#TSV}, a line separator 
     * {@literal \n} will be inserted between matrices
     * <i>Note 2:</i> Since Java doesn't support deserializing multiple objects from the same 
     * {@link OutputStream} if target is {@link Target#SERIAL} then the whole {@code Map} will
     * be serialized.
     * 
     * @param output The {@link OutputStream} in which to write the {@code Matrix} objects
     * @param m a {@link Map} where the keys are matrix names, if the target is anything but 
     * {@link Target#OCTAVE} the name will be ignored
     * @param t {@link Target}ed format, the default value (if {@code null}) is
     * {@link Target#SERIAL}
     * @throws IllegalArgumentException if either {@code output} or {@code m} are {@code null}
     * @throws IOException if an I/O error occurs
     */
    public static void write(OutputStream output,
                             Map<String, Matrix> m,
                             Target t) throws IllegalArgumentException, IOException {
        if (output == null) {
            throw new IllegalArgumentException("OutputStream can't be null");
        }
        if (m == null) {
            throw new IllegalArgumentException("The matrix map can't be null");
        }
        if (m.isEmpty()) {
            return;
        }
        
        t = t == null ? Target.SERIAL : t;
        
        switch(t) {
            case CSV:    writeText(output, m, ","); break;
            case TSV:    writeText(output, m, "\t"); break;
            case OCTAVE: writeOctave(output, m); break;
            case SERIAL:
            default:     writeSerial(output, m);
        }
    }
    
    private static void writeSerial(OutputStream output, Map<String, Matrix> m) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(output);
        if (m.size() == 1) {
            oos.writeObject(m.values().iterator().next());
        } else {
            oos.writeObject(m);
        }
    }
    
    private static void writeText(OutputStream output, Map<String, Matrix> m, String sep) throws IOException {
        PrintStream ps = new PrintStream(output);
        int k = 0;
        for (Matrix matrix : m.values()) {
            for (int i = 0, r = matrix.getRows(); i < r;i++) {
                for (int j = 0, c = matrix.getColumns(); j < c; j++) {
                    ps.append(Double.toString(matrix.position(i, j)));
                    if (j != c - 1) ps.append(sep);
                }
                if (i != r - 1) ps.append("\n");
            }
            if (++k != m.size()) ps.append("\n");
        }
    }
    
    private static void writeOctave(OutputStream os, Map<String, Matrix> m) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(11); // Always 11 bytes
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.put("Octave-1-L".getBytes(StandardCharsets.ISO_8859_1)); // Magic number
        header.put((byte)0); // 64-bit floats
        os.write(header.array());
		
        for (Map.Entry<String, Matrix> en : m.entrySet()) {
            String name = en.getKey();
            Matrix matrix = en.getValue();
            
            final int dLen = 20 + name.length();
            ByteBuffer data = ByteBuffer.allocate(dLen);
            data.order(ByteOrder.LITTLE_ENDIAN);
            data.putInt(name.length()); // variable name length
            data.put(name.getBytes(StandardCharsets.US_ASCII)); // variable name
            data.putInt(0); //no doc 
            data.put((byte)1); //global matrix
            data.put((byte)0xff); //data type (always 255)
            data.putInt("matrix".length()); //type_length
            data.put("matrix".getBytes(StandardCharsets.US_ASCII)); //type
            os.write(data.array());

            data = ByteBuffer.allocate(1 + 4 + 4 + 4 + 8 * matrix.getRows() * matrix.getColumns());
            data.order(ByteOrder.LITTLE_ENDIAN);
            data.putInt(0xfffffffe); // <-- I honestly can't say what is this... 
                                     //     it resulted after extensive hex dumping
                                     //     of mat files...
            data.putInt(matrix.getRows());
            data.putInt(matrix.getColumns());
            data.put((byte)0x07);    // data start (I think... see comment above)

            // Octave uses column based storage
            for (int j = 0, c = matrix.getColumns(); j < c; j++) {
                for (int i = 0, r = matrix.getRows(); i < r; i++) {
                    data.putDouble(matrix.position(i, j));
                }
            }

            os.write(data.array());
        }
    }
    
}

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

import libai.io.MatrixIO;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class MatrixIOTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void testNull1() throws IOException {
        MatrixIO.write(null, new Matrix(1, 1), MatrixIO.Target.SERIAL);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNull2() throws IOException {
        MatrixIO.write(null, (Matrix)null, MatrixIO.Target.SERIAL);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNull3() throws IOException {
        MatrixIO.write(new ByteArrayOutputStream(), (Matrix)null, MatrixIO.Target.SERIAL);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNull4() throws IOException {
        MatrixIO.write(new ByteArrayOutputStream(), (Map<String,Matrix>)null, MatrixIO.Target.SERIAL);
    }
    
    @Test
    public void testWriteTargetSerial() throws Exception {
        Matrix a = Matrix.random(10, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, a, MatrixIO.Target.SERIAL);
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Matrix b = (Matrix)ois.readObject();
            assertEquals(a, b);
        }
    }
    
    @Test
    public void testWriteTargetSerial2() throws Exception {
        Matrix a = Matrix.random(10, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, a);
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Matrix b = (Matrix)ois.readObject();
            assertEquals(a, b);
        }
    }
    
    @Test
    public void testWriteTargetSerial4() throws Exception {
        Matrix a = Matrix.random(15, 20);
        Matrix b = Matrix.random(25, 20);
        Map<String, Matrix> data = new HashMap<>(2);
        data.put("a", a);
        data.put("b", b);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        MatrixIO.write(baos, data);
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Map<String, Matrix> read = (Map<String, Matrix>)ois.readObject();
            assertEquals(a, read.get("a"));
            assertEquals(b, read.get("b"));
        }
    }
    
    @Test
    public void testWriteTargetCSV() throws Exception {
        Matrix a = new Matrix(2, 2, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, a, MatrixIO.Target.CSV);
        assertEquals("1.0,0.0\n0.0,1.0", new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }
    
    @Test
    public void testWriteTargetCSV2() throws Exception {
        Map<String, Matrix> data = new HashMap<>(2);
        data.put("a", new Matrix(2, 2, true));
        data.put("b", new Matrix(2, 2, new double[]{0,1,1,0}));
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, data, MatrixIO.Target.CSV);
        assertEquals("1.0,0.0\n0.0,1.0\n0.0,1.0\n1.0,0.0", new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }
    
    @Test
    public void testWriteTargetTSV() throws Exception {
        Matrix a = new Matrix(2, 2, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, a, MatrixIO.Target.TSV);
        assertEquals("1.0\t0.0\n0.0\t1.0", new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }
    
    @Test
    public void testWriteTargetTSV2() throws Exception {
        Map<String, Matrix> data = new HashMap<>(2);
        data.put("a", new Matrix(2,2,true));
        data.put("b", new Matrix(2, 2, new double[]{0,1,1,0}));
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        MatrixIO.write(baos, data, MatrixIO.Target.TSV);
        assertEquals("1.0\t0.0\n0.0\t1.0\n0.0\t1.0\n1.0\t0.0", new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }
    
    @Test
    public void testWriteTargetOctave() {
        assumeTrue("Can't use temp dir...", checkTemp());
        assumeTrue("Can't find Octave...", checkOctaveInstall());
        
        String tmp = System.getProperty("java.io.tmpdir") + File.separator;
        
        Matrix a = Matrix.random(10, 20);
        Matrix b = Matrix.random(20, 10);
        Matrix c = new Matrix(10, 10);
        a.multiply(b, c);
        
        try (OutputStream os = new FileOutputStream(tmp + "a.mat")) {
            MatrixIO.write(os, a, MatrixIO.Target.OCTAVE);
        } catch (Exception e) {}
        
        try (OutputStream os = new FileOutputStream(tmp + "b.mat")) {
            Map<String, Matrix> mat = new HashMap<>(1);
            mat.put("b", b);
            MatrixIO.write(os, mat, MatrixIO.Target.OCTAVE);
        } catch (Exception e) {}
        
        try (OutputStream os = new FileOutputStream(tmp + "c.mat")) {
            Map<String, Matrix> mat = new HashMap<>(1);
            mat.put("c", c);
            MatrixIO.write(os, mat, MatrixIO.Target.OCTAVE);
        } catch (Exception e) {}
        
        assertEquals("10", eval("load " + tmp + "a.mat; rows(a)"));
        assertEquals("20", eval("load " + tmp + "a.mat; columns(a)"));
        assertEquals("20", eval("load " + tmp + "b.mat; rows(b)"));
        assertEquals("10", eval("load " + tmp + "b.mat; columns(b)"));
        assertEquals("10", eval("load " + tmp + "c.mat; rows(c)"));
        assertEquals("10", eval("load " + tmp + "c.mat; columns(c)"));
        //Octave usually rounds numbers when stdouting, this is the cleanest way
        //I could came up to test if values were correctly written/read...
        assertEquals("0" , eval(
                "load " + tmp + "a.mat;" +
                "load " + tmp + "b.mat;" +
                "load " + tmp + "c.mat;" +
                "sum((a * b - c > 1e-12)(:))")
        );
        
        new File(tmp + "a.mat").delete();
        new File(tmp + "b.mat").delete();
        new File(tmp + "c.mat").delete();
    }
    
    @Test
    public void testWriteTargetOctave2() {
        assumeTrue("Can't use temp dir...", checkTemp());
        assumeTrue("Can't find Octave...", checkOctaveInstall());
        
        String tmp = System.getProperty("java.io.tmpdir") + File.separator;
        File matFile = new File(tmp + "foo.mat");
        
        Matrix a = Matrix.random(10, 20);
        Matrix b = Matrix.random(20, 10);
        Matrix c = new Matrix(10, 20);
        a.add(b.transpose(), c); // c = a + b'
        
        try (OutputStream os = new FileOutputStream(matFile)) {
            Map<String, Matrix> mat = new HashMap<>(3);
            mat.put("a", a);
            mat.put("b", b);
            mat.put("c", c);
            MatrixIO.write(os, mat, MatrixIO.Target.OCTAVE);
        } catch (Exception e) {}
        
        assertEquals("10", eval("load " + tmp + "foo.mat; rows(a)"));
        assertEquals("20", eval("load " + tmp + "foo.mat; columns(a)"));
        assertEquals("20", eval("load " + tmp + "foo.mat; rows(b)"));
        assertEquals("10", eval("load " + tmp + "foo.mat; columns(b)"));
        assertEquals("10", eval("load " + tmp + "foo.mat; rows(c)"));
        assertEquals("20", eval("load " + tmp + "foo.mat; columns(c)"));
        //Octave usually rounds numbers when stdouting, this is the cleanest way
        //I could came up to test if values were correctly written/read...
        assertEquals("0" , eval("load " + tmp + "foo.mat; sum((a + b' != c)(:))"));
        
        new File(tmp + "foo.mat").delete();
    }
	
	@Test 
	public void testOpenOfficeFormat(){
		double[] values = new double[]{1,2,3,4,5,6,7,8,9};
		Matrix m = new Matrix(3, 3, values);
		try(ByteArrayOutputStream output = new ByteArrayOutputStream()){
			MatrixIO.write(output, m, MatrixIO.Target.OPENOFFICE);
			assertEquals(output.toString("US-ASCII"), "a: \nleft [ matrix{1.0 # 2.0 # 3.0 ## 4.0 # 5.0 # 6.0 ## 7.0 # 8.0 # 9.0} right ]newLine\n");
		}catch(Exception e){
			fail("An unexpected IO error has occurred");
		}
	}
    
    public static String eval(String expr) {
        try {
            Process process = new ProcessBuilder().command(
                    "octave",
                    "--path", System.getProperty("java.io.tmpdir"),
                    "--eval", expr
            ).start();
            
            try (InputStreamReader isr = new InputStreamReader(process.getInputStream());
                 BufferedReader ov =  new BufferedReader(isr)) {
                String line, output = "";
                
                while ((line = ov.readLine()) != null) {
                    output = line;
                }
                
                return output.replace("ans = ", "").trim();
            }
        } catch (Exception e) {} 
        return null;
    }
    
    public static boolean checkTemp() {
        File temp = new File(System.getProperty("java.io.tmpdir"));
        return temp.exists() && temp.canWrite() && temp.canRead();
    }
    
    public static boolean checkOctaveInstall() {    
        boolean isInstalled = false;  
        
        try {
            Process process = new ProcessBuilder("octave","-version").start();
            try (InputStreamReader isr = new InputStreamReader(process.getInputStream());
                 BufferedReader ov =  new BufferedReader(isr)) {
                String line;

                while ((line = ov.readLine()) != null) {
                    //Since every OS has it's own term for "Command not found" this seemed like a 
                    //reasonable solution
                    isInstalled |= line.contains("John W. Eaton");
                }
            }
        } catch (Exception e) {} 
        
        return isInstalled;
    }
}

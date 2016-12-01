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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class SaveAsOctaveTest {
    
    @Test
    public void testSaveAsOctaveBin() {
        assumeTrue("Can't use temp dir...", checkTemp());
        assumeTrue("Can't find Octave...", checkOctaveInstall());
        
        String tmp = System.getProperty("java.io.tmpdir") + File.separator;
        
        Matrix a = Matrix.random(10, 20);
        Matrix b = Matrix.random(20, 10);
        Matrix c = new Matrix(10, 10);
        a.multiply(b, c);
        
        try (OutputStream os = new FileOutputStream(tmp + "a.mat")) {
            a.saveAsOctaveBin(os, "a", true);
        } catch (Exception e) {}
        
        try (OutputStream os = new FileOutputStream(tmp + "b.mat")) {
            b.saveAsOctaveBin(os, "b", true);
        } catch (Exception e) {}
        
        try (OutputStream os = new FileOutputStream(tmp + "c.mat")) {
            c.saveAsOctaveBin(os, "c", true);
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
    public void testSaveAsOctaveBin2() {
        assumeTrue("Can't use temp dir...", checkTemp());
        assumeTrue("Can't find Octave...", checkOctaveInstall());
        
        String tmp = System.getProperty("java.io.tmpdir") + File.separator;
        File matFile = new File(tmp + "foo.mat");
        
        Matrix a = Matrix.random(10, 20);
        Matrix b = Matrix.random(20, 10);
        Matrix c = new Matrix(10, 20);
        a.add(b.transpose(), c); // c = a + b'
        
        try (OutputStream os = new FileOutputStream(matFile)) {
            a.saveAsOctaveBin(os, "a", true);
            b.saveAsOctaveBin(os, "b", false);
            c.saveAsOctaveBin(os, "c", false);
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
    
    private String eval(String expr) {
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
    
    private boolean checkTemp() {
        File temp = new File(System.getProperty("java.io.tmpdir"));
        return temp.exists() && temp.canWrite() && temp.canRead();
    }
    
    private boolean checkOctaveInstall() {    
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

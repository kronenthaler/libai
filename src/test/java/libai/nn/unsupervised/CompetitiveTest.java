/*
 * MIT License
 *
 * Copyright (c) 2017 Federico Vera <https://github.com/dktcoding>
 *
 * Permission is hereby granted, free of charge, to any person obtaining ada copy
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
package libai.nn.unsupervised;

import demos.common.SimpleProgressDisplay;
import java.util.Random;
import javax.swing.JProgressBar;
import libai.common.Matrix;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Federico Vera {@literal <dktcoding [at] gmail>}
 */
public class CompetitiveTest {
	
	@Test
	public void testDemo() {
		int n = 6;
		int m = 2;
		int l = 3;
		Matrix[] patterns = new Matrix[n];
		Matrix[] ans = new Matrix[n];
		for (int i = 0; i < n; i++) {
			patterns[i] = new Matrix(m, 1);
			ans[i] = new Matrix(l, 1);
			ans[i].setValue(0);
		}

		patterns[0].position(0, 0, -1);
		patterns[0].position(1, 0, 6);

		patterns[1].position(0, 0, 1);
		patterns[1].position(1, 0, 6);

		patterns[2].position(0, 0, 6);
		patterns[2].position(1, 0, 2);

		patterns[3].position(0, 0, 6);
		patterns[3].position(1, 0, -2);

		patterns[4].position(0, 0, -5);
		patterns[4].position(1, 0, -3);

		patterns[5].position(0, 0, -3);
		patterns[5].position(1, 0, -5);

		ans[0].position(0, 0, 1);
		ans[1].position(0, 0, 1);
		ans[2].position(1, 0, 1);
		ans[3].position(1, 0, 1);
		ans[4].position(2, 0, 1);
		ans[5].position(2, 0, 1);

		int magicRand = 4; //Results are perfect
		Competitive net = new Competitive(m, l, new Random(magicRand));
		net.setProgressBar(new SimpleProgressDisplay(new JProgressBar()));
		net.train(patterns, ans, 0.01, 1000);

		for (int i = 0; i < patterns.length; i++) {
			for (int j = 0; j < ans[i].getRows(); j++){
			    assertEquals(ans[i].position(j, 0), net.simulate(patterns[i]).position(j, 0), 1e-8);
			}
		}
		
	}
}
/*
1.4714045207910316
*/
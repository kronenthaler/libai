/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
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
package demos.nn;

import demos.common.SimpleProgressDisplay;
import libai.common.Matrix;
import libai.common.kernels.LinearKernel;
import libai.nn.NeuralNetwork;
import libai.nn.supervised.SVM;

import java.util.Random;


/**
 *
 * @author kronenthaler
 */
public class SVMPanel extends javax.swing.JPanel {
	/**
	 * Creates new form SVMPanel
	 */
	public SVMPanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();

        jTextPane1.setText("Train a SVM network to learn the the classes upper and lower 2x+3 (as +1 and -1 respectively). The kernel used is a Linear kernel.");
        jScrollPane1.setViewportView(jTextPane1);

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		jTextPane1.setText("");
		new Thread(new Runnable() {
			@Override
			public void run() {
				int n = 100;
				int t = 40;

				Matrix[] patterns = new Matrix[n + t];
				Matrix[] ans = new Matrix[n + t];

				Random r = new Random();
				for (int i = 0; i < n; i++) {
					int inc = r.nextInt(10);
					patterns[i] = new Matrix(2, 1, new double[]{i + 1, (2 * (i + 1)) + 3 + Math.pow(-1, inc) * inc});
					ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
				}

				for (int i = n; i < n + t; i++) {
					int inc = r.nextInt(10);
					patterns[i] = new Matrix(2, 1, new double[]{i + 1.33, (2 * (i + 1.33)) + 3 + Math.pow(-1, inc) * inc});
					ans[i] = new Matrix(1, 1, new double[]{inc % 2 == 0 ? +1 : -1});
				}

				NeuralNetwork net = new SVM(new LinearKernel());
				net.setProgressBar(new SimpleProgressDisplay(jProgressBar1));
				net.train(patterns, ans, 0.001, 1000, 0, n);

				jTextPane1.setText(jTextPane1.getText() + "Error for training set: " + net.error(patterns, ans, 0, n));
				jTextPane1.setText(jTextPane1.getText() + "\nError for test set: " + net.error(patterns, ans, n, t));

				jTextPane1.setText(jTextPane1.getText() + "\n\nValues for the test set:");
				for (int i = n; i < patterns.length; i++) {
					jTextPane1.setText(jTextPane1.getText() + "\nexp: " + ans[i].position(0, 0) + " vs " + net.simulate(patterns[i]).position(0, 0));
				}
			}
		}).start();
	}//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}

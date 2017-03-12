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
import libai.common.matrix.Matrix;
import libai.common.functions.Function;
import libai.common.functions.Identity;
import libai.common.functions.Sigmoid;
import libai.nn.supervised.MultiLayerPerceptron;
import libai.nn.supervised.backpropagation.Backpropagation;
import libai.nn.supervised.backpropagation.MomentumBackpropagation;
import libai.nn.supervised.backpropagation.ResilientBackpropagation;
import libai.nn.supervised.backpropagation.StandardBackpropagation;

/**
 *
 * @author kronenthaler
 */
public class MPLPanel extends javax.swing.JPanel {
	static double f(double x) {
		return Math.sin(x) + Math.cos(x);
	}

	/**
	 * Creates new form MPLPPanel
	 */
	public MPLPanel() {
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

        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        algorithmType = new javax.swing.JComboBox();

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextPane1.setText("Train a MultiLayerPerceptron network to learn the equation: sin(x) + cos(x) for x in [1, 41) using a spacing of 0.1 for training and 0.33 for test. The network has 3 layers of 1, 4 and 1 neurons and functions, identity, sigmoid and identity respectively  ");
        jScrollPane1.setViewportView(jTextPane1);

        algorithmType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard Backpropagation", "Momentum Backpropagation", "Resilent Backpropagation" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(algorithmType, 0, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(algorithmType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
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
				int n = 40;
				int m = 1;
				int l = 1;
				int test = 12;
				Matrix[] p = new Matrix[n + test];
				Matrix[] t = new Matrix[n + test];
				double delta = 0.1;
				double x = 0;
				for (int i = 0; i < n; i++, x += delta) {
					p[i] = new Matrix(m, 1);
					t[i] = new Matrix(l, 1);

					p[i].position(0, 0, x);
					t[i].position(0, 0, f(x));
				}

				delta = 0.33;
				x = 0;
				for (int i = n; i < n + test && x < 4; i++, x += delta) {
					p[i] = new Matrix(m, 1);
					t[i] = new Matrix(l, 1);

					p[i].position(0, 0, x);
					t[i].position(0, 0, f(x));
				}

				int nperlayer[] = {m, 4, l};
				Backpropagation trainer;

				if (algorithmType.getSelectedIndex() == 0) {
					trainer = new StandardBackpropagation();
				} else if (algorithmType.getSelectedIndex() == 1) {
					trainer = new MomentumBackpropagation(0.4);
				} else {
					trainer = new ResilientBackpropagation();
				}

				MultiLayerPerceptron net = new MultiLayerPerceptron(nperlayer,
						new Function[]{new Identity(), new Sigmoid(), new Identity()});

				net.setProgressBar(new SimpleProgressDisplay(jProgressBar1));
				long start = System.currentTimeMillis();
				net.train(p, t, 0.2, 50000, 0, n);
				long end = System.currentTimeMillis() - start;

				jTextPane1.setText(jTextPane1.getText() + algorithmType.getSelectedItem() + "\n");
				jTextPane1.setText(jTextPane1.getText() + "Time taked: " + (end / 1000) + " sec.\n");
				jTextPane1.setText(jTextPane1.getText() + "Error for training set: " + net.error(p, t, 0, n));
				jTextPane1.setText(jTextPane1.getText() + "\nError for test set: " + net.error(p, t, n, test));

				jTextPane1.setText(jTextPane1.getText() + "\n\nValues for the test set:");
				for (int i = n; i < p.length; i++) {
					jTextPane1.setText(jTextPane1.getText() + "\nexp: " + t[i].position(0, 0) + " vs " + net.simulate(p[i]).position(0, 0));
				}
			}
		}).start();
	}//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox algorithmType;
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}

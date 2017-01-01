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

import libai.nn.unsupervised.Competitive;
import libai.common.Matrix;
import demos.common.SimpleProgressDisplay;

/**
 *
 * @author kronenthaler
 */
public class CompetitivePanel extends javax.swing.JPanel {
	static double f(double x) {
		return Math.sin(x) + Math.cos(x);
	}

	/**
	 * Creates new form MPLPPanel
	 */
	public CompetitivePanel() {
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

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextPane1.setText("Train a Competitive network to learn the points: (-1,6), (1,6) as class 1, (6,2), (6,-2) as class 2 and (-3,-5), (-5,-3) as class 3. Notice that the first 2 patterns must have the same class, regardless the expected answer, the same apply to the next two and for the last two.");
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
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

				Competitive net = new Competitive(m, l);
				net.setProgressBar(new SimpleProgressDisplay(jProgressBar1));
				net.train(patterns, ans, 0.1, 10000);

				jTextPane1.setText(jTextPane1.getText() + "Error for training set: " + net.error(patterns, ans, 0, n));
				//jTextPane1.setText(jTextPane1.getText()+"\nError for test set: "+net.error(patterns, ans, n,test));

				jTextPane1.setText(jTextPane1.getText() + "\n\nValues for the test set:");
				for (int i = 0; i < patterns.length; i++) {
					jTextPane1.setText(jTextPane1.getText() + "\nexp:\n");
					for (int j = 0; j < ans[i].getRows(); j++)
						jTextPane1.setText(jTextPane1.getText() + ans[i].position(j, 0) + " vs " + net.simulate(patterns[i]).position(j, 0) + "\n");
					jTextPane1.setText(jTextPane1.getText() + "---\n");
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
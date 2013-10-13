package demos.nn;

import libai.nn.supervised.Adaline;
import libai.nn.NeuralNetwork;
import libai.common.Matrix;
import demos.common.SimpleProgressDisplay;

/**
 *
 * @author kronenthaler
 */
public class PerceptronPanel extends javax.swing.JPanel {
	/**
	 * Creates new form PerceptronPanel
	 */
	public PerceptronPanel() {
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

        jTextPane1.setText("Train an Adaline network to learn the equation: 2x+3 for x in [1, 41) using a spacing of 1 for training and 1.33 for test.  ");
        jScrollPane1.setViewportView(jTextPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		jTextPane1.setText("");
		new Thread(new Runnable() {
			public void run() {
				int n = 40;
				int t = 10;

				Matrix[] patterns = new Matrix[n + t];
				Matrix[] ans = new Matrix[n + t];

				for (int i = 0; i < n; i++) {
					patterns[i] = new Matrix(1, 1, new double[]{i + 1});
					ans[i] = new Matrix(1, 1, new double[]{(2 * (i + 1)) + 3});
				}

				for (int i = n; i < n + t; i++) {
					patterns[i] = new Matrix(1, 1, new double[]{i + 1.33});
					ans[i] = new Matrix(1, 1, new double[]{(2 * (i + 1.33)) + 3});
				}

				NeuralNetwork net = new Adaline(1, 1);
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

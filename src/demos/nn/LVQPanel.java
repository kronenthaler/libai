package demos.nn;

import libai.nn.unsupervised.Competitive;
import libai.nn.supervised.LVQ;
import libai.common.Matrix;
import demos.common.SimpleProgressDisplay;

/**
 *
 * @author kronenthaler
 */
public class LVQPanel extends javax.swing.JPanel {
    static double f(double x) {
        return Math.sin(x) + Math.cos(x);
    }

    /**
     * Creates new form MPLPPanel
     */
    public LVQPanel() {
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

        jTextPane1.setText("Train an LVQ network to learn the points: (-1,6), (1,6) class (1 0 0), (6,2), (6,-2) class (0 1 0) and (-3,-5), (-5,-3) class (0 0 1)");
        jScrollPane1.setViewportView(jTextPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
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

                Competitive net = new LVQ(m, 2, l);
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

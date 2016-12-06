package demos.nn;

import libai.nn.unsupervised.Hopfield;
import libai.common.Matrix;
import demos.common.SimpleProgressDisplay;
import javax.swing.JCheckBox;

/**
 * TODO: work on it.
 *
 * @author kronenthaler
 */
public class HopfieldPanel extends javax.swing.JPanel {
	Hopfield net;

	/**
	 * Creates new form HebbPanel
	 */
	public HopfieldPanel() {
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
        p0 = new javax.swing.JCheckBox();
        p1 = new javax.swing.JCheckBox();
        p2 = new javax.swing.JCheckBox();
        p3 = new javax.swing.JCheckBox();
        p4 = new javax.swing.JCheckBox();
        p5 = new javax.swing.JCheckBox();
        p6 = new javax.swing.JCheckBox();
        p7 = new javax.swing.JCheckBox();
        p8 = new javax.swing.JCheckBox();
        p9 = new javax.swing.JCheckBox();
        p10 = new javax.swing.JCheckBox();
        p11 = new javax.swing.JCheckBox();
        p12 = new javax.swing.JCheckBox();
        p13 = new javax.swing.JCheckBox();
        p14 = new javax.swing.JCheckBox();
        p15 = new javax.swing.JCheckBox();
        p16 = new javax.swing.JCheckBox();
        p17 = new javax.swing.JCheckBox();
        p18 = new javax.swing.JCheckBox();
        p19 = new javax.swing.JCheckBox();
        p20 = new javax.swing.JCheckBox();
        p21 = new javax.swing.JCheckBox();
        p22 = new javax.swing.JCheckBox();
        p23 = new javax.swing.JCheckBox();
        p24 = new javax.swing.JCheckBox();
        testBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        p25 = new javax.swing.JCheckBox();
        p26 = new javax.swing.JCheckBox();
        p27 = new javax.swing.JCheckBox();
        p28 = new javax.swing.JCheckBox();
        p29 = new javax.swing.JCheckBox();
        p30 = new javax.swing.JCheckBox();
        p31 = new javax.swing.JCheckBox();
        p32 = new javax.swing.JCheckBox();
        p33 = new javax.swing.JCheckBox();
        p34 = new javax.swing.JCheckBox();
        p35 = new javax.swing.JCheckBox();
        p36 = new javax.swing.JCheckBox();
        p37 = new javax.swing.JCheckBox();
        p38 = new javax.swing.JCheckBox();
        p39 = new javax.swing.JCheckBox();
        p40 = new javax.swing.JCheckBox();
        p41 = new javax.swing.JCheckBox();
        p42 = new javax.swing.JCheckBox();
        p43 = new javax.swing.JCheckBox();
        p44 = new javax.swing.JCheckBox();
        p45 = new javax.swing.JCheckBox();
        p46 = new javax.swing.JCheckBox();
        p47 = new javax.swing.JCheckBox();
        p48 = new javax.swing.JCheckBox();
        p49 = new javax.swing.JCheckBox();

        jTextPane1.setText("Train a Hopfield network to learn 2 patterns representing the images of the sign + and -. \nThe test consist on create a modified, noise perturbed image, and retreive the original pattern as result.");
        jScrollPane1.setViewportView(jTextPane1);

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        testBtn.setText("Retrieve");
        testBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Query:");

        jLabel2.setText("Result:");

        p25.setEnabled(false);

        p26.setEnabled(false);

        p27.setEnabled(false);

        p28.setEnabled(false);

        p29.setEnabled(false);

        p30.setEnabled(false);

        p31.setEnabled(false);

        p32.setEnabled(false);

        p33.setEnabled(false);

        p34.setEnabled(false);

        p35.setEnabled(false);

        p36.setEnabled(false);

        p37.setEnabled(false);

        p38.setEnabled(false);

        p39.setEnabled(false);

        p40.setEnabled(false);

        p41.setEnabled(false);

        p42.setEnabled(false);

        p43.setEnabled(false);

        p44.setEnabled(false);

        p45.setEnabled(false);

        p46.setEnabled(false);

        p47.setEnabled(false);

        p48.setEnabled(false);

        p49.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(p0)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p4))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(layout.createSequentialGroup()
                                    .add(p15)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p16)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p17)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p18)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p19))
                                .add(layout.createSequentialGroup()
                                    .add(p5)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p6)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p7)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p8)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(p9)))
                            .add(jLabel1)
                            .add(layout.createSequentialGroup()
                                .add(p10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p13)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p14))
                            .add(layout.createSequentialGroup()
                                .add(p20)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p21)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p22)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p23)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p24)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(p25)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p26)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p27)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p28)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p29))
                            .add(layout.createSequentialGroup()
                                .add(p35)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p36)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p37)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p38)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p39))
                            .add(layout.createSequentialGroup()
                                .add(p30)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p31)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p32)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p33)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(p34))
                            .add(jLabel2)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(p40)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p41)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p42)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p43)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p44))
                                    .add(layout.createSequentialGroup()
                                        .add(p45)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p46)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p47)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p48)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(p49)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(testBtn)))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton1)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p0)
                                    .add(p1)
                                    .add(p2)
                                    .add(p3)
                                    .add(p4))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p5)
                                    .add(p6)
                                    .add(p7)
                                    .add(p8)
                                    .add(p9))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(p10)
                                    .add(p11)
                                    .add(p12)
                                    .add(p13)
                                    .add(p14))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(p15)
                                    .add(p16)
                                    .add(p17)
                                    .add(p18)
                                    .add(p19))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p20)
                                    .add(p21)
                                    .add(p22)
                                    .add(p23)
                                    .add(p24)))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p25)
                                    .add(p26)
                                    .add(p27)
                                    .add(p28)
                                    .add(p29))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p30)
                                    .add(p31)
                                    .add(p32)
                                    .add(p33)
                                    .add(p34))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(p35)
                                    .add(p36)
                                    .add(p37)
                                    .add(p38)
                                    .add(p39))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(p40)
                                    .add(p41)
                                    .add(p42)
                                    .add(p43)
                                    .add(p44))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(p45)
                                    .add(p46)
                                    .add(p47)
                                    .add(p48)
                                    .add(p49)))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, testBtn))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(6, 6, 6)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		jTextPane1.setText("");
		new Thread(new Runnable() {
			@Override
			public void run() {
				int n = 2;
				int m = 25;
				int l = 3;
				Matrix[] patterns = new Matrix[n];
				Matrix[] ans = patterns;
				for (int i = 0; i < n; i++) {
					patterns[i] = new Matrix(m, 1);
					patterns[i].setValue(-1);
					for (int j = 0; j < m; j++) {
						int row = j / 5;
						int col = j % 5;

						if (i == 0) {
							if ((row == 2 || col == 2 /*|| row == 4 || col == 4*/)) {
								patterns[i].position(j, 0, 1);
							}
						} else {
							if (row == 2) {
								patterns[i].position(j, 0, 1);
							}
						}
					}
					System.out.println(patterns[i]);
				}

				net = new Hopfield(25);
				net.setProgressBar(new SimpleProgressDisplay(jProgressBar1));
				net.train(patterns, patterns, 1, 80);

				jTextPane1.setText(jTextPane1.getText() + "Error for training set: " + net.error(patterns, patterns, 0, n));
				//jTextPane1.setText(jTextPane1.getText()+"\nError for test set: "+net.error(patterns, ans, n,test));

				jTextPane1.setText(jTextPane1.getText() + "\n\nValues for the test set:");
				for (int i = 0; i < patterns.length; i++) {
					jTextPane1.setText(jTextPane1.getText() + "\nexp:\n");
					for (int j = 0; j < patterns[i].getRows(); j++)
						jTextPane1.setText(jTextPane1.getText() + patterns[i].position(j, 0) + " vs " + net.simulate(patterns[i]).position(j, 0) + "\n");
					jTextPane1.setText(jTextPane1.getText() + "---\n");
				}
			}
		}).start();
}//GEN-LAST:event_jButton1ActionPerformed

	private void testBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testBtnActionPerformed
		Matrix pattern = new Matrix(25, 1);
		JCheckBox[] input = new JCheckBox[]{p0, p1, p2, p3, p4,
			p5, p6, p7, p8, p9,
			p10, p11, p12, p13, p14,
			p15, p16, p17, p18, p19,
			p20, p21, p22, p23, p24,};

		JCheckBox[] output = new JCheckBox[]{p25, p26, p27, p28, p29,
			p30, p31, p32, p33, p34,
			p35, p36, p37, p38, p39,
			p40, p41, p42, p43, p44,
			p45, p46, p47, p48, p49,};
		for (int i = 0; i < input.length; i++) {
			pattern.position(i, 0, input[i].isSelected() ? 1 : -1);
		}
		System.err.println("pattern:\n" + pattern);

		int count = 0;
		//while(count++ < 10){
		Matrix pattern1 = net.simulate(pattern);
		System.err.println("\n--\npattern1:\n" + pattern1);

		for (int i = 0; i < output.length; i++) {
			output[i].setSelected(pattern1.position(i, 0) > 0);
		}
		pattern = pattern1;

		//invalidate();
		//try{Thread.sleep(1000);}catch(Exception e){}
		//}


	}//GEN-LAST:event_testBtnActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JCheckBox p0;
    private javax.swing.JCheckBox p1;
    private javax.swing.JCheckBox p10;
    private javax.swing.JCheckBox p11;
    private javax.swing.JCheckBox p12;
    private javax.swing.JCheckBox p13;
    private javax.swing.JCheckBox p14;
    private javax.swing.JCheckBox p15;
    private javax.swing.JCheckBox p16;
    private javax.swing.JCheckBox p17;
    private javax.swing.JCheckBox p18;
    private javax.swing.JCheckBox p19;
    private javax.swing.JCheckBox p2;
    private javax.swing.JCheckBox p20;
    private javax.swing.JCheckBox p21;
    private javax.swing.JCheckBox p22;
    private javax.swing.JCheckBox p23;
    private javax.swing.JCheckBox p24;
    private javax.swing.JCheckBox p25;
    private javax.swing.JCheckBox p26;
    private javax.swing.JCheckBox p27;
    private javax.swing.JCheckBox p28;
    private javax.swing.JCheckBox p29;
    private javax.swing.JCheckBox p3;
    private javax.swing.JCheckBox p30;
    private javax.swing.JCheckBox p31;
    private javax.swing.JCheckBox p32;
    private javax.swing.JCheckBox p33;
    private javax.swing.JCheckBox p34;
    private javax.swing.JCheckBox p35;
    private javax.swing.JCheckBox p36;
    private javax.swing.JCheckBox p37;
    private javax.swing.JCheckBox p38;
    private javax.swing.JCheckBox p39;
    private javax.swing.JCheckBox p4;
    private javax.swing.JCheckBox p40;
    private javax.swing.JCheckBox p41;
    private javax.swing.JCheckBox p42;
    private javax.swing.JCheckBox p43;
    private javax.swing.JCheckBox p44;
    private javax.swing.JCheckBox p45;
    private javax.swing.JCheckBox p46;
    private javax.swing.JCheckBox p47;
    private javax.swing.JCheckBox p48;
    private javax.swing.JCheckBox p49;
    private javax.swing.JCheckBox p5;
    private javax.swing.JCheckBox p6;
    private javax.swing.JCheckBox p7;
    private javax.swing.JCheckBox p8;
    private javax.swing.JCheckBox p9;
    private javax.swing.JButton testBtn;
    // End of variables declaration//GEN-END:variables
}

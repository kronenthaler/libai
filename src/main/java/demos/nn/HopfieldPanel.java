package demos.nn;

import demos.common.SimpleProgressDisplay;
import libai.common.matrix.Column;
import libai.common.matrix.Matrix;
import libai.nn.unsupervised.Hopfield;

import javax.swing.*;

/**
 * TODO: work on it.
 *
 * @author kronenthaler
 */
public class HopfieldPanel extends javax.swing.JPanel {
	Hopfield net;
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

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(p0)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p1)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p2)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p3)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p4))
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
																.addGroup(layout.createSequentialGroup()
																		.addComponent(p15)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p16)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p17)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p18)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p19))
																.addGroup(layout.createSequentialGroup()
																		.addComponent(p5)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p6)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p7)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p8)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(p9)))
														.addComponent(jLabel1)
														.addGroup(layout.createSequentialGroup()
																.addComponent(p10)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p11)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p12)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p13)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p14))
														.addGroup(layout.createSequentialGroup()
																.addComponent(p20)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p21)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p22)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p23)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p24)))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(p25)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p26)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p27)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p28)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p29))
														.addGroup(layout.createSequentialGroup()
																.addComponent(p35)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p36)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p37)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p38)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p39))
														.addGroup(layout.createSequentialGroup()
																.addComponent(p30)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p31)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p32)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p33)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(p34))
														.addComponent(jLabel2)
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(p40)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p41)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p42)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p43)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p44))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(p45)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p46)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p47)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p48)
																				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(p49)))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(testBtn)))
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
														.addGroup(layout.createSequentialGroup()
																.addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(jButton1)))
												.addContainerGap())))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addGroup(layout.createSequentialGroup()
												.addComponent(jLabel1)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p0)
																		.addComponent(p1)
																		.addComponent(p2)
																		.addComponent(p3)
																		.addComponent(p4))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p5)
																		.addComponent(p6)
																		.addComponent(p7)
																		.addComponent(p8)
																		.addComponent(p9))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(p10)
																		.addComponent(p11)
																		.addComponent(p12)
																		.addComponent(p13)
																		.addComponent(p14))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(p15)
																		.addComponent(p16)
																		.addComponent(p17)
																		.addComponent(p18)
																		.addComponent(p19))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p20)
																		.addComponent(p21)
																		.addComponent(p22)
																		.addComponent(p23)
																		.addComponent(p24)))
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p25)
																		.addComponent(p26)
																		.addComponent(p27)
																		.addComponent(p28)
																		.addComponent(p29))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p30)
																		.addComponent(p31)
																		.addComponent(p32)
																		.addComponent(p33)
																		.addComponent(p34))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(p35)
																		.addComponent(p36)
																		.addComponent(p37)
																		.addComponent(p38)
																		.addComponent(p39))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(p40)
																		.addComponent(p41)
																		.addComponent(p42)
																		.addComponent(p43)
																		.addComponent(p44))
																.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(p45)
																		.addComponent(p46)
																		.addComponent(p47)
																		.addComponent(p48)
																		.addComponent(p49)))
														.addComponent(testBtn, javax.swing.GroupLayout.Alignment.TRAILING))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addComponent(jLabel2)
												.addGap(6, 6, 6)))
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jButton1)
										.addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
				Column[] patterns = new Column[]{
						new Column(25, new double[]{
								-1, -1, +1, -1, -1,
								-1, -1, +1, -1, -1,
								+1, +1, +1, +1, +1,
								-1, -1, +1, -1, -1,
								-1, -1, +1, -1, -1,
						}),
						new Column(25, new double[]{
								+1, -1, -1, -1, +1,
								-1, +1, -1, +1, -1,
								-1, -1, +1, -1, -1,
								-1, +1, -1, +1, -1,
								+1, -1, -1, -1, +1,
						}),
				};
				Matrix[] ans = patterns;

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
		Column pattern = new Column(25);
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
		Column pattern1 = net.simulate(pattern);
		System.err.println("\n--\npattern1:\n" + pattern1);

		for (int i = 0; i < output.length; i++) {
			output[i].setSelected(pattern1.position(i, 0) > 0);
		}
		pattern = pattern1;

		//invalidate();
		//try{Thread.sleep(1000);}catch(Exception e){}
		//}


	}//GEN-LAST:event_testBtnActionPerformed
	// End of variables declaration//GEN-END:variables
}

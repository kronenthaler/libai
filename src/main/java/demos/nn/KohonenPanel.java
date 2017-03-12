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
import libai.nn.unsupervised.Kohonen;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 *
 * @author kronenthaler
 */
public class KohonenPanel extends javax.swing.JPanel {
	static double f(double x) {
		return Math.sin(x) + Math.cos(x);
	}
	int map[][], map2[][];

	/**
	 * Creates new form MPLPPanel
	 */
	public KohonenPanel() {
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
        canvas = new JPanel(){
            public void paint(Graphics gr){
                super.paint(gr);
                if(map == null) return;
                int pixelW = getWidth()/map[0].length;
                int pixelH = getHeight()/map.length;
                int pixelSize = Math.min(pixelW, pixelH);
                for(int j=0;j<map[0].length;j++){
                    for(int i=0;i<map.length;i++){
                        int r=(map[i][j]>>>16)&0x000000ff;
                        int g=(map[i][j]>>>8)&0x000000ff;
                        int b=map[i][j]&0x000000ff;
                        gr.setColor(new Color(r,g,b));
                        gr.fillRect(j*pixelSize, i*pixelSize, pixelSize, pixelSize);
                    }
                }
            }
        };

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        jButton1.setText("Train");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextPane1.setText("Train a Kohonen network to learn the colors in RGB mode.");
        jScrollPane1.setViewportView(jTextPane1);

        canvas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout canvasLayout = new javax.swing.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 534, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 231, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addComponent(canvas, GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(canvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
				int t = 0;
				int r = 0, g = 0, b = 0;
				int delta = 16;

				int n = delta * delta * delta;
				int m = 3; //rgb
				delta = 256 / (delta - 1);
				final int test = n / 4;

				final Matrix[] p = new Matrix[n];
				final Matrix[] ex = new Matrix[n];
				int[] classes = new int[n];

				for (r = 0; r <= 255; r += delta) {
					for (g = 0; g <= 255; g += delta) {
						for (b = 0; b <= 255; b += delta) {
							p[t] = new Matrix(3, 1);
							p[t].position(0, 0, r/255.0);
							p[t].position(1, 0, g/255.0);
							p[t].position(2, 0, b/255.0);

							ex[t] = new Matrix(1, 1);
							ex[t].position(0, 0, (classes[t++] = ((r << 16) | (g << 8) | b) & 0x00ffffff));
						}
					}
				}

				p[p.length - 1].position(0, 0, 1);
				p[p.length - 1].position(1, 0, 1);
				p[p.length - 1].position(2, 0, 1);

				ex[p.length - 1].position(0, 0, (classes[p.length - 1] = ((1 << 16) | (1 << 8) | 1) & 0x00ffffff));

				Random rand = new Random();
				for(int i=0;i<p.length;i++){
					int tmp = rand.nextInt(p.length);
					Matrix aux = p[i];
					p[i]=p[tmp];
					p[tmp]=aux;

					aux = ex[i];
					ex[i]=ex[tmp];
					ex[tmp]=aux;
				}

				int nperlayer[] = {m, 30, 40};
				final Kohonen net = new Kohonen(nperlayer, 10);

				net.setProgressBar(new SimpleProgressDisplay(jProgressBar1));
				net.train(p, ex, 1, 20, 0, p.length - test);

				map = net.getMap();
				canvas.repaint();

				jTextPane1.setText(jTextPane1.getText() + "Error for training set: " + net.error(p, p, 0, p.length - test));
				jTextPane1.setText(jTextPane1.getText() + "\nError for test set: " + net.error(p, p, p.length - test, test));
			}
		}).start();
	}//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel canvas;
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}

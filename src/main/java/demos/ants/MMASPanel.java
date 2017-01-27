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
package demos.ants;

import libai.ants.Enviroment;
import libai.ants.Graph;
import libai.ants.algorithms.MMAS;

import java.util.Vector;

/**
 *
 * @author kronenthaler
 */
public class MMASPanel extends javax.swing.JPanel {
	/**
	 * Creates new form AntSystemPanel
	 */
	public MMASPanel() {
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
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        answerTxt = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        parameterSet = new javax.swing.JComboBox();

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("To find the shortest path between the nest and the source of food, the ants uses an enviromental type of comunication as known as stirmergy. The framework that implements the ants colony algorithms is very generic and precise some particular functions to be implemented by the user based on the condition of the problem (like in the search algorithms). In this example we have a graph of 5 nodes, simulating the double-brigde problem.\nThe next adjacency matrix defines the graph.\n   0 1 2 3 4 \n0 0 1 2 0 0\n1 1 0 0 1 0\n2 2 0 0 2 0\n3 0 1 2 0 1\n4 0 0 0 1 0");
        jScrollPane1.setViewportView(jTextArea1);

        answerTxt.setColumns(20);
        answerTxt.setRows(5);
        jScrollPane2.setViewportView(answerTxt);

        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jProgressBar1.setString("training");
        jProgressBar1.setStringPainted(true);

        parameterSet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Parameter set 1", "Parameter set 2", "Parameter set 3", "Parameter set 4", " " }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parameterSet, 0, 360, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		try {
			final SP_MMAS as = new SP_MMAS(0, 4, parameterSet.getSelectedIndex());

			jProgressBar1.setMinimum(0);
			jProgressBar1.setMaximum((int) as.getParam(2));
			jProgressBar1.setValue(0);

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (as.getCurrentIterationNumber() < jProgressBar1.getMaximum()) {
						jProgressBar1.setValue(as.getCurrentIterationNumber());
						try {
							Thread.sleep(1);
						} catch (Exception e) {
						}
					}
					jProgressBar1.setValue(jProgressBar1.getMaximum());
				}
			}).start();

			as.solve();

			answerTxt.append(as.getBestSolution().toString() + " F(x)=" + as.f(as.getBestSolution()));
			answerTxt.append("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
}//GEN-LAST:event_jButton3ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea answerTxt;
    private javax.swing.JButton jButton3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox parameterSet;
    // End of variables declaration//GEN-END:variables

	static class SP_MMAS extends libai.ants.algorithms.MMAS {
		protected SP_MMAS(int problemInitialNode, int problemDestinationNode, int parametersSet) throws Exception {
			Graph G = new Graph(5, 5, new double[]{0, 1, 2, 0, 0,
				1, 0, 0, 1, 0,
				2, 0, 0, 2, 0,
				0, 1, 2, 0, 1,
				0, 0, 0, 1, 0});
			/* Set enviroment */
			Enviroment Env = new Enviroment(G, true);
			/* Set parameters */
			this.setParam(SP_MMAS.initialNode, problemInitialNode);
			this.setParam(SP_MMAS.destinationNode, problemDestinationNode);
			if (parametersSet < 0 && parametersSet > 3) {
				throw new Exception("parametersSet invalid, must be either 0, 1, 2 or 3");
			}
			switch (parametersSet) {
				case 0:
					this.setParam(SP_MMAS.maxNumIterations, 5);
					this.setParam(SP_MMAS.pheromonesEvaporationRate, 0.01);
					this.setParam(SP_MMAS.alpha, 1);
					this.setParam(SP_MMAS.beta, 1);
					this.setParam(SP_MMAS.tau_max, 1.75);
					this.setParam(SP_MMAS.tau_min, 0.75);
					Env.setPheromones(this.Parameters.get(SP_MMAS.tau_max));
					Env.setAnts(5);
					break;
				case 1:
					this.setParam(SP_MMAS.maxNumIterations, 50);
					this.setParam(SP_MMAS.pheromonesEvaporationRate, 0.01);
					this.setParam(SP_MMAS.alpha, 0.8);
					this.setParam(SP_MMAS.beta, 1);
					this.setParam(SP_MMAS.tau_max, 1.75);
					this.setParam(SP_MMAS.tau_min, 0.75);
					Env.setPheromones(this.Parameters.get(SP_MMAS.tau_max));
					Env.setAnts(5);
					break;
				case 2:
					this.setParam(SP_MMAS.maxNumIterations, 100);
					this.setParam(SP_MMAS.pheromonesEvaporationRate, 0.01);
					this.setParam(SP_MMAS.alpha, 0.8);
					this.setParam(SP_MMAS.beta, 1);
					this.setParam(SP_MMAS.tau_max, 1.75);
					this.setParam(SP_MMAS.tau_min, 0.75);
					Env.setPheromones(this.Parameters.get(SP_MMAS.tau_max));
					Env.setAnts(10);
					break;
				case 3:
					this.setParam(SP_MMAS.maxNumIterations, 200);
					this.setParam(SP_MMAS.pheromonesEvaporationRate, 0.01);
					this.setParam(SP_MMAS.alpha, 0.8);
					this.setParam(SP_MMAS.beta, 1);
					this.setParam(SP_MMAS.tau_max, 1.75);
					this.setParam(SP_MMAS.tau_min, 0.75);
					Env.setPheromones(this.Parameters.get(SP_MMAS.tau_max));
					Env.setAnts(15);
					break;
			}
			this.setE(Env);
		}

		@Override
		public double heuristicInfo(double number) {
			return 1 / number;
		}

		@Override
		public double f(Vector<Integer> Solution) {
			int numberSolutionNodes = Solution.size();
			if (numberSolutionNodes != 0 && Solution.elementAt((numberSolutionNodes - 1)) != this.Parameters.get(MMAS.destinationNode).intValue()) {
				return Double.MAX_VALUE;
			} else {
				return super.f(Solution);
			}
		}

		@Override
		public Vector<Integer> constrains(int i, Vector<Integer> currentSolution) {
			int cols = this.Graph.getM().getColumns();
			Vector<Integer> adjacents = new Vector<>();
			//Calculate adjancent nodes
			for (int j = 0; j < cols; j++) {
				if (this.Graph.getM().position(i, j) < Integer.MAX_VALUE) {
					//Is adyacent
					adjacents.add(j);
				}
			}
			return adjacents;
		}
	}
}

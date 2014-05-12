package demos.fuzzy;

import libai.fuzzy.sets.Triangular;
import libai.fuzzy.sets.FuzzySet;
import libai.fuzzy.FuzzyGroup;
import libai.fuzzy.Engine;
import libai.fuzzy.Rule;
import libai.fuzzy.Condition;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author kronenthaler
 */
public class FuzzyPanel extends javax.swing.JPanel {
	Engine engine = new Engine();
	boolean exit = false;
	FuzzyGroup answers, location, state;
	FuzzySet left, middle, right, movingLeft, movingRight, standingStill;

	/**
	 * Creates new form FuzzyPanel
	 */
	public FuzzyPanel() {
		initComponents();

		left = new Triangular(-1, -1, 0);
		middle = new Triangular(-1, 0, 1);
		right = new Triangular(0, 1, 1);
		//Variable position = new Variable(.5);
		location = new FuzzyGroup(left, middle, right);


		movingLeft = new Triangular(-0.5, -0.5, 0);
		standingStill = new Triangular(-0.5, 0, 0.5);
		movingRight = new Triangular(0, 0.5, 0.5);
		//Variable direction = new Variable(0);
		state = new FuzzyGroup(movingLeft, movingRight, standingStill);

		FuzzySet pull = new Triangular(-0.6, -0.5, 0, 0.5);
		FuzzySet none = new Triangular(-0.5, 0, 0.5, 0.5);
		FuzzySet push = new Triangular(0, 0.5, 0.6, 0.5);
		answers = new FuzzyGroup(pull, none, push);

		Rule r0 = new Rule(new Condition(middle, location.getVariable()).and(standingStill, state.getVariable()), none);
		Rule r1 = new Rule(new Condition(left, location.getVariable()), push);
		Rule r2 = new Rule(new Condition(right, location.getVariable()), pull);
		Rule r3 = new Rule(new Condition(middle, location.getVariable()), none);
		Rule r4 = new Rule(new Condition(movingLeft, state.getVariable()), push);
		Rule r5 = new Rule(new Condition(standingStill, state.getVariable()), none);
		Rule r6 = new Rule(new Condition(movingRight, state.getVariable()), pull);
		Rule r7 = new Rule(new Condition(left, location.getVariable()).and(movingLeft, state.getVariable()), push);
		Rule r8 = new Rule(new Condition(right, location.getVariable()).and(movingRight, state.getVariable()), pull);

		engine.addRule(r0, r1, r2, r3, r4, r5, r6, r7, r8);
		engine.addGroup(location, state, answers);
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
        jButton1 = new javax.swing.JButton();
        canvas = new JPanel(){
            public void paint(Graphics g){
                super.paint(g);
                if(location == null || state == null) return;

                double l = location.getVariable().getValue() + 1;
                double s = state.getVariable().getValue() + 0.5;

                int w = getWidth();
                int h = getHeight();

                int x = (int)(l*w)>>1;
                int y = h - 10;
                //g.drawLine(x,0,x,10);
                g.drawLine(0,y,w,y);

                g.setColor(Color.blue);
                g.drawRect(x-10, y-10, 20, 10);

                g.setColor(Color.red);
                g.drawLine(x, y-5, x+(int)(s*20), y-5-(int)(Math.cos(s)*20));
            }
        };
        posSpn = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dirSpn = new javax.swing.JSpinner();

        jTextPane1.setText("This fuzzy example is about the classical problem of balancing a pole over a car.  The car has two variables: direction and position. The direction can be: moving left, standing still or moving right and the position can be: \nleft, middle or right inside the platform.\nTo keep the pole balanced the car has the following rules:\nr0 = if(position is middle AND direction is standingStill) then none;\nr1 = if(position is left) then push;\nr2 = if(position is right) then pull;\nr3 = if(position is middle) then none;\nr4 = if(direction is movingLeft) then push;\nr5 = if(direction is standingStill) then none;\nr6 = if(direction is movingRight) then pull;\nr7 = if(position is left AND direction is movingLeft) then push;\nr8 = if(position is right AND direction is movingRight) then pull;\n\nThe fuzzy sets pull, none and push forms a fuzzy group (a contextually equal fuzzy sets). And the result of the inference is a quantity of energy to apply in one direction or another.");
        jScrollPane1.setViewportView(jTextPane1);

        jButton1.setText("Simulate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        canvas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout canvasLayout = new org.jdesktop.layout.GroupLayout(canvas);
        canvas.setLayout(canvasLayout);
        canvasLayout.setHorizontalGroup(
            canvasLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 449, Short.MAX_VALUE)
        );
        canvasLayout.setVerticalGroup(
            canvasLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        posSpn.setModel(new javax.swing.SpinnerNumberModel(-0.5d, -1.0d, 1.0d, 0.1d));
        posSpn.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                posSpnStateChanged(evt);
            }
        });

        jLabel1.setText("Position:");

        jLabel2.setText("Direction:");

        dirSpn.setModel(new javax.swing.SpinnerNumberModel(-0.5d, -0.5d, 0.5d, 0.1d));
        dirSpn.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                dirSpnStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, canvas, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(posSpn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dirSpn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 101, Short.MAX_VALUE)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(canvas, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(posSpn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(dirSpn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		if (!jButton1.getText().equalsIgnoreCase("simulate")) {
			jButton1.setText("Simulate");
			exit = true;
		} else {
			jButton1.setText("Stop");
			new Thread(new Runnable() {
				public void run() {
					while (!exit) {
						engine.start();

						//actualizar la direccion y la posicion
						location.getVariable().setValue(location.getVariable().getValue() + answers.getVariable().getValue());
						state.getVariable().setValue(state.getVariable().getValue() - answers.getVariable().getValue());
						canvas.repaint();

						//pintar la posicion actual
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					}
					exit = false;
				}
			}).start();
		}
}//GEN-LAST:event_jButton1ActionPerformed

	private void posSpnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_posSpnStateChanged
		location.getVariable().setValue((Double) posSpn.getValue());
		canvas.repaint();
	}//GEN-LAST:event_posSpnStateChanged

	private void dirSpnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_dirSpnStateChanged
		state.getVariable().setValue((Double) dirSpn.getValue());
		canvas.repaint();
	}//GEN-LAST:event_dirSpnStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel canvas;
    private javax.swing.JSpinner dirSpn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JSpinner posSpn;
    // End of variables declaration//GEN-END:variables
}
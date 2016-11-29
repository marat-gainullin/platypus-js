/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.routing.gui;

import com.bearsoft.routing.Connector;
import com.bearsoft.routing.Paths;
import com.bearsoft.routing.PathFragment;
import com.bearsoft.routing.QuadTree;
import com.bearsoft.routing.Sweeper;
import com.bearsoft.routing.graph.Vertex;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class Sample extends javax.swing.JFrame {

    protected static final Color selectedColor = Color.red.darker();
    protected static final Color obstaclesColor = Color.gray;
    protected static final Color spaceColor = Color.orange;
    protected static final Color edgeColor = Color.blue.darker();

    protected class DrawablePanel extends JPanel {

        public DrawablePanel() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color oldColor = g.getColor();
            try {
                for (Rectangle o : obstacles) {
                    g.setColor(obstaclesColor);
                    g.fillRect(o.x, o.y, o.width, o.height);
                    g.setColor(obstaclesColor.brighter());
                    g.drawRect(o.x, o.y, o.width, o.height);
                }
                if (graph != null) {
                    // paint vertices
                    for (Vertex<PathFragment> vertex : graph) {
                        g.setColor(spaceColor);
                        g.fillRect(vertex.attribute.rect.x, vertex.attribute.rect.y, vertex.attribute.rect.width, vertex.attribute.rect.height);
                        g.setColor(spaceColor.brighter().brighter());
                        g.drawRect(vertex.attribute.rect.x, vertex.attribute.rect.y, vertex.attribute.rect.width, vertex.attribute.rect.height);
                    }
                    // paint there's edges
                    for (Vertex<PathFragment> vertex : graph) {
                        for (Vertex<PathFragment> ajacent : vertex.getAjacent()) {
                            g.setColor(edgeColor);
                            Rectangle startRect = vertex.attribute.rect;
                            Rectangle endRect = ajacent.attribute.rect;
                            g.drawLine(startRect.x + startRect.width / 2, startRect.y + startRect.height / 2,
                                    endRect.x + endRect.width / 2, endRect.y + endRect.height / 2);
                        }
                    }
                }
                if (selected != null) {
                    g.setColor(selectedColor);
                    g.fillRect(selected.x, selected.y, selected.width, selected.height);
                    g.setColor(selectedColor.brighter().brighter());
                    g.drawRect(selected.x, selected.y, selected.width, selected.height);
                }
                if (connector != null) {
                    g.setColor(Color.black);
                    g.drawPolyline(connector.getX(), connector.getY(), connector.getSize());
                }
            } finally {
                g.setColor(oldColor);
            }
        }
    }

    protected class DeleteAction extends AbstractAction {

        public DeleteAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }

        @Override
        public boolean isEnabled() {
            return selected != null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            obstacles.remove(selected);
            rebuildGraph();
            findPaths();
            selected = null;
            pnlDraw.repaint();
        }
    }

    protected class RebuildAction extends AbstractAction {

        public RebuildAction() {
            super();
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, 0));
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rebuildGraph();
            findPaths();
            pnlDraw.repaint();
        }
    }
    protected DeleteAction deleteAction = new DeleteAction();
    protected RebuildAction rebuildAction = new RebuildAction();
    protected Set<Rectangle> obstacles = new HashSet<>();
    protected Rectangle selected;
    protected List<Vertex<PathFragment>> graph;
    protected QuadTree<Vertex<PathFragment>> verticiesIndex;
    protected Connector connector;

    /**
     * Creates new form Sample
     */
    public Sample() {
        initComponents();
        pnlDraw.getActionMap().put(DeleteAction.class.getSimpleName(), deleteAction);
        pnlDraw.getInputMap().put((KeyStroke) deleteAction.getValue(Action.ACCELERATOR_KEY), DeleteAction.class.getSimpleName());
        pnlDraw.getActionMap().put(RebuildAction.class.getSimpleName(), rebuildAction);
        pnlDraw.getInputMap().put((KeyStroke) rebuildAction.getValue(Action.ACCELERATOR_KEY), RebuildAction.class.getSimpleName());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDraw = new DrawablePanel();
        lblHintObstacle = new javax.swing.JLabel();
        lblHintDeleteObstacle = new javax.swing.JLabel();
        lblDescirption = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Visibility graph building test");

        pnlDraw.setBackground(new java.awt.Color(255, 255, 255));
        pnlDraw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlDrawMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlDrawMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlDrawMouseReleased(evt);
            }
        });
        pnlDraw.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlDrawMouseDragged(evt);
            }
        });

        lblHintObstacle.setText("Drag & drop to create a new obstacle");

        lblHintDeleteObstacle.setText("Hit \"Delete\" key to remove highlighted obstacle");

        lblDescirption.setText("Two points are connected by straight line if an atomic (two segment) orthogonal path can be constructed between them");

        javax.swing.GroupLayout pnlDrawLayout = new javax.swing.GroupLayout(pnlDraw);
        pnlDraw.setLayout(pnlDrawLayout);
        pnlDrawLayout.setHorizontalGroup(
            pnlDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDrawLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblHintObstacle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblHintDeleteObstacle, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .addComponent(lblDescirption, javax.swing.GroupLayout.PREFERRED_SIZE, 747, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(231, Short.MAX_VALUE))
        );
        pnlDrawLayout.setVerticalGroup(
            pnlDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDrawLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHintObstacle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblHintDeleteObstacle)
                .addGap(18, 18, 18)
                .addComponent(lblDescirption)
                .addContainerGap(562, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDraw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDraw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pnlDrawMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDrawMouseDragged
        if ((evt.getModifiersEx() & java.awt.event.MouseEvent.CTRL_DOWN_MASK) != java.awt.event.MouseEvent.CTRL_DOWN_MASK) {
            draw(evt.getPoint());
        }
    }//GEN-LAST:event_pnlDrawMouseDragged

    private void pnlDrawMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDrawMouseClicked
        selected = null;
        for (Rectangle o : obstacles) {
            if (o.contains(evt.getPoint())) {
                selected = o;
                break;
            }
        }
        deleteAction.setEnabled(deleteAction.isEnabled());
        pnlDraw.repaint();
    }//GEN-LAST:event_pnlDrawMouseClicked
    protected Point pressed;
    protected Point startConnectorPoint;
    protected Point endConnectorPoint;

    private void pnlDrawMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDrawMousePressed
        startConnectorPoint = null;
        endConnectorPoint = null;
        if ((evt.getModifiersEx() & java.awt.event.MouseEvent.CTRL_DOWN_MASK) == java.awt.event.MouseEvent.CTRL_DOWN_MASK) {
            startConnectorPoint = evt.getPoint();
        } else {
            pressed = evt.getPoint();
        }
    }//GEN-LAST:event_pnlDrawMousePressed

    protected void draw(Point aNewPoint) {
        if (pressed != null && !pressed.equals(aNewPoint)) {
            selected = new Rectangle();
            selected.x = Math.min(aNewPoint.x, pressed.x);
            selected.y = Math.min(aNewPoint.y, pressed.y);
            selected.width = Math.abs(aNewPoint.x - pressed.x);
            selected.height = Math.abs(aNewPoint.y - pressed.y);
            repaint();
        }
    }

    private void pnlDrawMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDrawMouseReleased
        if ((evt.getModifiersEx() & java.awt.event.MouseEvent.CTRL_DOWN_MASK) == java.awt.event.MouseEvent.CTRL_DOWN_MASK) {
            if (startConnectorPoint != null) {
                endConnectorPoint = evt.getPoint();
            }
        } else {
            draw(evt.getPoint());
            if (pressed != null && !pressed.equals(evt.getPoint())) {
                obstacles.add(selected);
            }
        }
        pressed = null;
        rebuildGraph();
        findPaths();
        pnlDraw.repaint();
        deleteAction.setEnabled(deleteAction.isEnabled());
    }//GEN-LAST:event_pnlDrawMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sample.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Sample().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblDescirption;
    private javax.swing.JLabel lblHintDeleteObstacle;
    private javax.swing.JLabel lblHintObstacle;
    private javax.swing.JPanel pnlDraw;
    // End of variables declaration//GEN-END:variables

    protected void rebuildGraph() {
        verticiesIndex = new QuadTree<>();
        graph = Sweeper.build(pnlDraw.getWidth(), pnlDraw.getHeight(), obstacles, verticiesIndex);
    }

    protected void findPaths() {
        connector = null;
        if (startConnectorPoint != null && endConnectorPoint != null) {
            Paths pf = new Paths(graph, verticiesIndex);
            connector = pf.find(startConnectorPoint, endConnectorPoint);
        }
    }
}

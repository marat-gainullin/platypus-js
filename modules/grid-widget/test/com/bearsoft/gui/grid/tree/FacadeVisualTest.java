/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.tree;

import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TreedModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class FacadeVisualTest extends FacadeBaseTest {

    @Test
    public void collapsedVisualTest() throws InterruptedException {
        System.out.println("collapsedVisualTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TreedModel model = new TestTreedModel();
        TableModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        //frame.setVisible(false);
    }

    @Test
    public void expandedSomeRootsVisualTest() throws InterruptedException {
        System.out.println("expandedSomeRootsVisualTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        tModel.expand(tree.get(37), false);
        tModel.expand(tree.get(7), false);
        tModel.expand(tree.get(56), false);
        tModel.expand(tree.get(138), false);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        //frame.setVisible(false);
    }

    protected class ExpandAction extends AbstractAction {

        protected TableFront2TreedModel tModel;
        protected JTable table;

        public ExpandAction(TableFront2TreedModel aTModel, JTable aTable) {
            super();
            putValue(Action.NAME, "expand");
            tModel = aTModel;
            table = aTable;
        }

        public void actionPerformed(ActionEvent e) {
            tModel.expand(tModel.getElementAt(table.getSelectedRow()), false);
        }
    }

    protected class CollapseAction extends AbstractAction {

        protected TableFront2TreedModel tModel;
        protected JTable table;

        public CollapseAction(TableFront2TreedModel aTModel, JTable aTable) {
            super();
            putValue(Action.NAME, "collapse");
            tModel = aTModel;
            table = aTable;
        }

        public void actionPerformed(ActionEvent e) {
            tModel.collapse(tModel.getElementAt(table.getSelectedRow()));
        }
    }

    @Test
    public void dynamicExpandCollapseVisualTest() throws InterruptedException {
        System.out.println("dynamicExpandCollapseVisualTest");
        int oldFirstLevelCount = firstLevelCount;
        try {
            firstLevelCount = 3000;
            initTree();
            System.out.println("\tbig tree initialized successfully");
            JFrame frame = new JFrame();
            frame.getContentPane().setLayout(new BorderLayout());
            TreedModel model = new TestTreedModel();
            TableFront2TreedModel tModel = new TableFront2TreedModel(model);
            JTable tbl = new JTable(tModel);

            JToolBar tb = new JToolBar();
            JButton collapseBtn = new JButton(new CollapseAction(tModel, tbl));
            JButton expandBtn = new JButton(new ExpandAction(tModel, tbl));
            tb.add(collapseBtn);
            tb.add(expandBtn);

            frame.getContentPane().add(tb, BorderLayout.NORTH);
            frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
            frame.setSize(800, 700);
            //frame.setVisible(true);
            //
            assertEquals(tree.size(), tbl.getModel().getRowCount());
            tModel.expand(tree.get(37), false);
            assertEquals(tree.size() + secondLevelCount, tbl.getModel().getRowCount());
            tModel.expand(tree.get(7), false);
            assertEquals(tree.size() + 2 * secondLevelCount, tbl.getModel().getRowCount());
            tModel.expand(tree.get(56), false);
            assertEquals(tree.size() + 3 * secondLevelCount, tbl.getModel().getRowCount());
            tModel.expand(tree.get(138), false);
            assertEquals(tree.size() + 4 * secondLevelCount, tbl.getModel().getRowCount());

            tModel.expand(tree.get(7).children.get(16), false);
            assertEquals(tree.size() + 4 * secondLevelCount + thirdLevelCount, tbl.getModel().getRowCount());
            tModel.expand(tree.get(7).children.get(23), false);
            assertEquals(tree.size() + 4 * secondLevelCount + 2 * thirdLevelCount, tbl.getModel().getRowCount());

            tModel.collapse(tree.get(7));
            assertEquals(tree.size() + 3 * secondLevelCount, tbl.getModel().getRowCount());
            tModel.expand(tree.get(7), false);
            assertEquals(tree.size() + 4 * secondLevelCount + 2 * thirdLevelCount, tbl.getModel().getRowCount());
            tModel.collapse(tree.get(7));
            assertEquals(tree.size() + 3 * secondLevelCount, tbl.getModel().getRowCount());

            tModel.collapse(tree.get(7).children.get(16));
            tModel.expand(tree.get(7), false);
            assertEquals(tree.size() + 4 * secondLevelCount + thirdLevelCount, tbl.getModel().getRowCount());
            //
            //frame.setVisible(false);
        } finally {
            firstLevelCount = oldFirstLevelCount;
            System.out.println("uugph!  dynamicExpandCollapseVisualTest completed");
        }
    }
}

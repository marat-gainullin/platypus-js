/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.tree;

import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TreedModel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class TreedDataChangesTest extends FacadeBaseTest {

    @Test
    public void forwardCellsChangesTest() {
        System.out.println("forwardCellsChangesTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        String test1Data = "Changed1";
        String test2Data = "Changed2";
        String test3Data = "Changed3";
        tbl.setValueAt(test1Data, 0, 0);
        tbl.setValueAt(test2Data, 1, 1);
        tbl.setValueAt(test3Data, 2, 2);
        assertEquals(test1Data, tbl.getValueAt(0, 0));
        assertEquals(test2Data, tbl.getValueAt(1, 1));
        assertEquals(test3Data, tbl.getValueAt(2, 2));
        assertEquals(test1Data, tree.get(0).data1);
        assertEquals(test2Data, tree.get(1).data2);
        assertEquals(test3Data, tree.get(2).data3);
        int oldRowCount = tbl.getRowCount();
        tModel.expand(tree.get(1), false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        //frame.setVisible(false);
    }

    @Test
    public void backwardCellsChangesTest() {
        System.out.println("backwardCellsChangesTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        int oldRowCount = tbl.getRowCount();
        tModel.expand(tree.get(1), false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        String test1Data = "Changed1";
        String test2Data = "Changed2";
        String test3Data = "Changed3";
        model.setValue(tree.get(0), 0, test1Data);
        model.setValue(tree.get(1), 1, test2Data);
        model.setValue(tree.get(2), 2, test3Data);
        assertEquals(test1Data, tbl.getValueAt(0, 0));
        assertEquals(test2Data, tbl.getValueAt(1, 1));
        assertEquals(test3Data, tbl.getValueAt(2 + 30, 2));
        //frame.setVisible(false);
    }

    @Test
    public void addedElementsTest() {
        System.out.println("addedElementsTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TestTreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);

        // fill some node and than try to expand it
        // the right case is children count would double.
        int oldRowCount = tbl.getRowCount();
        TreeItem toFill = tree.get(6);
        int oldChildrenCount = toFill.children.size();
        for (int i = 0; i < oldChildrenCount; i++) {
            model.add(toFill, new TreeItem("inserted " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 0);
        }
        //       so, let's see...
        assertEquals(oldRowCount, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + oldChildrenCount * 2, tbl.getRowCount());
        tModel.collapse(toFill);
        assertEquals(oldRowCount, tbl.getRowCount());

        // fill some already expanded node
        toFill = tree.get(7);
        oldRowCount = tbl.getRowCount();
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        oldRowCount = tbl.getRowCount();
        oldChildrenCount = toFill.children.size();
        for (int i = 0; i < oldChildrenCount; i++) {
            model.add(toFill, new TreeItem("inserted " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 0);
        }
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        // let's perform some operations on fill node and see what it will lead to.
        tModel.collapse(toFill);
        assertEquals(oldRowCount - 30, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        tModel.collapse(toFill);
        assertEquals(oldRowCount - 30, tbl.getRowCount());

        // add a node in top level
        oldRowCount = tbl.getRowCount();
        for (int i = 0; i < 10; i++) {
            toFill = new TreeItem(8 + i + "top inserted; col0", 8 + i + "top inserted; col1", 8 + i + "top inserted; col2");
            model.add(null, toFill, 8 + i);
        }
        assertEquals(oldRowCount + 10, tbl.getRowCount());
    }


    @Test
    public void addedMultipleElementsTest() {
        System.out.println("addedMultipleElementsTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TestTreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);

        // fill some node and than try to expand it
        // the right case is children count would double.
        int oldRowCount = tbl.getRowCount();
        TreeItem toFill = tree.get(6);
        int oldChildrenCount = toFill.children.size();
        for (int i = 0; i < oldChildrenCount; i++) {
            model.add(toFill, new TreeItem("inserted " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 0);
        }
        //       so, let's see...
        assertEquals(oldRowCount, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + oldChildrenCount * 2, tbl.getRowCount());
        tModel.collapse(toFill);
        assertEquals(oldRowCount, tbl.getRowCount());

        // fill some already expanded node
        toFill = tree.get(7);
        oldRowCount = tbl.getRowCount();
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        oldRowCount = tbl.getRowCount();
        oldChildrenCount = toFill.children.size();
        for (int i = 0; i < oldChildrenCount; i++) {
            model.add(toFill, new TreeItem("inserted   " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 0,
                              new TreeItem("inserted_  " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 2,
                              new TreeItem("inserted__ " + i + "; col0", "inserted " + i + "; col1", "inserted " + i + "; col2"), 3);
        }
        assertEquals(oldRowCount + 30*3, tbl.getRowCount());
        // let's perform some operations on fill node and see what it will lead to.
        tModel.collapse(toFill);
        assertEquals(oldRowCount - 30, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30*3, tbl.getRowCount());
        tModel.expand(toFill, false);
        assertEquals(oldRowCount + 30*3, tbl.getRowCount());
        tModel.collapse(toFill);
        assertEquals(oldRowCount - 30, tbl.getRowCount());

        // add a node in top level
        oldRowCount = tbl.getRowCount();
        for (int i = 0; i < 10; i++) {
            toFill = new TreeItem(8 + i + "top inserted; col0", 8 + i + "top inserted; col1", 8 + i + "top inserted; col2");
            model.add(null, toFill, 8 + i);
        }
        assertEquals(oldRowCount + 10, tbl.getRowCount());
    }
    
    @Test
    public void removedElementsTest() {
        System.out.println("removedElementsTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TestTreedModel model = new TestTreedModel();
        TableFront2TreedModel tModel = new TableFront2TreedModel(model);
        JTable tbl = new JTable(tModel);
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);

        // clear some node and than try to expand it
        // the right case is nothing changes.
        int oldRowCount = tbl.getRowCount();
        TreeItem toClear = tree.get(6);
        for (int i = toClear.children.size() - 1; i >= 0; i--) {
            model.remove(toClear.children.get(i));
        }
        //       so, let's see...
        tModel.expand(toClear, false);
        assertEquals(oldRowCount, tbl.getRowCount());
        tModel.collapse(toClear);
        assertEquals(oldRowCount, tbl.getRowCount());

        // clear some already expanded node
        toClear = tree.get(7);
        oldRowCount = tbl.getRowCount();
        tModel.expand(toClear, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());
        for (int i = toClear.children.size() - 1; i >= 0; i--) {
            model.remove(toClear.children.get(i));
        }
        assertEquals(oldRowCount, tbl.getRowCount());
        // let's perform some operations on empty node and see what it will lead to.
        tModel.collapse(toClear);
        tModel.expand(toClear, false);
        tModel.expand(toClear, false);
        tModel.collapse(toClear);
        assertEquals(oldRowCount, tbl.getRowCount());

        // remove node without expanding it
        toClear = tree.get(8);
        model.remove(toClear);

        // remove already expanded node
        toClear = tree.get(8);
        oldRowCount = tbl.getRowCount();
        tModel.expand(toClear, false);
        assertEquals(oldRowCount + 30, tbl.getRowCount());

        oldRowCount = tbl.getRowCount();
        model.remove(toClear);
        assertEquals(oldRowCount - 30 - 1, tbl.getRowCount());
    }
}

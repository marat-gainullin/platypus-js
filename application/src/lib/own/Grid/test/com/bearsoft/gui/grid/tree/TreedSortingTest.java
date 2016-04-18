/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.tree;

import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TreedModel;
import com.bearsoft.gui.grid.rows.TreedRowsSorter;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class TreedSortingTest extends FacadeBaseTest{

    @Test
    public void treedSorterTest() throws Exception
    {
        System.out.println("treedSorterTest");
        initTree();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        TreedModel model = new TestTreedModel();
        TableFront2TreedModel<TreeItem> tModel = new TableFront2TreedModel<>(model);
        tModel.expand(tree.get(37), false);
        tModel.expand(tree.get(7), false);
        tModel.expand(tree.get(56), false);
        tModel.expand(tree.get(138), false);
        JTable tbl = new JTable(tModel);
        tbl.setRowSorter(new TreedRowsSorter<>(tModel, null));
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        Thread.sleep(100);
        //frame.setVisible(false);
    }   
}

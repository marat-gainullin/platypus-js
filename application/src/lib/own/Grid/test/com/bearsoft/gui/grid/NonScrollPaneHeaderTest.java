/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class NonScrollPaneHeaderTest extends BaseTableTest {

    protected JFrame frame = new JFrame();
    protected TableModel generalModel = new SampleTableModel();
    protected JTable table = new JTable(generalModel);

    @Before
    public void prepare() throws InterruptedException {
        table.setModel(generalModel);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(table.getTableHeader(), BorderLayout.NORTH);
        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
    }

    @Test
    public void headerShownTest() {
        System.out.println("headerShownTest");
//        assertTrue(table.getTableHeader().isShowing());
//        assertTrue(table.isShowing());
        assertTrue(table.getTableHeader().getParent() == frame.getContentPane());
        assertTrue(table.getParent() == frame.getContentPane());
    }
}

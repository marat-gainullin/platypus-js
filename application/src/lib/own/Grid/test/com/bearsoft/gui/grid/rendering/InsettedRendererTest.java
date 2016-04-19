/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import com.bearsoft.gui.grid.BaseTableTest;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class InsettedRendererTest extends BaseTableTest {

    @Test
    public void multipleTypesRenderingTest() throws InterruptedException {
        JLabel leadLabel = new JLabel("< ");
        JLabel trailingLabel = new JLabel(" >");
        TableModel generalModel = new SampleTableModel();

        JTable tbl1 = new JTable(generalModel);
        TableColumn col1 = tbl1.getColumnModel().getColumn(1);
        col1.setCellRenderer(new InsettedRenderer(new DefaultTableCellRenderer(), leadLabel, trailingLabel));
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tbl1, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        Thread.sleep(1000);
        //frame.setVisible(false);
    }
}

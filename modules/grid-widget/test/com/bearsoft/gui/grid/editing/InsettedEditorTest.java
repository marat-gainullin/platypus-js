/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.editing;

import com.bearsoft.gui.grid.BaseTableTest;
import com.bearsoft.gui.grid.rendering.InsettedRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class InsettedEditorTest extends BaseTableTest {

    @Test
    public void multipleTypesRenderingTest() throws InterruptedException {
        TableModel generalModel = new SampleTableModel();
        JTable tbl = new JTable(generalModel);
        tbl.setBackground(new Color(250, 255, 200));

        TableColumn col1 = tbl.getColumnModel().getColumn(1);
        DefaultCellEditor defEditor1 = new DefaultCellEditor(new JCheckBox());
        defEditor1.setClickCountToStart(2);
        JLabel leadLabel11 = new JLabel("< ");
        JLabel trailingLabel11 = new JLabel(" >");
        col1.setCellEditor(new InsettedEditor(defEditor1, leadLabel11, trailingLabel11));
        JLabel leadLabel12 = new JLabel("< ");
        JLabel trailingLabel12 = new JLabel(" >");
        col1.setCellRenderer(new InsettedRenderer(new DefaultTableCellRenderer(), leadLabel12, trailingLabel12));

        TableColumn col3 = tbl.getColumnModel().getColumn(3);
        JTextField txt = new JTextField();
        txt.setBorder(null);
        DefaultCellEditor defEditor3 = new DefaultCellEditor(txt);
        defEditor3.setClickCountToStart(2);
        JLabel leadLabel21 = new JLabel("< ");
        JLabel trailingLabel21 = new JLabel(" >");
        col3.setCellEditor(new InsettedEditor(defEditor3, leadLabel21, trailingLabel21));
        JLabel leadLabel22 = new JLabel("< ");
        JLabel trailingLabel22 = new JLabel(" >");
        col3.setCellRenderer(new InsettedRenderer(new DefaultTableCellRenderer(), leadLabel22, trailingLabel22));

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tbl, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        Thread.sleep(1000);
        //frame.setVisible(false);
    }
}

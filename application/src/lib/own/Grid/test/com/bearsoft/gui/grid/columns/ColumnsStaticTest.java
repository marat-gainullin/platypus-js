/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.columns;

import com.bearsoft.gui.grid.BaseTableTest;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.BoundaredTableModel;
import com.bearsoft.gui.grid.insets.InsetContent;
import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.LinearInset;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.gui.grid.selection.InsettedListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Enumeration;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class ColumnsStaticTest extends BaseTableTest {

    protected int fixedRows = 3;
    protected int fixedCols = 2;
    protected TableModel etalonModel;
    protected TableColumnModel etalonColumnModel;
    protected JTable tlTable;
    protected JTable trTable;
    protected JTable blTable;
    protected JTable brTable;

    @Before
    public void prepare() throws InterruptedException {
        // base grid
        etalonModel = new SampleTableModel();
        etalonColumnModel = new SampleColumnModel(new LinearInset(0, 0));
        for (int i = 0; i < etalonColumnModel.getColumnCount(); i++) {
            etalonColumnModel.getColumn(i).setMinWidth(150);
        }
        final ListSelectionModel etalonRowsSelectionModel = new DefaultListSelectionModel();
        final ListSelectionModel etalonColumnsSelectionModel = new DefaultListSelectionModel();
        etalonColumnModel.setSelectionModel(etalonColumnsSelectionModel);
        etalonColumnModel.setColumnSelectionAllowed(true);
        etalonRowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        etalonColumnsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // insets/constraints setup
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);//(1, 0);
        LinearConstraint leftColsConstraint = new LinearConstraint(0, fixedCols - 1);
        LinearConstraint rightColsConstraint = new LinearConstraint(fixedCols, Integer.MAX_VALUE);
        LinearConstraint topRowsConstraint = new LinearConstraint(0, fixedRows - 1);
        LinearConstraint bottomRowsConstraint = new LinearConstraint(fixedRows, Integer.MAX_VALUE);

        // insetted layer models setup
        final TableModel insettedModel = new BoundaredTableModel(etalonModel, InsetPart.AFTER_INSET_BIAS, rowsInset, columnsInset);
        final ListSelectionModel insettedSelectionModel = new InsettedListSelectionModel(etalonRowsSelectionModel, rowsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getRowCount();
            }
        });
        final TableColumnModel insettedColumnModel = new InsettedColumnModel(etalonColumnModel, columnsInset);
        final ListSelectionModel insettedColumnSelectionModel = new InsettedListSelectionModel(etalonColumnsSelectionModel, columnsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getColumnCount();
            }
        });
        insettedColumnModel.setSelectionModel(insettedColumnSelectionModel);

        tlTable = new JTable();
        trTable = new JTable();
        blTable = new JTable();
        brTable = new JTable();

        // constrained layer models setup
        tlTable.setModel(insettedModel);
        tlTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        tlTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        tlTable.setRowSorter(new ConstrainedRowSorter<>(insettedModel, topRowsConstraint));
        tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        trTable.setModel(insettedModel);
        trTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        trTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        trTable.setRowSorter(new ConstrainedRowSorter<>(insettedModel, topRowsConstraint));
        trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        blTable.setModel(insettedModel);
        blTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        blTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        blTable.setRowSorter(new ConstrainedRowSorter<>(insettedModel, bottomRowsConstraint));
        blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        brTable.setModel(insettedModel);
        brTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        brTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        brTable.setRowSorter(new ConstrainedRowSorter<>(insettedModel, bottomRowsConstraint));
        brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // grid components setup
        JPanel tlPanel = new JPanel(new BorderLayout());
        tlPanel.add(tlTable.getTableHeader(), BorderLayout.NORTH);
        tlPanel.add(tlTable, BorderLayout.CENTER);
        JPanel trPanel = new JPanel(new BorderLayout());
        trPanel.add(trTable.getTableHeader(), BorderLayout.NORTH);
        trPanel.add(trTable, BorderLayout.CENTER);
        JPanel blPanel = new JPanel(new BorderLayout());
        blPanel.add(blTable, BorderLayout.CENTER);
        JPanel brPanel = new JPanel(new BorderLayout());
        brPanel.add(brTable, BorderLayout.CENTER);

        trPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.blue));
        tlPanel.setBorder(new MatteBorder(0, 0, 1, 1, Color.blue));
        blPanel.setBorder(new MatteBorder(0, 0, 0, 1, Color.blue));

        final JScrollPane gridScroll = new JScrollPane();
        gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
        gridScroll.setColumnHeaderView(trPanel);
        gridScroll.setRowHeaderView(blPanel);
        gridScroll.setViewportView(brPanel);

        // test components setup
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(gridScroll, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        frame.setTitle("configuredTest");
    }

    @Test
    public void constraintedColumnsTest() {
        System.out.println("constraintedColumnsTest");
        assertEquals(tlTable.getColumnCount(), fixedCols);
        checkColumnsEnumeration(tlTable, fixedCols);
        assertEquals(blTable.getColumnCount(), fixedCols);
        checkColumnsEnumeration(blTable, fixedCols);
        assertEquals(trTable.getColumnCount(), etalonColumnModel.getColumnCount() - fixedCols);
        checkColumnsEnumeration(trTable, etalonColumnModel.getColumnCount() - fixedCols);
        assertEquals(brTable.getColumnCount(), etalonColumnModel.getColumnCount() - fixedCols);
        checkColumnsEnumeration(brTable, etalonColumnModel.getColumnCount() - fixedCols);
    }

    @Test
    public void constraintedRowsTest() {
        System.out.println("constraintedRowsTest");
        assertEquals(tlTable.getRowCount(), fixedRows);
        assertEquals(trTable.getRowCount(), fixedRows);
        assertEquals(blTable.getRowCount(), etalonModel.getRowCount() - fixedRows);
        assertEquals(brTable.getRowCount(), etalonModel.getRowCount() - fixedRows);
    }

    private void checkColumnsEnumeration(JTable aTable, int aCols) {
        Enumeration<TableColumn> cols = aTable.getColumnModel().getColumns();
        int enumeratedColsCount = 0;
        while (cols.hasMoreElements()) {
            cols.nextElement();
            enumeratedColsCount++;
        }
        assertEquals(enumeratedColsCount, aCols);
        assertEquals(aTable.getColumnCount(), enumeratedColsCount);
    }
}

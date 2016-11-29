/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid;

import com.bearsoft.gui.grid.BaseTableTest.SampleColumnModel;
import com.bearsoft.gui.grid.BaseTableTest.SampleTableModel;
import com.bearsoft.gui.grid.columns.ConstrainedColumnModel;
import com.bearsoft.gui.grid.columns.InsettedColumnModel;
import com.bearsoft.gui.grid.constraints.LinearConstraint;
import com.bearsoft.gui.grid.data.BoundaredTableModel;
import com.bearsoft.gui.grid.insets.InsetContent;
import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.LinearInset;
import com.bearsoft.gui.grid.rows.ConstrainedRowSorter;
import com.bearsoft.gui.grid.rows.InsettedRowSorter;
import com.bearsoft.gui.grid.selection.ConstrainedListSelectionModel;
import com.bearsoft.gui.grid.selection.InsettedListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Gala
 */
public class GridTest extends BaseTableTest {

    protected JFrame frame;
    protected SampleTableModel etalonModel;
    protected TableColumnModel etalonColumnModel;
    protected TableColumnModel insettedColumnModel;
    protected ListSelectionModel etalonRowsSelectionModel;
    protected ListSelectionModel etalonColumnsSelectionModel;
    protected TableModel insettedModel;
    protected ListSelectionModel insettedSelectionModel;
    protected ListSelectionModel insettedColumnSelectionModel;

    protected class SyncTableHeader extends JTableHeader {

        public SyncTableHeader(TableColumnModel aColumnModel) {
            super(aColumnModel);
        }

        @Override
        protected void paintComponent(Graphics g) {
            synchronized (GridTest.this) {
                super.paintComponent(g);
            }
        }
    }

    protected class SyncTable extends JTable {

        @Override
        protected void paintComponent(Graphics g) {
            synchronized (GridTest.this) {
                super.paintComponent(g);
            }
        }

        @Override
        protected JTableHeader createDefaultTableHeader() {
            return new SyncTableHeader(columnModel);
        }
    }

    protected class ConfResult {

        public LinearConstraint leftColsConstraint;
        public LinearConstraint rightColsConstraint;
        public LinearConstraint topRowsConstraint;
        public LinearConstraint bottomRowsConstraint;
        public JTable tlTable = new SyncTable();
        public JTable trTable = new SyncTable();
        public JTable blTable = new SyncTable();
        public JTable brTable = new SyncTable();
    }

    protected ConfResult beginVisual(int aFixedRows, int aFixedCols, LinearInset aRowsInset, LinearInset aColumnsInset) throws Exception {
        // base grid
        etalonModel = new SampleTableModel();
        etalonColumnModel = new SampleColumnModel(new LinearInset(0, 0));
        for (int i = 0; i < etalonColumnModel.getColumnCount(); i++) {
            etalonColumnModel.getColumn(i).setMinWidth(15);
        }
        etalonRowsSelectionModel = new DefaultListSelectionModel();
        etalonColumnsSelectionModel = new DefaultListSelectionModel();
        etalonColumnModel.setSelectionModel(etalonColumnsSelectionModel);
        etalonColumnModel.setColumnSelectionAllowed(true);
        etalonRowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        etalonColumnsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // constraints setup
        LinearConstraint leftColsConstraint = new LinearConstraint(0, aFixedCols - 1);
        LinearConstraint rightColsConstraint = new LinearConstraint(aFixedCols, Integer.MAX_VALUE);
        LinearConstraint topRowsConstraint = new LinearConstraint(0, aFixedRows - 1);
        LinearConstraint bottomRowsConstraint = new LinearConstraint(aFixedRows, Integer.MAX_VALUE);

        ConfResult result = new ConfResult();

        result.leftColsConstraint = leftColsConstraint;
        result.rightColsConstraint = rightColsConstraint;
        result.topRowsConstraint = topRowsConstraint;
        result.bottomRowsConstraint = bottomRowsConstraint;

        // insetted layer models setup
        insettedModel = new BoundaredTableModel(etalonModel, InsetPart.AFTER_INSET_BIAS, aRowsInset, aColumnsInset);
        insettedSelectionModel = new InsettedListSelectionModel(etalonRowsSelectionModel, aRowsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getRowCount();
            }
        });
        insettedColumnModel = new InsettedColumnModel(etalonColumnModel, aColumnsInset);
        insettedColumnSelectionModel = new InsettedListSelectionModel(etalonColumnsSelectionModel, aColumnsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getColumnCount();
            }
        });
        insettedColumnModel.setSelectionModel(insettedColumnSelectionModel);
        InsettedRowSorter<TableModel> insettedRowSorter = new InsettedRowSorter<>(insettedModel, aRowsInset);

        // constrained layer models setup
        result.tlTable.setModel(insettedModel);
        result.trTable.setModel(insettedModel);
        result.blTable.setModel(insettedModel);
        result.brTable.setModel(insettedModel);

        result.tlTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, topRowsConstraint));
        result.tlTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        result.tlTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        result.tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        result.tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.trTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, topRowsConstraint));
        result.trTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        result.trTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        result.trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        result.trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.blTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, bottomRowsConstraint));
        result.blTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        result.blTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        result.blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        result.blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.brTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, bottomRowsConstraint));
        result.brTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        result.brTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        result.brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        result.brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // grid components setup
        JPanel tlPanel = new JPanel(new BorderLayout());
        tlPanel.add(result.tlTable.getTableHeader(), BorderLayout.NORTH);
        tlPanel.add(result.tlTable, BorderLayout.CENTER);
        JPanel trPanel = new JPanel(new BorderLayout());
        trPanel.add(result.trTable.getTableHeader(), BorderLayout.NORTH);
        trPanel.add(result.trTable, BorderLayout.CENTER);
        JPanel blPanel = new JPanel(new BorderLayout());
        blPanel.add(result.blTable, BorderLayout.CENTER);
        JPanel brPanel = new JPanel(new BorderLayout());
        brPanel.add(result.brTable, BorderLayout.CENTER);

        trPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.blue));
        tlPanel.setBorder(new MatteBorder(0, 0, 1, 1, Color.blue));
        blPanel.setBorder(new MatteBorder(0, 0, 0, 1, Color.blue));

        final JScrollPane gridScroll = new JScrollPane();
        gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
        gridScroll.setColumnHeaderView(trPanel);
        gridScroll.setRowHeaderView(blPanel);
        gridScroll.setViewportView(brPanel);

        // test components setup
        frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(gridScroll, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        frame.setTitle("Configured tests frame with constraints configured over general insets");
        return result;
    }

    protected ConfResult beginConstrainedVisual(int aFixedRows, int aFixedCols) throws Exception {
        // base grid
        etalonModel = new SampleTableModel();
        etalonColumnModel = new SampleColumnModel(new LinearInset(0, 0));
        for (int i = 0; i < etalonColumnModel.getColumnCount(); i++) {
            etalonColumnModel.getColumn(i).setMinWidth(15);
        }
        etalonRowsSelectionModel = new DefaultListSelectionModel();
        etalonColumnsSelectionModel = new DefaultListSelectionModel();
        etalonColumnModel.setSelectionModel(etalonColumnsSelectionModel);
        etalonColumnModel.setColumnSelectionAllowed(true);
        etalonRowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        etalonColumnsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // constraints setup
        LinearConstraint leftColsConstraint = new LinearConstraint(0, aFixedCols - 1);
        LinearConstraint rightColsConstraint = new LinearConstraint(aFixedCols, Integer.MAX_VALUE);
        LinearConstraint topRowsConstraint = new LinearConstraint(0, aFixedRows - 1);
        LinearConstraint bottomRowsConstraint = new LinearConstraint(aFixedRows, Integer.MAX_VALUE);

        ConfResult result = new ConfResult();
        // constrained layer models setup
        result.tlTable.setModel(etalonModel);
        result.trTable.setModel(etalonModel);
        result.blTable.setModel(etalonModel);
        result.brTable.setModel(etalonModel);

        result.tlTable.setRowSorter(new ConstrainedRowSorter(etalonModel, topRowsConstraint));
        result.tlTable.setSelectionModel(new ConstrainedListSelectionModel(etalonRowsSelectionModel, topRowsConstraint));
        result.tlTable.setColumnModel(new ConstrainedColumnModel(etalonColumnModel, leftColsConstraint));
        result.tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(etalonColumnsSelectionModel, leftColsConstraint));
        result.tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.trTable.setRowSorter(new ConstrainedRowSorter(etalonModel, topRowsConstraint));
        result.trTable.setSelectionModel(new ConstrainedListSelectionModel(etalonRowsSelectionModel, topRowsConstraint));
        result.trTable.setColumnModel(new ConstrainedColumnModel(etalonColumnModel, rightColsConstraint));
        result.trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(etalonColumnsSelectionModel, rightColsConstraint));
        result.trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.blTable.setRowSorter(new ConstrainedRowSorter(etalonModel, bottomRowsConstraint));
        result.blTable.setSelectionModel(new ConstrainedListSelectionModel(etalonRowsSelectionModel, bottomRowsConstraint));
        result.blTable.setColumnModel(new ConstrainedColumnModel(etalonColumnModel, leftColsConstraint));
        result.blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(etalonColumnsSelectionModel, leftColsConstraint));
        result.blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.brTable.setRowSorter(new ConstrainedRowSorter(etalonModel, bottomRowsConstraint));
        result.brTable.setSelectionModel(new ConstrainedListSelectionModel(etalonRowsSelectionModel, bottomRowsConstraint));
        result.brTable.setColumnModel(new ConstrainedColumnModel(etalonColumnModel, rightColsConstraint));
        result.brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(etalonColumnsSelectionModel, rightColsConstraint));
        result.brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // grid components setup
        JPanel tlPanel = new JPanel(new BorderLayout());
        tlPanel.add(result.tlTable.getTableHeader(), BorderLayout.NORTH);
        tlPanel.add(result.tlTable, BorderLayout.CENTER);
        JPanel trPanel = new JPanel(new BorderLayout());
        trPanel.add(result.trTable.getTableHeader(), BorderLayout.NORTH);
        trPanel.add(result.trTable, BorderLayout.CENTER);
        JPanel blPanel = new JPanel(new BorderLayout());
        blPanel.add(result.blTable, BorderLayout.CENTER);
        JPanel brPanel = new JPanel(new BorderLayout());
        brPanel.add(result.brTable, BorderLayout.CENTER);

        trPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.blue));
        tlPanel.setBorder(new MatteBorder(0, 0, 1, 1, Color.blue));
        blPanel.setBorder(new MatteBorder(0, 0, 0, 1, Color.blue));

        final JScrollPane gridScroll = new JScrollPane();
        gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
        gridScroll.setColumnHeaderView(trPanel);
        gridScroll.setRowHeaderView(blPanel);
        gridScroll.setViewportView(brPanel);

        // test components setup
        frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(gridScroll, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        frame.setTitle("Configured tests frame with only constraints configured");
        return result;
    }

    protected ConfResult beginSortedVisual(int aFixedRows, int aFixedCols, LinearInset aRowsInset, LinearInset aColumnsInset) throws Exception {
        // base grid
        etalonModel = new SampleTableModel();
        etalonColumnModel = new SampleColumnModel(new LinearInset(0, 0));
        for (int i = 0; i < etalonColumnModel.getColumnCount(); i++) {
            etalonColumnModel.getColumn(i).setMinWidth(15);
        }
        etalonRowsSelectionModel = new DefaultListSelectionModel();
        etalonColumnsSelectionModel = new DefaultListSelectionModel();
        etalonColumnModel.setSelectionModel(etalonColumnsSelectionModel);
        etalonColumnModel.setColumnSelectionAllowed(true);
        etalonRowsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        etalonColumnsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // constraints setup
        LinearConstraint leftColsConstraint = new LinearConstraint(0, aFixedCols - 1);
        LinearConstraint rightColsConstraint = new LinearConstraint(aFixedCols, Integer.MAX_VALUE);
        LinearConstraint topRowsConstraint = new LinearConstraint(0, aFixedRows - 1);
        LinearConstraint bottomRowsConstraint = new LinearConstraint(aFixedRows, Integer.MAX_VALUE);

        // insetted layer models setup
        insettedModel = new BoundaredTableModel(etalonModel, InsetPart.AFTER_INSET_BIAS, aRowsInset, aColumnsInset);
        insettedSelectionModel = new InsettedListSelectionModel(etalonRowsSelectionModel, aRowsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getRowCount();
            }
        });
        insettedColumnModel = new InsettedColumnModel(etalonColumnModel, aColumnsInset);
        insettedColumnSelectionModel = new InsettedListSelectionModel(etalonColumnsSelectionModel, aColumnsInset, new InsetContent() {

            public int getContentSize() {
                return insettedModel.getColumnCount();
            }
        });
        insettedColumnModel.setSelectionModel(insettedColumnSelectionModel);

        InsettedRowSorter<TableModel> insettedRowSorter = new InsettedRowSorter<>(new TableRowSorter(insettedModel), aRowsInset);

        ConfResult result = new ConfResult();
        // constrained layer models setup
        result.tlTable.setModel(insettedModel);
        result.trTable.setModel(insettedModel);
        result.blTable.setModel(insettedModel);
        result.brTable.setModel(insettedModel);

        result.tlTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, topRowsConstraint));
        result.tlTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        result.tlTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        result.tlTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        result.tlTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.trTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, topRowsConstraint));
        result.trTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, topRowsConstraint));
        result.trTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        result.trTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        result.trTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.blTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, bottomRowsConstraint));
        result.blTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        result.blTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, leftColsConstraint));
        result.blTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, leftColsConstraint));
        result.blTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        result.brTable.setRowSorter(new ConstrainedRowSorter(insettedRowSorter, bottomRowsConstraint));
        result.brTable.setSelectionModel(new ConstrainedListSelectionModel(insettedSelectionModel, bottomRowsConstraint));
        result.brTable.setColumnModel(new ConstrainedColumnModel(insettedColumnModel, rightColsConstraint));
        result.brTable.getColumnModel().setSelectionModel(new ConstrainedListSelectionModel(insettedColumnSelectionModel, rightColsConstraint));
        result.brTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // grid components setup
        JPanel tlPanel = new JPanel(new BorderLayout());
        tlPanel.add(result.tlTable.getTableHeader(), BorderLayout.NORTH);
        tlPanel.add(result.tlTable, BorderLayout.CENTER);
        JPanel trPanel = new JPanel(new BorderLayout());
        trPanel.add(result.trTable.getTableHeader(), BorderLayout.NORTH);
        trPanel.add(result.trTable, BorderLayout.CENTER);
        JPanel blPanel = new JPanel(new BorderLayout());
        blPanel.add(result.blTable, BorderLayout.CENTER);
        JPanel brPanel = new JPanel(new BorderLayout());
        brPanel.add(result.brTable, BorderLayout.CENTER);

        trPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.blue));
        tlPanel.setBorder(new MatteBorder(0, 0, 1, 1, Color.blue));
        blPanel.setBorder(new MatteBorder(0, 0, 0, 1, Color.blue));

        final JScrollPane gridScroll = new JScrollPane();
        gridScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, tlPanel);
        gridScroll.setColumnHeaderView(trPanel);
        gridScroll.setRowHeaderView(blPanel);
        gridScroll.setViewportView(brPanel);

        // test components setup
        frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(gridScroll, BorderLayout.CENTER);
        frame.setSize(800, 700);
        //frame.setVisible(true);
        frame.setTitle("Configured tests frame with full grid configured over insets, constraints and sorting");
        return result;
    }

    protected void endVisual() {
        //frame.setVisible(false);
        frame.dispose();
    }
}

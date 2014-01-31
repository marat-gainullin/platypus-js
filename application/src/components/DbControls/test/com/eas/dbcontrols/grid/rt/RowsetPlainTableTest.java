/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import java.awt.BorderLayout;
import java.text.Collator;
import java.util.Comparator;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Gala
 */
public class RowsetPlainTableTest extends GridBaseTest {

    protected class CellDataComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            o1 = ((CellData) o1).getData();
            o2 = ((CellData) o2).getData();
            if (o1 instanceof String && o2 instanceof String) {
                return Collator.getInstance().compare((String) o1, (String) o2);
            } else {
                return 0;
            }
        }
    }

    @Test
    public void rowsCrudTest() throws Exception {
        Rowset rowset = initRowset();
        TableColumnModel columnModel = new DefaultTableColumnModel();
        RowsetsTableModel model = new RowsetsTableModel(null, rowset, null, null);
        TableRowSorter sorter = new TableRowSorter(model);
        for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
            FieldModelColumn mCol = new FieldModelColumn(rowset, i, null, null, false, null, null, null);
            model.addColumn(mCol);
            TableColumn vCol = new TableColumn(i - 1, 70);
            vCol.setHeaderValue(rowset.getFields().get(i).getDescription());
            vCol.setIdentifier(mCol);
            vCol.setCellRenderer(new CellDataRenderer());
            columnModel.addColumn(vCol);
        }
        for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
            sorter.setComparator(i - 1, new CellDataComparator());
        }
        JTable tbl = new JTable(new CachingTableModel(model), columnModel);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setRowSorter(sorter);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);
        int size = rowset.size();
        assertEquals(model.getColumnCount(), rowset.getFields().getFieldsCount());

        assertEquals(rowset.size(), model.getRowCount());
        verifyTableData(model);

        rowset.absolute(rowset.size()); // eqvivalent to rowset.last();
        rowset.delete();
        assertEquals(size - 1, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        assertTrue(rowset.first());
        rowset.delete();
        assertEquals(size - 2, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        rowset.absolute(4);
        rowset.deleteAll();
        assertEquals(0, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        fillInRowset(rowset);
        assertEquals(size, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        frame.setVisible(false);
    }

    @Test
    public void columnsCrudTest() throws Exception {
        Rowset rowset = initRowset();
        TableColumnModel columnModel = new DefaultTableColumnModel();
        RowsetsTableModel model = new RowsetsTableModel(null, rowset, null, null);
        TableRowSorter sorter = new TableRowSorter(model);
        for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
            FieldModelColumn mCol = new FieldModelColumn(rowset, i, null, null, false, null, null, null);
            model.addColumn(mCol);
            TableColumn vCol = new TableColumn(i - 1, 70);
            vCol.setHeaderValue(rowset.getFields().get(i).getDescription());
            vCol.setIdentifier(mCol);
            vCol.setCellRenderer(new CellDataRenderer());
            columnModel.addColumn(vCol);
        }
        for (int i = 1; i <= rowset.getFields().getFieldsCount(); i++) {
            sorter.setComparator(i - 1, new CellDataComparator());
        }
        JTable tbl = new JTable(new CachingTableModel(model), columnModel);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setRowSorter(sorter);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);
        int size = rowset.size();
        assertEquals(model.getColumnCount(), rowset.getFields().getFieldsCount());
        assertEquals(rowset.size(), model.getRowCount());
        rowset.absolute(rowset.size()); // eqvivalent to rowset.last();
        rowset.delete();
        assertEquals(size - 1, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        rowset.absolute(4);
        rowset.deleteAll();
        assertEquals(0, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        fillInRowset(rowset);
        assertEquals(size, rowset.size());
        assertEquals(rowset.size(), model.getRowCount());

        Object value = null;
        setValueToAllCells(model, value);
        verifyValuesInAllCells(model, value);
        frame.setVisible(false);
    }
}

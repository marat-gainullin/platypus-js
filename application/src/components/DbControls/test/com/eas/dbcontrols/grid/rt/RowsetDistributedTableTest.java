/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt;

import com.bearsoft.gui.grid.data.CachingTableModel;
import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Rowset;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsTableModel;
import java.awt.BorderLayout;
import java.awt.Graphics;
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
public class RowsetDistributedTableTest extends GridBaseTest {

    private void editSomeData(JTable tbl, Rowset rowsRowset, int size, RowsetsTableModel model, TableColumnModel columnModel) throws Exception {
        synchronized (tbl) {
            rowsRowset.absolute(rowsRowset.size()); // equivalent to rowset.last();
            rowsRowset.delete();
        }
        assertEquals(size - 1, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        assertTrue(rowsRowset.first());
        synchronized (tbl) {
            rowsRowset.delete();
        }
        assertEquals(size - 2, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        synchronized (tbl) {
            int from = Double.valueOf(Math.random() * (columnModel.getColumnCount() - 2) + 1).intValue();
            int to = Double.valueOf(Math.random() * (columnModel.getColumnCount() - 2) + 1).intValue();
            columnModel.moveColumn(from, to);
            rowsRowset.absolute(4);
            rowsRowset.deleteAll();
        }
        assertEquals(0, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        synchronized (tbl) {
            fillInRowset(rowsRowset);
        }
        assertEquals(size, rowsRowset.size());
        assertEquals(rowsRowset.size(), model.getRowCount());
        synchronized (tbl) {
            verifyTableData(model);
        }
    }

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
        ApplicationDbModel dm = new ApplicationDbModel(new DummyTestDbClient());
        assertNotNull(dm);
        Rowset rowsRowset = initRowset();
        ApplicationDbEntity rowsEntity = dm.newGenericEntity();
        rowsEntity.setQuery(new DummyTestSqlQuery());
        rowsEntity.setRowset(rowsRowset);
        dm.addEntity(rowsEntity);
        TableColumnModel columnModel = new DefaultTableColumnModel();
        RowsetsTableModel model = new RowsetsTableModel(null, rowsRowset, null, null);
        TableRowSorter sorter = new TableRowSorter(model);
        for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
            Rowset colRowset = initRowset();
            ApplicationDbEntity colEntity = dm.newGenericEntity();
            dm.addEntity(colEntity);
            colEntity.setQuery(new DummyTestSqlQuery());
            colEntity.setRowset(colRowset);
            Relation colRelation = new Relation(rowsEntity, rowsRowset.getFields().get(1), colEntity, colRowset.getFields().get(1));
            dm.addRelation(colRelation);

            FieldModelColumn mCol = new FieldModelColumn(colRowset, i, null, null, false, null, null, null);
            model.addColumn(mCol);
            TableColumn vCol = new TableColumn(i - 1, 70);
            vCol.setHeaderValue(colRowset.getFields().get(i).getDescription());
            vCol.setIdentifier(mCol);
            vCol.setCellRenderer(new CellDataRenderer());
            columnModel.addColumn(vCol);
        }
        for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
            sorter.setComparator(i - 1, new CellDataComparator());
        }

        dm.setRuntime(true);
        JTable tbl = new JTable(new CachingTableModel(model), columnModel) {
            @Override
            protected synchronized void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setRowSorter(sorter);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);
        int size = rowsRowset.size();
        assertEquals(model.getColumnCount(), rowsRowset.getFields().getFieldsCount());
        synchronized (tbl) {
            verifyTableData(model);
        }
        assertEquals(rowsRowset.size(), model.getRowCount());
        for (int i = 0; i < 10; i++) {
            editSomeData(tbl, rowsRowset, size, model, columnModel);
        }
        frame.setVisible(false);
    }

    @Test
    public void editAllDataThroughTableModelTest() throws Exception {
        ApplicationDbModel dm = new ApplicationDbModel(new DummyTestDbClient());
        assertNotNull(dm);
        Rowset rowsRowset = initRowset();
        ApplicationDbEntity rowsEntity = dm.newGenericEntity();
        rowsEntity.setQuery(new DummyTestSqlQuery());
        rowsEntity.setRowset(rowsRowset);
        dm.addEntity(rowsEntity);
        TableColumnModel columnModel = new DefaultTableColumnModel();
        RowsetsTableModel model = new RowsetsTableModel(null, rowsRowset, null, null);
        TableRowSorter sorter = new TableRowSorter(model);
        for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
            Rowset colRowset = initRowset();
            ApplicationDbEntity colEntity = dm.newGenericEntity();
            dm.addEntity(colEntity);
            colEntity.setQuery(new DummyTestSqlQuery());
            colEntity.setRowset(colRowset);
            Relation colRelation = new Relation(rowsEntity, rowsRowset.getFields().get(1), colEntity, colRowset.getFields().get(1));
            dm.addRelation(colRelation);

            FieldModelColumn mCol = new FieldModelColumn(colRowset, i, null, null, false, null, null, null);
            model.addColumn(mCol);
            TableColumn vCol = new TableColumn(i - 1, 70);
            vCol.setHeaderValue(colRowset.getFields().get(i).getDescription());
            vCol.setIdentifier(mCol);
            vCol.setCellRenderer(new CellDataRenderer());
            columnModel.addColumn(vCol);
        }
        for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
            sorter.setComparator(i - 1, new CellDataComparator());
        }

        dm.setRuntime(true);
        JTable tbl = new JTable(new CachingTableModel(model), columnModel) {
            @Override
            protected synchronized void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl.setRowSorter(sorter);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(tbl), BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);
        int size = rowsRowset.size();
        assertEquals(model.getColumnCount(), rowsRowset.getFields().getFieldsCount());
        synchronized (tbl) {
            verifyTableData(model);
        }
        assertEquals(rowsRowset.size(), model.getRowCount());
        Object value = null;
        synchronized (tbl) {
            setValueToAllCells(model, value);
        }
        synchronized (tbl) {
            verifyValuesInAllCells(model, value);
        }
        frame.setVisible(false);
    }
}

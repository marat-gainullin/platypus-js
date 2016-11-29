/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.table.TableColumn;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class TabularDataChangesTest extends GridTest {

    @Test
    public void removeRowsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
            assertNull(conf.trTable.getValueAt(1, i));
        }
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
            assertNull(conf.tlTable.getValueAt(1, i));
        }
        etalonModel.deleteRow(5);
        etalonModel.deleteRow(3);
        etalonModel.deleteRow(2);
        etalonModel.deleteRow(1);
        restoreSampleData();
        endVisual();
    }

    @Test
    public void insertRowsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
            assertNull(conf.trTable.getValueAt(1, i));
        }
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
            assertNull(conf.tlTable.getValueAt(1, i));
        }
        etalonModel.insertRow(5);
        etalonModel.insertRow(3);
        etalonModel.insertRow(2);
        etalonModel.insertRow(1);
        restoreSampleData();
        endVisual();
    }

    @Test
    public void removeColumnsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
            assertNull(conf.trTable.getValueAt(1, i));
        }
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
            assertNull(conf.tlTable.getValueAt(1, i));
        }
        /*
        assertTrue(conf.tlTable.getTableHeader().isShowing());
        assertTrue(conf.tlTable.getTableHeader().getHeight() > 0);
        assertTrue(conf.trTable.getTableHeader().isShowing());
        assertTrue(conf.trTable.getTableHeader().getHeight() > 0);
        */
        for (int i = insettedColumnModel.getColumnCount() - 1; i >= 0; i--) {
            insettedColumnModel.removeColumn(insettedColumnModel.getColumn(i));
        }
        /*
        assertTrue(conf.tlTable.getTableHeader().isShowing());
        assertTrue(conf.tlTable.getTableHeader().getHeight() > 0);
        assertTrue(conf.trTable.getTableHeader().isShowing());
        assertTrue(conf.trTable.getTableHeader().getHeight() > 0);
        */
        assertEquals(columnsInset.getPreFirst() + columnsInset.getAfterLast(), insettedColumnModel.getColumnCount());
        endVisual();
    }

    @Test
    public void insertColumnsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
            assertNull(conf.trTable.getValueAt(1, i));
        }
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
            assertNull(conf.tlTable.getValueAt(1, i));
        }
        /* Commented out due to gui free nature of tests
        assertTrue(conf.tlTable.getTableHeader().isShowing());
        assertTrue(conf.tlTable.getTableHeader().getHeight() > 0);
        assertTrue(conf.trTable.getTableHeader().isShowing());
        assertTrue(conf.trTable.getTableHeader().getHeight() > 0);
        */
        for (int i = etalonColumnModel.getColumnCount() - 1; i >= 0; i--) {
            etalonColumnModel.removeColumn(etalonColumnModel.getColumn(i));
        }
        /* Commented out due to gui free nature of tests
        assertTrue(conf.tlTable.getTableHeader().isShowing());
        assertTrue(conf.tlTable.getTableHeader().getHeight() > 0);
        assertTrue(conf.trTable.getTableHeader().isShowing());
        assertTrue(conf.trTable.getTableHeader().getHeight() > 0);
        */
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn col = new TableColumn(i);
            col.setHeaderValue(columnNames[i]);
            etalonColumnModel.addColumn(col);
        }
        endVisual();
    }

    @Test
    public void updateTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
            assertNull(conf.trTable.getValueAt(1, i));
        }
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
            assertNull(conf.tlTable.getValueAt(1, i));
        }
        /*
         * Update section of this test case.
         */
        restoreSampleData();
        endVisual();
    }
}

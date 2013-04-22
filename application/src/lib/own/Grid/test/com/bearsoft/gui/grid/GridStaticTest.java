/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid;

import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.JTable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class GridStaticTest extends GridTest {

    @Test
    public void bypassTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        // insets setup
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(0, conf.tlTable.getRowCount());
        assertEquals(0, conf.tlTable.getColumnCount());
        assertEquals(0, conf.trTable.getRowCount());
        assertEquals(columnNames.length, conf.trTable.getColumnCount());
        assertEquals(sampleData.length, conf.blTable.getRowCount());
        assertEquals(0, conf.blTable.getColumnCount());
        assertEquals(sampleData.length, conf.brTable.getRowCount());
        assertEquals(columnNames.length, conf.brTable.getColumnCount());
        verifyData(conf.brTable);
        endVisual();
    }

    private void verifyData(JTable aTable) {
        assertEquals(columnNames.length, aTable.getColumnCount());
        assertEquals(sampleData.length, aTable.getRowCount());
        for (int c = 0; c < aTable.getColumnCount(); c++) {
            for (int r = 0; r < aTable.getRowCount(); r++) {
                assertEquals(sampleData[r][c], aTable.getValueAt(r, c));
            }
        }
    }

    @Test
    public void valuedInsetsTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        int addRows = 10; // additional rows
        LinearInset columnsInset = new LinearInset(2, 5);
        int addCols = 7; // additional columns
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(0, conf.tlTable.getRowCount());
        assertEquals(0, conf.tlTable.getColumnCount());
        assertEquals(0, conf.trTable.getRowCount());
        assertEquals(columnNames.length + addCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length + addRows, conf.blTable.getRowCount());
        assertEquals(0, conf.blTable.getColumnCount());
        assertEquals(sampleData.length + addRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length + addCols, conf.brTable.getColumnCount());
        for (int i = 0; i < conf.brTable.getColumnCount(); i++) {
            assertNull(conf.brTable.getValueAt(0, i));
            assertNull(conf.brTable.getValueAt(1, i));
            assertNull(conf.brTable.getValueAt(2, i));
        }
        for (int i = 0; i < conf.brTable.getRowCount(); i++) {
            assertNull(conf.brTable.getValueAt(i, 0));
            assertNull(conf.brTable.getValueAt(i, 1));
            assertNull(conf.brTable.getValueAt(i, 8));
            assertNull(conf.brTable.getValueAt(i, 9));
            assertNull(conf.brTable.getValueAt(i, 10));
            assertNull(conf.brTable.getValueAt(i, 11));
            assertNull(conf.brTable.getValueAt(i, 12));
        }
        endVisual();
    }

    @Test
    public void valuedConstraintsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        assertEquals(fixedRows, conf.tlTable.getRowCount());
        assertEquals(fixedCols, conf.tlTable.getColumnCount());
        assertEquals(fixedRows, conf.trTable.getRowCount());
        assertEquals(columnNames.length - fixedCols, conf.trTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows, conf.blTable.getRowCount());
        assertEquals(fixedCols, conf.blTable.getColumnCount());
        assertEquals(sampleData.length - fixedRows, conf.brTable.getRowCount());
        assertEquals(columnNames.length - fixedCols, conf.brTable.getColumnCount());
        endVisual();
    }

    @Test
    public void fullValuedTest() throws Exception {
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
        endVisual();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import java.util.List;
import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import java.util.ArrayList;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class RowsMovementsTest extends GridTest {

    @Test
    public void generalRowsorterTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginSortedVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        checkInsetsAreNull(conf);
        endVisual();
    }

    @Test
    public void sortByFirstModelColumnTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginSortedVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        for (int i = 1; i < conf.tlTable.getRowCount(); i++) {
            assertEquals(new Long(i), (Long)conf.tlTable.getValueAt(i, 1));
        }
        for (int i = 0; i < conf.blTable.getRowCount() - 1; i++) {
            assertEquals(new Long(i + conf.tlTable.getRowCount()), (Long)conf.blTable.getValueAt(i, 1));
        }
        SortKey sk = new SortKey(0/*in terms of the model, not the view*/, SortOrder.DESCENDING);
        List<SortKey> keys = new ArrayList<>();
        keys.add(sk);
        conf.tlTable.getRowSorter().setSortKeys(keys);
        Long k = 7l;
        for (int i = 1; i < conf.tlTable.getRowCount(); i++) {
            assertEquals(k--, (Long)conf.tlTable.getValueAt(i, 1));
        }
        for (int i = 0; i < conf.blTable.getRowCount() - 1; i++) {
            assertEquals(k--, (Long)conf.blTable.getValueAt(i, 1));
        }
        endVisual();
    }

    private void checkInsetsAreNull(ConfResult conf) {
        for (int i = 0; i < conf.tlTable.getColumnCount(); i++) {
            assertNull(conf.tlTable.getValueAt(0, i));
        }
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertNull(conf.trTable.getValueAt(0, i));
        }
        for (int i = 0; i < conf.blTable.getColumnCount(); i++) {
            assertNull(conf.blTable.getValueAt(conf.blTable.getRowCount() - 1, i));
        }
        for (int i = 0; i < conf.brTable.getColumnCount(); i++) {
            assertNull(conf.brTable.getValueAt(conf.brTable.getRowCount() - 1, i));
        }
    }
}

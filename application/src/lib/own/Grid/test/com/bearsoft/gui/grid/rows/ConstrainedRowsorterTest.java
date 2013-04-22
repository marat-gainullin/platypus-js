/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rows;

import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.JTable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class ConstrainedRowsorterTest extends GridTest {

    @Test
    public void delegating2ModelTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        ConfResult conf = beginConstrainedVisual(fixedRows, fixedCols);
        checkTableRowsorterConverting(conf.tlTable);
        checkTableRowsorterConverting(conf.trTable);
        checkTableRowsorterConverting(conf.blTable);
        checkTableRowsorterConverting(conf.brTable);
        endVisual();
    }

    private void checkTableRowsorterConverting(JTable aTable) {
        for (int i = 0; i < aTable.getRowCount(); i++) {
            int mRow = aTable.getRowSorter().convertRowIndexToModel(i);
            assertEquals(i, aTable.getRowSorter().convertRowIndexToView(mRow));
        }
    }

    @Test
    public void delegating2SorterTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        checkTableRowsorterConverting(conf.tlTable);
        checkTableRowsorterConverting(conf.trTable);
        checkTableRowsorterConverting(conf.blTable);
        checkTableRowsorterConverting(conf.brTable);
        endVisual();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.constraints;

import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class ConstraintChangesTest extends GridTest {

    @Test
    public void iterateAllCellsTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        try {
            synchronized (this) {
                for (int col = -1; col <= insettedModel.getColumnCount() + 1; col++) {
                    conf.leftColsConstraint.setMax(col);
                    conf.rightColsConstraint.setMin(col + 1);
                    col = col + 1 - 1;
                    //TODO: add applicable asserts on columns
                    for (int row = -1; row <= insettedModel.getRowCount() + 1; row++) {
                        conf.topRowsConstraint.setMax(row);
                        conf.bottomRowsConstraint.setMin(row + 1);
                        row = row + 1 - 1;
                        //TODO: add applicable asserts on rows
                    }
                }
            }
        } finally {
            endVisual();
        }
    }
}

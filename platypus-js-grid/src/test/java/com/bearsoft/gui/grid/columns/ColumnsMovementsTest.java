/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.columns;

import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.table.TableColumn;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class ColumnsMovementsTest extends GridTest {

    @Test
    public void moveIntoRightTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int moveFrom = 2;
        int moveTo = 3;

        // top right table
        TableColumn col2Move = conf.trTable.getColumnModel().getColumn(moveFrom);
        conf.trTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.trTable.getColumnModel().getColumn(moveFrom);
        conf.trTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(moveTo));

        // bottom right table
        col2Move = conf.brTable.getColumnModel().getColumn(moveFrom);
        conf.brTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.brTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.brTable.getColumnModel().getColumn(moveFrom);
        conf.brTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.brTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void moveIntoLeftTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 1;
        int moveTo = 2;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));

        // bottom left table
        col2Move = conf.blTable.getColumnModel().getColumn(moveFrom);
        conf.blTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.blTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.blTable.getColumnModel().getColumn(moveFrom);
        conf.blTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.blTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void moveIntoRightReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int moveFrom = 3;
        int moveTo = 2;

        // top right table
        TableColumn col2Move = conf.trTable.getColumnModel().getColumn(moveFrom);
        conf.trTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.trTable.getColumnModel().getColumn(moveFrom);
        conf.trTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(moveTo));

        // bottom right table
        col2Move = conf.brTable.getColumnModel().getColumn(moveFrom);
        conf.brTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.brTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.brTable.getColumnModel().getColumn(moveFrom);
        conf.brTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.brTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void moveIntoLeftReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 2;
        int moveTo = 1;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));

        // bottom left table
        col2Move = conf.blTable.getColumnModel().getColumn(moveFrom);
        conf.blTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.blTable.getColumnModel().getColumn(moveTo));

        col2Move = conf.blTable.getColumnModel().getColumn(moveFrom);
        conf.blTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertSame(col2Move, conf.blTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void leftInsetFixedTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 0;
        int moveTo = 1;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void rightInsetFixedTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 3;
        int moveTo = 4;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void leftInsetFixedReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 1;
        int moveTo = 0;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void rightInsetFixedReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 4;
        int moveTo = 3;
        // top left table
        TableColumn col2Move = conf.tlTable.getColumnModel().getColumn(moveFrom);
        conf.tlTable.getColumnModel().moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, conf.tlTable.getColumnModel().getColumn(moveTo));
        endVisual();
    }

    @Test
    public void interconstraintMovementsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 2;
        int moveTo = 3;
        TableColumn col2Move = insettedColumnModel.getColumn(moveFrom);
        TableColumn col2Move1 = insettedColumnModel.getColumn(moveTo);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, insettedColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(0));
        assertSame(col2Move1, conf.tlTable.getColumnModel().getColumn(conf.tlTable.getColumnModel().getColumnCount()-1));
        
        moveFrom = 1;
        moveTo = 4;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        col2Move1 = insettedColumnModel.getColumn(moveTo);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, insettedColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(1));
        assertSame(col2Move1, conf.trTable.getColumnModel().getColumn(0));

        moveFrom = 0;
        moveTo = 2;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, insettedColumnModel.getColumn(moveTo));

        moveFrom = 6;
        moveTo = 7;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, insettedColumnModel.getColumn(moveTo));
        endVisual();
    }

    @Test
    public void interconstraintMovementsReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 3;
        int moveTo = 2;
        TableColumn col2Move = insettedColumnModel.getColumn(moveFrom);
        TableColumn col2Move1 = insettedColumnModel.getColumn(moveTo);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, insettedColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(2));
        assertSame(col2Move1, conf.trTable.getColumnModel().getColumn(0));

        moveFrom = 4;
        moveTo = 1;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        col2Move1 = insettedColumnModel.getColumn(moveTo);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, insettedColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(1));
        assertSame(col2Move1, conf.tlTable.getColumnModel().getColumn(2));

        moveFrom = 2;
        moveTo = 0;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, insettedColumnModel.getColumn(moveTo));

        moveFrom = 7;
        moveTo = 6;
        col2Move = insettedColumnModel.getColumn(moveFrom);
        insettedColumnModel.moveColumn(moveFrom, moveTo);
        assertNotSame(col2Move, insettedColumnModel.getColumn(moveTo));
        endVisual();
    }

    @Test
    public void deepestModelMovementsTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 1;
        int moveTo = 2;
        TableColumn col2Move = etalonColumnModel.getColumn(moveFrom);
        TableColumn col2Move1 = etalonColumnModel.getColumn(moveTo);
        etalonColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, etalonColumnModel.getColumn(moveTo));
        assertSame(col2Move1, conf.tlTable.getColumnModel().getColumn(2));
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(0));

        moveFrom = 1;
        moveTo = 4;
        col2Move = etalonColumnModel.getColumn(moveFrom);
        col2Move1 = etalonColumnModel.getColumn(moveTo);
        etalonColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, etalonColumnModel.getColumn(moveTo));
        assertSame(col2Move1, conf.trTable.getColumnModel().getColumn(1));
        assertSame(col2Move, conf.trTable.getColumnModel().getColumn(2));
        endVisual();
    }

    @Test
    public void deepestModelMovementsReverseTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(1, 1);
        LinearInset columnsInset = new LinearInset(1, 1);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);

        int moveFrom = 2;
        int moveTo = 1;
        TableColumn col2Move = etalonColumnModel.getColumn(moveFrom);
        TableColumn col2Move1 = etalonColumnModel.getColumn(moveTo);
        etalonColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, etalonColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(2));
        assertSame(col2Move1, conf.trTable.getColumnModel().getColumn(0));

        moveFrom = 4;
        moveTo = 1;
        col2Move = etalonColumnModel.getColumn(moveFrom);
        col2Move1 = etalonColumnModel.getColumn(moveTo);
        etalonColumnModel.moveColumn(moveFrom, moveTo);
        assertSame(col2Move, etalonColumnModel.getColumn(moveTo));
        assertSame(col2Move, conf.tlTable.getColumnModel().getColumn(2));
        assertSame(col2Move1, conf.trTable.getColumnModel().getColumn(0));
        endVisual();
    }
}

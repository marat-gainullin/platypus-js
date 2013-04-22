/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.selection;

import com.bearsoft.gui.grid.GridTest;
import com.bearsoft.gui.grid.insets.LinearInset;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gala
 */
public class ConstraintedSelectionTest extends GridTest {

    @Test
    public void changeConstrainedSelectionTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        LinearInset columnsInset = new LinearInset(2, 5);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        insettedColumnSelectionModel.setSelectionInterval(0, insettedColumnModel.getColumnCount() - 1);
        insettedSelectionModel.setSelectionInterval(0, 0);
        insettedColumnSelectionModel.setSelectionInterval(1, 1);
        insettedColumnSelectionModel.setSelectionInterval(0, 0);
        assertTrue(conf.tlTable.getSelectionModel().isSelectedIndex(0));
        assertTrue(conf.tlTable.getColumnModel().getSelectionModel().isSelectedIndex(0));
        endVisual();
    }

    @Test
    public void constraintedOverInsettedSelectionTest() throws Exception {
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        LinearInset columnsInset = new LinearInset(2, 5);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        insettedColumnSelectionModel.setSelectionInterval(0, insettedColumnModel.getColumnCount() - 1);
        insettedSelectionModel.setSelectionInterval(0, 1);
        for (int i = 0; i < columnsInset.getPreFirst(); i++) {
            assertTrue(conf.tlTable.getColumnModel().getSelectionModel().isSelectedIndex(i));
            assertTrue(conf.tlTable.getSelectionModel().isSelectedIndex(0));
            assertTrue(conf.tlTable.getSelectionModel().isSelectedIndex(1));
        }
        for (int i = 0; i < conf.trTable.getColumnCount(); i++) {
            assertTrue(conf.trTable.getColumnModel().getSelectionModel().isSelectedIndex(i));
            assertTrue(conf.trTable.getSelectionModel().isSelectedIndex(0));
            assertTrue(conf.trTable.getSelectionModel().isSelectedIndex(1));
        }
        endVisual();
    }
    protected int columnSelectionEventsCount = 0;
    protected int columnSelectionEventsCount1 = 0;

    @Test
    public void constraintedColumnsSelection2ColumnModelEventsChainTest() throws Exception {
        columnSelectionEventsCount = 0;
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        LinearInset columnsInset = new LinearInset(2, 5);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        insettedColumnSelectionModel.setSelectionInterval(0, insettedColumnModel.getColumnCount() - 1);
        insettedSelectionModel.setSelectionInterval(0, 0);
        insettedColumnSelectionModel.setSelectionInterval(1, 1);
        insettedColumnSelectionModel.setSelectionInterval(0, 0);
        assertTrue(conf.tlTable.getSelectionModel().isSelectedIndex(0));
        assertTrue(conf.tlTable.getColumnModel().getSelectionModel().isSelectedIndex(0));
        conf.tlTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                columnSelectionEventsCount++;
            }
        });
        insettedColumnSelectionModel.setSelectionInterval(1, 1);
        insettedColumnSelectionModel.setSelectionInterval(0, 0);
        assertEquals(4, columnSelectionEventsCount);
        endVisual();
    }
    @Test
    public void insettedColumnsSelection2ColumnModelEventsChainTest() throws Exception {
        columnSelectionEventsCount1 = 0;
        int fixedRows = 2;
        int fixedCols = 3;
        // insets setup
        LinearInset rowsInset = new LinearInset(3, 7);
        LinearInset columnsInset = new LinearInset(2, 5);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        insettedColumnSelectionModel.setSelectionInterval(0, insettedColumnModel.getColumnCount() - 1);
        insettedSelectionModel.setSelectionInterval(0, 0);
        insettedColumnSelectionModel.setSelectionInterval(1, 1);
        insettedColumnSelectionModel.setSelectionInterval(0, 0);
        assertTrue(conf.tlTable.getSelectionModel().isSelectedIndex(0));
        assertTrue(conf.tlTable.getColumnModel().getSelectionModel().isSelectedIndex(0));
        insettedColumnModel.addColumnModelListener(new TableColumnModelListener() {

            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                columnSelectionEventsCount1++;
            }
        });
        insettedColumnSelectionModel.setSelectionInterval(1, 1);
        insettedColumnSelectionModel.setSelectionInterval(0, 0);
        assertEquals(2, columnSelectionEventsCount1);
        endVisual();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.insets;

import com.bearsoft.gui.grid.GridTest;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class InsetChangesTest extends GridTest {

    //TODO: add appropriate asserts for all four cases.

    @Test
    public void changeLeftTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int left=0;
        columnsInset.setPreFirst(left++);
        columnsInset.setPreFirst(left++);
        columnsInset.setPreFirst(left++);
        columnsInset.setPreFirst(left++);
        columnsInset.setPreFirst(--left);
        columnsInset.setPreFirst(--left);
        columnsInset.setPreFirst(--left);
        columnsInset.setPreFirst(--left);
        endVisual();
    }

    @Test
    public void changeRightTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int right=0;
        columnsInset.setAfterLast(right++);
        columnsInset.setAfterLast(right++);
        columnsInset.setAfterLast(right++);
        columnsInset.setAfterLast(right++);
        columnsInset.setAfterLast(--right);
        columnsInset.setAfterLast(--right);
        columnsInset.setAfterLast(--right);
        columnsInset.setAfterLast(--right);
        endVisual();
    }
    @Test
    public void changeTopTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int top=0;
        rowsInset.setPreFirst(top++);
        rowsInset.setPreFirst(top++);
        rowsInset.setPreFirst(top++);
        rowsInset.setPreFirst(top++);
        rowsInset.setPreFirst(--top);
        rowsInset.setPreFirst(--top);
        rowsInset.setPreFirst(--top);
        rowsInset.setPreFirst(--top);
        endVisual();
    }
    @Test
    public void changeBottomTest() throws Exception {
        int fixedRows = 0;
        int fixedCols = 0;
        LinearInset rowsInset = new LinearInset(0, 0);
        LinearInset columnsInset = new LinearInset(0, 0);
        ConfResult conf = beginVisual(fixedRows, fixedCols, rowsInset, columnsInset);
        int bottom=0;
        rowsInset.setAfterLast(bottom++);
        rowsInset.setAfterLast(bottom++);
        rowsInset.setAfterLast(bottom++);
        rowsInset.setAfterLast(bottom++);
        rowsInset.setAfterLast(--bottom);
        rowsInset.setAfterLast(--bottom);
        rowsInset.setAfterLast(--bottom);
        rowsInset.setAfterLast(--bottom);
        endVisual();
    }
}

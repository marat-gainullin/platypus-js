/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.columns;

import javax.swing.table.TableColumn;

/**
 * Interface intended for insets changes processing and filling inset columns space.
 * When one of the insets is changed there is a need to synchronize
 * columns content of <code>InsettedColumnModel</code>'s left and right inset sets of columns.
 * @author mg
 * @see InsettedColumnModel
 */
public interface InsettedColumnsFactory {

    /**
     * Creates a column while filling left inset of corresponding InsettedColumnModel
     * @param aInsetSize Size of left inset.
     * @param aNewInsettedColumnIndex Index of the column being created.
     * @return New <code>TableColumn</code> instance.
     * @see InsettedColumnModel
     * @see TableColumn
     */
    public TableColumn createLeft(int aInsetSize, int aNewInsettedColumnIndex);

    /**
     * Creates a column while filling right inset of corresponding <code>InsettedColumnModel</code>
     * @param aInsetSize Size of right inset.
     * @param aNewInsettedColumnIndex Index of the column being created.
     * @return New <code>TableColumn</code> instance.
     * @see InsettedColumnModel
     * @see TableColumn
     */
    public TableColumn createRight(int aInsetSize, int aNewInsettedColumnIndex);
}

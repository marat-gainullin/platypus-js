/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.columns;

import com.bearsoft.gui.grid.insets.InsetPart;
import javax.swing.table.TableColumn;

/**
 * The default implementation of the <code>InsettedColumnsFactory</code> interface
 * @see InsettedColumnsFactory
 * @author mg
 */
public class DefaultInsettedColumnsFactory implements InsettedColumnsFactory{

    /**
     * {@inheritDoc}
     */
    @Override
    public TableColumn createLeft(int aInsetSize, int aNewInsettedColumnIndex) {
        TableColumn col = new TableColumn(aNewInsettedColumnIndex-aInsetSize);
        col.setHeaderValue(" ");
        return col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableColumn createRight(int aInsetSize, int aNewInsettedColumnIndex) {
        TableColumn col = new TableColumn(aNewInsettedColumnIndex+InsetPart.AFTER_INSET_BIAS);
        col.setHeaderValue(" ");
        return col;
    }
}

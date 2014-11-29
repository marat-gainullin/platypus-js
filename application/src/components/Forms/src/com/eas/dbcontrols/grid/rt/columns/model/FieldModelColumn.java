/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.model;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowsetListener;
import com.eas.client.forms.api.components.model.ScalarDbControl;
import com.eas.dbcontrols.grid.rt.HasStyle;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Model's column, bound with some additional information about how to achieve
 * model data. It holds a reference to corresponding view's column. It is used
 * when model's columns are added or removed. In such case, view's column's
 * model index become invalid and view columns need to be reindexed.
 *
 * @author mg
 */
public class FieldModelColumn extends ModelColumn {

    protected int rowsetField;
    protected RowsetListener rowsetListener;

    /**
     * Constructs model column, bounded with view's column.
     *
     * @param aRowset Rowset instance to be used as data source of the model's
     * column.
     * @param aColumnIndex Rowset's field index.
     * @param aCellsHander
     * @param aSelectHandler
     * @param aReadOnly
     * @param aStyleHost
     * @param aView
     * @param aEditor
     * @see Rowset
     */
    public FieldModelColumn(Rowset aRowset, int aColumnIndex, JSObject aCellsHander, JSObject aSelectHandler, boolean aReadOnly, HasStyle aStyleHost, ScalarDbControl aView, ScalarDbControl aEditor) {
        super(aRowset, aCellsHander, aSelectHandler, aReadOnly, aStyleHost, aView, aEditor);
        rowsetField = aColumnIndex;
    }

    /**
     * Returns a column number, bounded to this model column.
     *
     * @return Rowset's column number. 1-based.
     */
    public int getRowsetField() {
        return rowsetField;
    }

    /**
     * Adds the specified rowset listener to column's internal rowset. The
     * FieldModelColumn instance can't do it by itself because listener needs to
     * know about a model, but column instance should not to know such things.
     * So we need to let some outer code to organize a listener.
     *
     * @param aListener
     */
    public void setRowsetListener(RowsetListener aListener) {
        if (rowsetListener != null) {
            rowset.removeRowsetListener(rowsetListener);
        }
        rowsetListener = aListener;
        if (rowsetListener != null) {
            rowset.addRowsetListener(rowsetListener);
        }
    }
}

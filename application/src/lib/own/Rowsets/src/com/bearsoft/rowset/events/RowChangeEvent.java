/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;

/**
 * This event occurs before and after row changing.
 * Sometimes, fields's value changing lead to moving changed row from one subset of rows to another.
 * This might occur while rowsets are used in conjunction with filters.
 * @author mg
 * @see RowsetListener
 */
public class RowChangeEvent extends RowsetEvent{

    protected int oldRowCount;
    protected int newRowCount;
    protected Row changedRow;
    protected int fieldIndex;
    protected Object oldValue;
    protected Object newValue;

    /**
     * <code>RowChangeEvent</code> constructor.
     * @param source Rowset changes are made to. It's nessasary to hold information about <code>Rowset</code> because of filtering.
     * @param aFieldIndex Index of changing field. Index is 1 based.
     * @param aOldValue Value that is to be changed
     * @param aNewValue Value the field's value is to be changed to.
     * @param aKind Event kind (before or after)
     * @see RowsetEventMoment
     */
    public RowChangeEvent(Rowset source, Row aChangedRow, int aFieldIndex, Object aOldValue, Object aNewValue, RowsetEventMoment aKind)
    {
        super(source, aKind);
        changedRow = aChangedRow;
        fieldIndex = aFieldIndex;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    /**
     * Returns changed row.
     * @return Changhed row, this event represents.
     */
    public Row getChangedRow() {
        return changedRow;
    }

    /**
     * Returns rows count in the rowset before changing the field's value.
     * @return Rows count in the rowset before changing the field's value.
     */
    public int getOldRowCount()
    {
        return oldRowCount;
    }

    /**
     * Sets rows count in the rowset before changing the field's value.
     * @param oldRowCount Rows count in the rowset before changing the field's value.
     */
    public void setOldRowCount(int aValue)
    {
        oldRowCount = aValue;
    }

    /**
     * Returns rows count in the rowset after changing the field's value.
     * @return Rows count in the rowset after changing the field's value.
     */
    public int getNewRowCount()
    {
        return newRowCount;
    }

    /**
     * Sets rows count in the rowset after changing the field's value.
     * @param aValue Rows count in the rowset after changing the field's value.
     */
    public void setNewRowCount(int aValue)
    {
        newRowCount = aValue;
    }

    /**
     * Returns field index of changing field.
     * @return Field index of changing field.
     */
    public int getFieldIndex()
    {
        return fieldIndex;
    }

    /**
     * Returns value that would be setted to changing field.
     * @return Value that would be setted to changing field.
     */
    public Object getNewValue()
    {
        return newValue;
    }

    /**
     * Returns value that would be replaced by new value.
     * @return Value that would be replaced by new value.
     */
    public Object getOldValue()
    {
        return oldValue;
    }
}

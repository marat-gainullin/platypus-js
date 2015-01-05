/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.Row;

/**
 * RowWrap is intended to store information about row's ordinal number in underlying rowset.
 * @author mg
 */
public class RowWrap {

    protected Row row;
    protected int index;

    /**
     * Default constructor
     * @param aRow Row for which we build ordering
     */
    public RowWrap(Row aRow)
    {
        super();
        row = aRow;
    }

    /**
     * Constructor, assosiating row index with <code>aRow</code>.
     * @param aRow Row for which we build ordering
     * @param aRowIndex Row index to assosiate with <code>aRow</code>.
     */
    public RowWrap(Row aRow, int aRowIndex)
    {
        this(aRow);
        index = aRowIndex;
    }

    /**
     * Returns index assosiated with the row.
     * @return Index assosiated with the row.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Sets row assoosiated index.
     * @param index Value to set.
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * Returns the row.
     * @return The row.
     */
    public Row getRow()
    {
        return row;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Event that occurs when rowset is requeried.
 * Event testifies that rowset have been requeried and al of it's contents have been refreshed.
 * @author mg
 */
public class RowsetRequeryEvent extends RowsetEvent{

    /**
     * Rowset requeried event constructor.
     * @param source Rowset the row is inserted to.
     * @param aKind Event kind (before or after)
     * @see RowsetEventMoment
     */
    public RowsetRequeryEvent(Rowset source, RowsetEventMoment aKind)
    {
        super(source, aKind);
    }
}

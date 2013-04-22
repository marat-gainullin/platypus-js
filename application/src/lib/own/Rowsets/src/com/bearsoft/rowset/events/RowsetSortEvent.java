/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Rowset sort event. Occurs before and after the rowset is sorted.
 * @author mg.
 * @see RowsetListener
 */
public class RowsetSortEvent extends RowsetEvent{

    /**
     * The event constructor.
     */
    public RowsetSortEvent(Rowset aRowset, RowsetEventMoment aKind)
    {
        super(aRowset, aKind);
    }
}

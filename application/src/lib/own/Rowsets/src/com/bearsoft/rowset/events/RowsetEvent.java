/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Base class for all rowset's events.
 * @author mg
 */
public class RowsetEvent{

    protected Rowset rowset;
    protected RowsetEventMoment kind;

    /**
     * <code>RowsetEvent</code> class constructor
     * @param source Rowset the events are propagated from.
     * @param aKind Event kind (before or after)
     * @see RowsetEventMoment
     */
    public RowsetEvent(Rowset source, RowsetEventMoment aKind)
    {
        super();
        rowset = source;
        kind = aKind;
    }

    public Rowset getRowset() {
        return rowset;
    }
}

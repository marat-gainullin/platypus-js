/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.ordering.Orderer;

/**
 * Event that occurs before and after <code>Rowset</code> is filtered
 *
 * @author mg
 */
public class RowsetFilterEvent extends RowsetEvent {

    protected Orderer filter;

    /**
     * Event constructor.
     *
     * @param source Rowset the events are propagated from.
     * @param aFilter Filter setted to Rowset as current(active) filter after
     * filtering has been performed if aKind equals to RowsetEventMoment.AFTER
     * or before filtering is performed is aKind equals to
     * RowsetEventMoment.BEFORE.
     * @param aKind Event kind (before or after)
     * @see RowsetEventMoment
     */
    public RowsetFilterEvent(Rowset source, Orderer aFilter, RowsetEventMoment aKind) {
        super(source, aKind);
        filter = aFilter;
    }
}

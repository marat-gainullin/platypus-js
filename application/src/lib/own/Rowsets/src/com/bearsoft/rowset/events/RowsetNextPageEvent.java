/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Event that occurs when rowset is filled with next page of data. Event
 * testifies that rowset have been scrolled to the next page and all of it's
 * contents have been refreshed.
 *
 * @author mg
 */
public class RowsetNextPageEvent extends RowsetRequeryEvent {

    /**
     * Rowset next page event constructor.
     *
     * @param source Rowset the row is inserted to.
     * @param aKind Event kind (before or after)
     * @see RowsetEventMoment
     */
    public RowsetNextPageEvent(Rowset source, RowsetEventMoment aKind) {
        super(source, aKind);
    }
}

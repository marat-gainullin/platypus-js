/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;

/**
 * Delete row event class. Occurs before and after row is deleted from the rowset.
 * @author mg
 * @see RowsetListener
 */
public class RowsetDeleteEvent extends RowsetEvent {

    protected Row row = null;
    protected boolean ajusting;

    /**
     * Rowset delete event constructor.
     * @param source Rowset the row is deleted from.
     * @param aRow The rows is deleted.
     * @param aKind Event kind (before or after)
     * @param aAjusting Flag, indicating that this event is a serias of similar events element.
     * @see RowsetEventMoment
     */
    public RowsetDeleteEvent(Rowset source, Row aRow, RowsetEventMoment aKind, boolean aAjusting)
    {
        super(source, aKind);
        row = aRow;
        ajusting = aAjusting;
    }

    public Row getRow() {
        return row;
    }

    /**
     * Returns whether this event is an element of series of similar events.
     * In general, events with true ajusting flag occur when a series of simmilar operations are performed.
     * Operations are deleting or inserting rows in a rowset. Note, that in general, last event in a series
     * have no this flag. I.e. last event is not ajusted event.
     * @return True if this event is a series element.
     */
    public boolean isAjusting() {
        return ajusting;
    }
}

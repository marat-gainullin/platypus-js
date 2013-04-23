/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 * Rowset save event. Occurs before and after rowset is saved.
 * @author mg
 * @see RowsetListener
 */
public class RowsetSaveEvent extends RowsetEvent{

    /**
     * The event constructor.
     */
    public RowsetSaveEvent(Rowset source)
    {
        super(source, RowsetEventMoment.AFTER);
    }
}

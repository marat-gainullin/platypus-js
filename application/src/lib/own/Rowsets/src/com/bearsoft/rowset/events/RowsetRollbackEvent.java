/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 *
 * @author mg
 */
public class RowsetRollbackEvent extends RowsetEvent{
    
    /**
     * The event constructor.
     */
    public RowsetRollbackEvent(Rowset source)
    {
        super(source, RowsetEventMoment.AFTER);
    }
}

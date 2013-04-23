package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

public class RowsetRollbackEvent extends RowsetEvent{

    /**
     * The event constructor.
     */
    public RowsetRollbackEvent(Rowset source)
    {
        super(source, RowsetEventMoment.AFTER);
    }
}

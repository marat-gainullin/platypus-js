/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;

/**
 * Insert row event class. Occurs before and after row is inserted into the
 * rowset.
 *
 * @author mg
 * @see RowsetListener
 */
public class RowsetInsertEvent extends RowsetDeleteEvent {

    /**
     * Rowset insert event constructor.
     *
     * @param source Rowset the row is inserted to.
     * @param aRow The rows is inserted.
     * @param aKind Event kind (before or after)
     * @param aAjusting Ajusting flag. see <code>RowsetDeleteEvent</code>
     * javadoc.
     * @see RowsetEventMoment
     * @see RowsetDeleteEvent
     */
    public RowsetInsertEvent(Rowset source, Row aRow, RowsetEventMoment aKind, boolean aAjusting) {
        super(source, aRow, aKind, aAjusting);
    }

}

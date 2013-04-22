/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.veers;

import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;

/**
 * Column's rowset listener. It listen events from column's rowset when it is
 * not a veer rowset. Veer rowsets, that serve as column sources are handled by
 * column riddler. Cells rowset events are handled by another rowset listener.
 *
 * @author Gala
 * @see ColumnsRiddler
 */
public class ColumnRowsetListener extends RowsetAdapter {

    protected RowsetsModel model;
    protected ModelColumn column;

    public ColumnRowsetListener(RowsetsModel aModel, ModelColumn aColumn) {
        super();
        model = aModel;
        column = aColumn;
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        model.fireColumnRowsetChanged(column);
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        model.fireColumnRowsetChanged(column);
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        model.fireColumnRowsetChanged(column);
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        model.fireColumnRowsetChanged(column);
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireColumnRowsetChanged(column);
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireColumnRowsetChanged(column);
    }
}

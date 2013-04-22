/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.veers;

import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.models.RowsetsModel;

/**
 *
 * @author Gala
 */
public class CellsRowsetsListener extends RowsetAdapter {

    protected RowModelColumn column;
    protected RowsetsModel model;

    public CellsRowsetsListener(RowsetsModel aModel, RowModelColumn aColumn) {
        super();
        model = aModel;
        column = aColumn;
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        model.fireRowsDataChanged();
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        if (!event.isAjusting()) {
            model.fireRowsDataChanged();
        }
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        if (!event.isAjusting()) {
            model.fireRowsDataChanged();
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireRowsDataChanged();
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireRowsDataChanged();
    }
}

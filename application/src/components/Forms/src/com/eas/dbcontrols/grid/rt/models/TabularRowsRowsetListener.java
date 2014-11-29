/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;

/**
 *
 * @author Gala
 */
public class TabularRowsRowsetListener extends RowsetAdapter {

    protected RowsetsTableModel model;

    public TabularRowsRowsetListener(RowsetsTableModel aModel) {
        super();
        model = aModel;
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        Rowset rowset = event.getRowset();
        model.fireRowInserted(rowset.getCursorPos() - 1);
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        Rowset rowset = event.getRowset();
        if (rowset.isEmpty()) {
            model.fireRowsChanged();
        } else {
            model.fireRowDeleted(rowset.getCursorPos() - 1);
        }
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        if (event.getNewRowCount() != event.getOldRowCount()) {
            model.fireRowsChanged();
        } else {
            model.fireRowsRowsetFieldChanged(event.getFieldIndex());
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        model.fireRowsChanged();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        model.fireRowsChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireRowsChanged();
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireRowsChanged();
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        model.fireRowsDataChanged();
    }
}

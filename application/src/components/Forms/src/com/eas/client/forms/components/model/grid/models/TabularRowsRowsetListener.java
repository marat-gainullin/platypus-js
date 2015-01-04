/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.metadata.Field;

/**
 *
 * @author mg
 */
public class TabularRowsRowsetListener extends RowsetAdapter {

    protected ArrayTableModel model;

    public TabularRowsRowsetListener(ArrayTableModel aModel) {
        super();
        model = aModel;
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        Rowset rowset = event.getRowset();
        model.fireElementInserted(rowset.getCursorPos() - 1);
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        Rowset rowset = event.getRowset();
        if (rowset.isEmpty()) {
            model.fireElementsChanged();
        } else {
            model.fireElementDeleted(rowset.getCursorPos() - 1);
        }
    }
/*
    @Override
    public void rowChanged(RowChangeEvent event) {
        if (event.getNewRowCount() != event.getOldRowCount()) {
            model.fireElementsChanged();
        } else {
            Field field = event.getChangedRow().getFields().get(event.getFieldIndex());
            model.fireElementsFieldChanged(field.getName());
        }
    }
*/
    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        model.fireElementsChanged();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        model.fireElementsChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireElementsChanged();
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireElementsChanged();
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        model.fireElementsChanged();
    }
}

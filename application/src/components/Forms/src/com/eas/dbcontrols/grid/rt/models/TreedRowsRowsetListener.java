/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gala
 */
public class TreedRowsRowsetListener extends RowsetAdapter {

    protected RowsetsTreedModel model;
    protected List<Row> oldRows = new ArrayList<>();

    public TreedRowsRowsetListener(RowsetsTreedModel aModel) {
        super();
        model = aModel;
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        model.fireRowsRowsetRowsChanged(event.getChangedRow(), event.getFieldIndex(), false);
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        oldRows.add(event.getRow());
        if (!event.isAjusting()) {
            model.fireElementsRemoved(oldRows);
            oldRows.clear();
        }
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        oldRows.add(event.getRow());
        if (!event.isAjusting()) {
            model.fireElementsAdded(oldRows);
            oldRows.clear();
        }
        Row parentRow = model.getParentOf(event.getRow());
        if (parentRow != null && parentRow.getColumnCount() > 0) {
            model.fireRowsRowsetRowsChanged(parentRow, 1, event.isAjusting());
        }
    }

    @Override
    public boolean willFilter(RowsetFilterEvent event) {
        oldRows.clear();
        oldRows.addAll(model.getRowsRowset().getCurrent());
        return super.willFilter(event);
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        List<Row> oldRows1 = new ArrayList<>();
        oldRows1.addAll(oldRows);
        oldRows1.removeAll(model.getRowsRowset().getCurrent());
        model.fireElementsRemoved(oldRows1);

        List<Row> newRows = new ArrayList<>();
        newRows.addAll(model.getRowsRowset().getCurrent());
        newRows.removeAll(oldRows);
        model.fireElementsAdded(newRows);

        oldRows.clear();
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent event) {
        oldRows.clear();
        oldRows.addAll(model.getRowsRowset().getCurrent());
        return super.willRequery(event);
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        model.fireElementsRemoved(oldRows);
        oldRows.clear();
        model.fireElementsAdded(model.getRowsRowset().getCurrent());
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        model.fireElementsRemoved(oldRows);
        oldRows.clear();
        model.fireElementsAdded(model.getRowsRowset().getCurrent());
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent event) {
        oldRows.clear();
        oldRows.addAll(model.getRowsRowset().getCurrent());
        return super.willNextPageFetch(event);
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        List<Row> currentRows = new ArrayList<>();
        currentRows.addAll(model.getRowsRowset().getCurrent());
        boolean changed = currentRows.removeAll(oldRows);
        assert changed;
        model.fireElementsAdded(currentRows);
        oldRows.clear();
    }
}

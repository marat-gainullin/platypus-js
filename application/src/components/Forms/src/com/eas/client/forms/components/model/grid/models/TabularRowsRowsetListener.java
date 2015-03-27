/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

/**
 *
 * @author mg
 */
public class TabularRowsRowsetListener {

    protected ArrayTableModel model;

    public TabularRowsRowsetListener(ArrayTableModel aModel) {
        super();
        model = aModel;
    }
/*
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
    */
}

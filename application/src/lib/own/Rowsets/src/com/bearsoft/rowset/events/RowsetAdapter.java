/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

/**
 * This class is intended to be a default implementor of the <code>RowsetListener</code> interface.
 * There are much number of method that may veto any rowset's operation. And there is a danger to implement that interface by youself and veto some operation without knowing about it.
 * This default implementor garateers that it wouldn't happen.
 * @author mg
 */
public class RowsetAdapter implements RowsetListener {

    @Override
    public boolean willScroll(RowsetScrollEvent event) {
        return true;
    }

    @Override
    public boolean willFilter(RowsetFilterEvent event) {
        return true;
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent event) {
        return true;
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent event) {
        return true;
    }

    @Override
    public boolean willInsertRow(RowsetInsertEvent event) {
        return true;
    }

    @Override
    public boolean willChangeRow(RowChangeEvent event) {
        return true;
    }

    @Override
    public boolean willDeleteRow(RowsetDeleteEvent event) {
        return true;
    }

    @Override
    public boolean willSort(RowsetSortEvent event) {
        return true;
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.events.*;

/**
 *
 * @author mg
 */
public class EntityRefreshFilterDataListener extends RowsetAdapter {

    protected int events;

    public EntityRefreshFilterDataListener() {
        super();
    }

    protected void incEvents() {
        events++;
    }

    public void reset() {
        events = 0;
    }

    public int getEvents() {
        return events;
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        incEvents();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        incEvents();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
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
    public void rowDeleted(RowsetDeleteEvent event) {
    }
}

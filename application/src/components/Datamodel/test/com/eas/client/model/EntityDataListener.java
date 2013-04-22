/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.events.*;

/**
 *
 * @author mg
 */
public class EntityDataListener extends RowsetAdapter {

    protected int events;
    protected int scrollEvents;

    public EntityDataListener() {
        super();
    }

    protected void incEvents() {
        events++;
    }

    public void reset() {
        events = 0;
        scrollEvents = 0;
    }

    public int getEvents() {
        return events;
    }

    public int getScrollEvents() {
        return scrollEvents;
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
    public boolean willScroll(RowsetScrollEvent event) {
        scrollEvents++;
        return true;
    }

    @Override
    public boolean willChangeRow(RowChangeEvent event) {
        incEvents();
        return true;
    }

    @Override
    public boolean willInsertRow(RowsetInsertEvent event) {
        incEvents();
        return true;
    }

    @Override
    public boolean willDeleteRow(RowsetDeleteEvent event) {
        incEvents();
        return true;
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
        scrollEvents++;
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        incEvents();
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        incEvents();
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        incEvents();
    }
}

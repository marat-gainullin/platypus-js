package com.bearsoft.rowset.events;

import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class RowsetJSAdapter extends RowsetAdapter implements RowsetEventsEarlyAccess {

    public JSObject rowsetFiltered;
    public JSObject rowsetRequeried;
    public JSObject rowsetNextPageFetched;
    public JSObject rowsetSaved;
    public JSObject rowsetRolledback;
    public JSObject rowsetScrolled;
    public JSObject rowsetSorted;
    public JSObject rowInserted;
    public JSObject rowChanged;
    public JSObject rowDeleted;

    @Override
    public void rowChanged(RowChangeEvent event) {
        executeEvent(rowChanged, event);
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        executeEvent(rowInserted, event);
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        executeEvent(rowDeleted, event);
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        executeEvent(rowsetFiltered, event);
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        executeEvent(rowsetNextPageFetched, event);
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        executeEvent(rowsetRequeried, event);
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        executeEvent(rowsetRolledback, event);
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
        executeEvent(rowsetSaved, event);
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
        executeEvent(rowsetScrolled, event);
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        executeEvent(rowsetSorted, event);
    }

    protected void executeEvent(JSObject aHandler, RowsetEvent aEvent) {
        if (aHandler != null) {
            aHandler.call(null, new Object[]{aEvent});
        }
    }
}

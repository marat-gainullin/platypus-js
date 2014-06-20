/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Class intended to hold Rowset's listeners and fire Rowset's events.
 *
 * @author mg
 */
public class RowsetChangeSupport {

    protected Set<RowsetListener> rowsetListeners = new HashSet<>();
    protected Rowset source;

    /**
     * The constructor.
     *
     * @param aRowset The events source.
     */
    public RowsetChangeSupport(Rowset aRowset) {
        super();
        source = aRowset;
    }

    /**
     * Registers <code>RowsetListener</code> on this
     * <code>RowsetChangeSupport</code>.
     *
     * @param aListener <code>RowsetListener</code> to be registered.
     */
    public void addRowsetListener(RowsetListener aListener) {
        rowsetListeners.add(aListener);
    }

    /**
     * Removes <code>RowsetListener</code> from this
     * <code>RowsetChangeSupport</code>.
     *
     * @param aListener <code>RowsetListener</code> to be removed.
     */
    public void removeRowsetListener(RowsetListener aListener) {
        rowsetListeners.remove(aListener);
    }

    /**
     * Returns a set of <code>RowsetListener</code> instances.
     *
     * @return <code>RowsetListener</code> instances set.
     */
    public Set<RowsetListener> getRowsetListeners() {
        return rowsetListeners;
    }

    public void setRowsetListeners(Set<RowsetListener> aListeners) {
        rowsetListeners = aListeners;
    }

    /**
     * Fires <code>willScroll</code> event to all registered listeners. The
     * scrolling might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aNewRowIndex Row index that will be cursor position when scrolling
     * will be performed.
     * @return Whether event source may perform the scrolling.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillScrollEvent(int aNewRowIndex) throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty() && source.getCursorPos() != aNewRowIndex) {
            RowsetScrollEvent event = new RowsetScrollEvent(source, source.getCursorPos(), aNewRowIndex, RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willScroll(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willSort</code> event to all registered listeners. The
     * sorting might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @return Whether event source may perform the sorting.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillSortEvent() throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetSortEvent event = new RowsetSortEvent(source, RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willSort(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willRequery</code> event to all registered listeners. The
     * requering might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @return Whether event source may perform the requering.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillRequeryEvent() throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetRequeryEvent event = new RowsetRequeryEvent(source, RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willRequery(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willNextPageFetch</code> event to all registered listeners.
     * The fetching might be vetoed by one of the registered listeners.
     * Nevetheless the event will be propagated to all the registered listeners.
     *
     * @return Whether event source may perform the paging.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillNextPageEvent() throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetNextPageEvent event = new RowsetNextPageEvent(source, RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willNextPageFetch(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willFilter</code> event to all registered listeners. The
     * filtering might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @return Whether event source may perform the filtering.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillFilterEvent() throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetFilterEvent event = new RowsetFilterEvent(source, source.getActiveFilter(), RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willFilter(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willChange</code> event to all registered listeners. The
     * changing might be vetoed by one of the registered listeners. Nevertheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aChangedRow
     * @param aFieldIndex Rowset's column(field) index the changing will be
     * performed at.
     * @param aOldValue The old value of the updating column.
     * @param aNewValue The value that is to be setted to the updating column.
     * @return Whether event source may perform the changing.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillChangeEvent(Row aChangedRow, int aFieldIndex, Object aOldValue, Object aNewValue) throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowChangeEvent event = new RowChangeEvent(source, aChangedRow, aFieldIndex, aOldValue, aNewValue, RowsetEventMoment.BEFORE);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willChangeRow(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Fires <code>willInsert</code> event to all registered listeners. The
     * inserting might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aRow The row is to be inserted to the rowset.
     * @return Whether event source may perform the inserting.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillInsertEvent(Row aRow) throws InvalidCursorPositionException {
        return fireWillInsertEvent(aRow, false);
    }

    /**
     * Fires <code>willInsert</code> event to all registered listeners. The
     * inserting might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aRow The row is to be inserted to the rowset.
     * @param aAjusting Flag, indicating that the event is a series of similar
     * events element.
     * @return Whether event source may perform the inserting.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillInsertEvent(Row aRow, boolean aAjusting) throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetInsertEvent event = new RowsetInsertEvent(source, aRow, RowsetEventMoment.BEFORE, aAjusting);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willInsertRow(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                        source.wideCheckCursor();
                    }
                }
            }
        }
        return res;
    }

    /**
     * Private utility method for restoring rowset's position.
     *
     * @param oldRowPos Position to be setted to the Rowset.
     */
    private void restoreRowsetPosition(int oldRowPos) throws InvalidCursorPositionException {
        Set<RowsetListener> oldRowsetListeners = rowsetListeners;
        rowsetListeners = null;
        try {
            source.absolute(oldRowPos);
        } finally {
            rowsetListeners = oldRowsetListeners;
        }
    }

    /**
     * Fires <code>willDelete</code> event to all registered listeners. The
     * deleting might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aRow The row is to be deleted from the rowset.
     * @return Whether event source may perform the deleting.
     * @throws InvalidCursorPositionException
     */
    public boolean fireWillDeleteEvent(Row aRow) throws InvalidCursorPositionException {
        return fireWillDeleteEvent(aRow, false);
    }

    /**
     * Fires <code>willDelete</code> event to all registered listeners. The
     * deleting might be vetoed by one of the registered listeners. Nevetheless
     * the event will be propagated to all the registered listeners.
     *
     * @param aRow The row is to be deleted from the rowset.
     * @param aAjusting Ajusting flag. See <code>firerowDeletedEvent()</code>
     * javadoc.
     * @return Whether event source may perform the deleting.
     * @throws InvalidCursorPositionException
     * @see #fireRowDeletedEvent(com.bearsoft.rowset.Row, boolean)
     */
    public boolean fireWillDeleteEvent(Row aRow, boolean aAjusting) throws InvalidCursorPositionException {
        boolean res = true;
        if (rowsetListeners != null && !rowsetListeners.isEmpty()) {
            RowsetDeleteEvent event = new RowsetDeleteEvent(source, aRow, RowsetEventMoment.BEFORE, aAjusting);
            for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
                if (l != null) {
                    int oldRowPos = source.getCursorPos();
                    try {
                        if (!l.willDeleteRow(event)) {
                            res = false;
                        }
                    } finally {
                        restoreRowsetPosition(oldRowPos);
                    }
                }
            }
        }
        return res;
    }

    protected void notifyListeners(Consumer<RowsetListener> aOperation) {
        RowsetListener[] listeners = rowsetListeners.toArray(new RowsetListener[]{});
        for (RowsetListener l : listeners) {
            if (l instanceof RowsetEventsEarlyAccess) {
                aOperation.accept(l);
            }
        }
        for (RowsetListener l : listeners) {
            if (l != null && !(l instanceof RowsetEventsEarlyAccess)) {
                aOperation.accept(l);
            }
        }
    }

    /**
     * Fires requeriedEvent event to all registered listeners.
     */
    public void fireRequeriedEvent() {
        if (rowsetListeners != null) {
            RowsetRequeryEvent event = new RowsetRequeryEvent(source, RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowsetRequeried(event);
            });
        }
    }

    /**
     * Fires rowsetNextPageFetched event to all registered listeners.
     */
    public void fireNextPageFetchedEvent() {
        if (rowsetListeners != null) {
            RowsetNextPageEvent event = new RowsetNextPageEvent(source, RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowsetNextPageFetched(event);
            });
        }
    }

    /**
     * Fires filteredEvent event to all registered listeners.
     */
    public void fireFilteredEvent() {
        if (rowsetListeners != null) {
            RowsetFilterEvent event = new RowsetFilterEvent(source, source.getActiveFilter(), RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowsetFiltered(event);
            });
        }
    }

    /**
     * Fires savedEvent event to all registered listeners.
     */
    public void fireSavedEvent() {
        if (rowsetListeners != null) {
            RowsetSaveEvent event = new RowsetSaveEvent(source);
            notifyListeners((RowsetListener l) -> {
                l.rowsetSaved(event);
            });
        }
    }

    /**
     * Fires savedEvent event to all registered listeners.
     */
    public void fireRolledbackEvent() {
        if (rowsetListeners != null) {
            RowsetRollbackEvent event = new RowsetRollbackEvent(source);
            notifyListeners((RowsetListener l) -> {
                l.rowsetRolledback(event);
            });
        }
    }

    /**
     * Fires scrolledEvent event to all registered listeners.
     *
     * @param oldRowIndex Rowset's cursor position, that was actual before the
     * scrolling has been performed.
     */
    public void fireScrolledEvent(int oldRowIndex) {
        if (rowsetListeners != null) {
            RowsetScrollEvent event = new RowsetScrollEvent(source, oldRowIndex, source.getCursorPos(), RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowsetScrolled(event);
            });
        }
    }

    /**
     * Fires sortedEvent event to all registered listeners.
     */
    public void fireSortedEvent() {
        if (rowsetListeners != null) {
            RowsetSortEvent event = new RowsetSortEvent(source, RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowsetSorted(event);
            });
        }
    }

    /**
     * @param aChangedRow Fires rowChangedEvent event to all registered
     * listeners.
     * @param aOldValue Old value of row's column at <code>aFieldIndex</code>.
     * @param aFieldIndex Field (column) index of the upadated column.
     * @throws InvalidColIndexException
     * @throws InvalidCursorPositionException
     */
    public void fireRowChangedEvent(Row aChangedRow, int aFieldIndex, Object aOldValue) throws InvalidColIndexException, InvalidCursorPositionException {
        fireRowChangedEvent(aChangedRow, aFieldIndex, aOldValue, aChangedRow.getColumnObject(aFieldIndex));
    }

    /**
     * Fires rowChangedEvent event to all registered listeners.
     *
     * @param aChangedRow
     * @param aOldValue Old value of row's column at <code>aFieldIndex</code>.
     * @param aNewValue New value of row's column at <code>aFieldIndex</code>.
     * @param aFieldIndex Field (column) index of the upadated column.
     * @throws InvalidColIndexException
     * @throws InvalidCursorPositionException
     */
    public void fireRowChangedEvent(Row aChangedRow, int aFieldIndex, Object aOldValue, Object aNewValue) throws InvalidColIndexException, InvalidCursorPositionException {
        if (rowsetListeners != null) {
            RowChangeEvent event = new RowChangeEvent(source, aChangedRow, aFieldIndex, aOldValue, aNewValue, RowsetEventMoment.AFTER);
            notifyListeners((RowsetListener l) -> {
                l.rowChanged(event);
            });
        }
    }

    /**
     * Fires rowInsertedEvent event to all registered listeners.
     *
     * @param aRow Row was inserted to the rowset.
     *
     * public void fireRowInsertedEvent(Row aRow) { fireRowInsertedEvent(aRow,
     * false); }
     */
    /**
     * Fires rowInsertedEvent event to all registered listeners.
     *
     * @param aRow Row was inserted to the rowset.
     * @param aAjusting Flag, indicating that the event is a series of similar
     * events element.
     */
    public void fireRowInsertedEvent(Row aRow, boolean aAjusting) {
        if (rowsetListeners != null) {
            RowsetInsertEvent event = new RowsetInsertEvent(source, aRow, RowsetEventMoment.AFTER, aAjusting);
            notifyListeners((RowsetListener l) -> {
                l.rowInserted(event);
            });
        }
    }

    /**
     * Fires rowDeletedEvent event to all registered listeners.
     *
     * @param aRow Row was deleted from the rowset.
     */
    public void fireRowDeletedEvent(Row aRow) {
        fireRowDeletedEvent(aRow, false);
    }

    /**
     * Fires rowDeletedEvent event to all registered listeners.
     *
     * @param aRow Row was deleted from the rowset.
     * @param aAjusting If true, the event is marked as series of similar events
     * element.
     */
    public void fireRowDeletedEvent(Row aRow, boolean aAjusting) {
        if (rowsetListeners != null) {
            RowsetDeleteEvent event = new RowsetDeleteEvent(source, aRow, RowsetEventMoment.AFTER, aAjusting);
            notifyListeners((RowsetListener l) -> {
                l.rowDeleted(event);
            });
        }
    }
}

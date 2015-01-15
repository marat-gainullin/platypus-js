/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class intended to hold Rowset's listeners and fire Rowset's events.
 * 
 * @author mg
 */
public class RowsetChangeSupport {

	protected Set<RowsetListener> rowsetListeners = new HashSet<>();
	protected Rowset source;
    protected Row oldCurrentRow;
    protected int oldLength;

	/**
	 * The constructor.
	 * 
	 * @param aRowset
	 *            The events source.
	 */
	public RowsetChangeSupport(Rowset aRowset) {
		super();
		source = aRowset;
	}

	/**
	 * Registers <code>RowsetListener</code> on this
	 * <code>RowsetChangeSupport</code>.
	 * 
	 * @param aListener
	 *            <code>RowsetListener</code> to be registered.
	 */
	public void addRowsetListener(RowsetListener aListener) {
		rowsetListeners.add(aListener);
	}

	/**
	 * Removes <code>RowsetListener</code> from this
	 * <code>RowsetChangeSupport</code>.
	 * 
	 * @param aListener
	 *            <code>RowsetListener</code> to be removed.
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
	 * @param aNewRowIndex
	 *            Row index that will be cursor position when scrolling will be
	 *            performed.
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
            oldLength = source.size();
        }
		return res;
	}

	public void fireBeforeRequery(){
        oldCurrentRow = source.getCurrentRow();
        oldLength = source.size();
		RowsetRequeryEvent event = new RowsetRequeryEvent(source, RowsetEventMoment.BEFORE);
		for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
			if (l != null) {
				l.beforeRequery(event);
			}
		}
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
            oldLength = source.size();
        }
		return res;
	}
	
	/**
	 * Fires <code>willInsert</code> event to all registered listeners. The
	 * inserting might be vetoed by one of the registered listeners. Nevetheless
	 * the event will be propagated to all the registered listeners.
	 * 
	 * @param aRow
	 *            The row is to be inserted to the rowset.
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
	 * @param aRow
	 *            The row is to be inserted to the rowset.
	 * @param aAjusting
	 *            Flag, indicating that the event is a series of similar events
	 *            element.
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
            oldLength = source.size();
        }
		return res;
	}

	/**
	 * Private utility method for restoring rowset's position.
	 * 
	 * @param oldRowPos
	 *            Position to be setted to the Rowset.
	 */
	private void restoreRowsetPosition(int oldRowPos) throws InvalidCursorPositionException {
		Set<RowsetListener> oldRowsetListeners = rowsetListeners;
		rowsetListeners = null;
		try {
			source.setCursorPos(oldRowPos);
		} finally {
			rowsetListeners = oldRowsetListeners;
		}
	}

	/**
	 * Fires <code>willDelete</code> event to all registered listeners. The
	 * deleting might be vetoed by one of the registered listeners. Nevetheless
	 * the event will be propagated to all the registered listeners.
	 * 
	 * @param aRow
	 *            The row is to be deleted from the rowset.
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
	 * @param aRow
	 *            The row is to be deleted from the rowset.
	 * @param aAjusting
	 *            Ajusting flag. See <code>firerowDeletedEvent()</code> javadoc.
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
        if (res) {
            oldCurrentRow = source.getCurrentRow();
            oldLength = source.size();
        }
		return res;
	}

    public void fireBeforeRollback() {
        oldCurrentRow = source.getCurrentRow();
        oldLength = source.size();
    }

    protected void notifyCursor() {
        if (oldCurrentRow != source.getCurrentRow()) {
            source.firePropertyChange("cursor", oldCurrentRow, source.getCurrentRow());
            oldCurrentRow = null;
        }
    }

    protected void notifyLength() {
        if (oldLength == source.size()) {
            source.firePropertyChange("length", oldLength, 0);
            source.firePropertyChange("length", 0, source.size());
        } else {
            source.firePropertyChange("length", oldLength, source.size());
        }
    }

	/**
	 * Fires requeriedEvent event to all registered listeners.
	 */
	public void fireRequeriedEvent() {
        notifyCursor();
        notifyLength();
		if (rowsetListeners != null) {
			RowsetRequeryEvent event = new RowsetRequeryEvent(source, RowsetEventMoment.AFTER);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetRequeried(event);
				}
			}
		}
	}
	
	/**
	 * Fires requeriedEvent event to all registered listeners.
	 */
	public void fireNetErrorEvent(String anErrorMessage) {
		if (rowsetListeners != null) {
			RowsetNetErrorEvent event = new RowsetNetErrorEvent(source, anErrorMessage);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetNetError(event);
				}
			}
		}
	}
	

	/**
	 * Fires filteredEvent event to all registered listeners.
	 */
	public void fireFilteredEvent() {
        notifyCursor();
        notifyLength();
		if (rowsetListeners != null) {
			RowsetFilterEvent event = new RowsetFilterEvent(source, source.getActiveFilter(), RowsetEventMoment.AFTER);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetFiltered(event);
				}
			}
		}
	}

	/**
	 * Fires savedEvent event to all registered listeners.
	 */
	public void fireSavedEvent() {
		if (rowsetListeners != null) {
			RowsetSaveEvent event = new RowsetSaveEvent(source);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetSaved(event);
				}
			}
		}
	}

	/**
	 * Fires savedEvent event to all registered listeners.
	 */
	public void fireRolledbackEvent() {
        notifyCursor();
        notifyLength();
		if (rowsetListeners != null) {
			RowsetRollbackEvent event = new RowsetRollbackEvent(source);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetRolledback(event);
				}
			}
		}
	}

	/**
	 * Fires scrolledEvent event to all registered listeners.
	 * 
	 * @param oldRowIndex
	 *            Rowset's cursor position, that was actual before the scrolling
	 *            has been performed.
	 */
	public void fireScrolledEvent(int oldRowIndex) {
        notifyCursor();
		if (rowsetListeners != null) {
			RowsetScrollEvent event = new RowsetScrollEvent(source, oldRowIndex, source.getCursorPos(), RowsetEventMoment.AFTER);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetScrolled(event);
				}
			}
		}
	}

	/**
	 * Fires sortedEvent event to all registered listeners.
	 */
	public void fireSortedEvent() {
        notifyCursor();
		if (rowsetListeners != null) {
			RowsetSortEvent event = new RowsetSortEvent(source, RowsetEventMoment.AFTER);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowsetSorted(event);
				}
			}
		}
	}

	/**
	 * Fires rowInsertedEvent event to all registered listeners.
	 * 
	 * @param aRow
	 *            Row was inserted to the rowset.
	 * @param aAjusting
	 *            Flag, indicating that the event is a series of similar events
	 *            element.
	 */
	public void fireRowInsertedEvent(Row aRow, boolean aAjusting) {
        notifyCursor();
        notifyLength();
		if (rowsetListeners != null) {
			RowsetInsertEvent event = new RowsetInsertEvent(source, aRow, RowsetEventMoment.AFTER, aAjusting);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowInserted(event);
				}
			}
		}
	}

	/**
	 * Fires rowDeletedEvent event to all registered listeners.
	 * 
	 * @param aRow
	 *            Row was deleted from the rowset.
	 */
	public void fireRowDeletedEvent(Row aRow) {
		fireRowDeletedEvent(aRow, false);
	}

	/**
	 * Fires rowDeletedEvent event to all registered listeners.
	 * 
	 * @param aRow
	 *            Row was deleted from the rowset.
	 * @param aAjusting
	 *            If true, the event is marked as series of similar events
	 *            element.
	 */
	public void fireRowDeletedEvent(Row aRow, boolean aAjusting) {
        notifyCursor();
        notifyLength();
		if (rowsetListeners != null) {
			RowsetDeleteEvent event = new RowsetDeleteEvent(source, aRow, RowsetEventMoment.AFTER, aAjusting);
			for (RowsetListener l : rowsetListeners.toArray(new RowsetListener[]{})) {
				if (l != null) {
					l.rowDeleted(event);
				}
			}
		}
	}
}

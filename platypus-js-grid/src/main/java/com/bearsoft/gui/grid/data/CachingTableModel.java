/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This is TableModel implementation class, that delegates all the work to delegate model.
 * It caches results of delegate model work, rotating cache by maximum size.
 * It raises events from itself and from delegate model as if they
 * appeared straight in this model.
 * @see TableModel
 * @author Gala
 */
public class CachingTableModel implements TableModelWrapper {

    protected class TabledKey {

        protected int row;
        protected int column;

        public TabledKey(int aRow, int aColumn) {
            super();
            row = aRow;
            column = aColumn;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TabledKey other = (TabledKey) obj;
            if (this.row != other.row) {
                return false;
            }
            if (this.column != other.column) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + this.row;
            hash = 41 * hash + this.column;
            return hash;
        }
    }

    protected class DelegateListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            clearCache();
            TableModelEvent event = new TableModelEvent(CachingTableModel.this, e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType());
            listeners.stream().forEach((l) -> {
                l.tableChanged(event);
            });
        }
    }
    protected TableModel delegate;
    protected Set<TableModelListener> listeners = new HashSet<>();
    protected Map<TabledKey, Object> cache = new HashMap<>();
    protected int maxCacheSize = 4096;// 64^2;

    /**
     * Constructs an instance of CachingTableModel with default cache size.
     * @param aDelegate Model the work been delegated to.
     */
    public CachingTableModel(TableModel aDelegate) {
        super();
        delegate = aDelegate;
        delegate.addTableModelListener(new DelegateListener());
    }

    /**
     * Constructs an instance of CachingTableModel with default cache size.
     * @param aDelegate Model the work been delegated to.
     * @param aMaxCacheSize Maximum size of the cache, inclusive.
     */
    public CachingTableModel(TableModel aDelegate, int aMaxCacheSize) {
        this(aDelegate);
        maxCacheSize = aMaxCacheSize;
        if (maxCacheSize < 0) {
            maxCacheSize = 0;
        }
    }

    /**
     * Returns maximum cache size of the model.
     * @return Maximum size of the cache, inclusive.
     */
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    /**
     * Sets maximum cache size of the model.
     * @param aMaxCacheSize Maximum size of the cache, inclusive.
     */
    public void setMaxCacheSize(int aMaxCacheSize) {
        maxCacheSize = aMaxCacheSize;
        if (maxCacheSize < 0) {
            maxCacheSize = 0;
        }
    }

    protected Object put(int aRow, int aColumn, Object aValue) {
        if (cache.size() > maxCacheSize) {
            clearCache();
        }
        return cache.put(new TabledKey(aRow, aColumn), aValue);
    }

    protected Object get(int aRow, int aColumn) {
        return cache.get(new TabledKey(aRow, aColumn));
    }

    protected Object remove(int aRow, int aColumn, Object aValue) {
        return cache.remove(new TabledKey(aRow, aColumn));
    }

    public void clearCache() {
        cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return delegate.getColumnClass(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int columnIndex) {
        return delegate.getColumnName(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = get(rowIndex, columnIndex);
        if (value == null) {
            value = delegate.getValueAt(rowIndex, columnIndex);
            put(rowIndex, columnIndex, value);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        delegate.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return delegate.isCellEditable(rowIndex, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public TableModel unwrap() {
        return delegate;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.bearsoft.gui.grid.insets.InsetPart;
import com.bearsoft.gui.grid.insets.LinearInset;
import java.util.HashSet;
import java.util.Set;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * This table model wrapper is intended to work around a situation when row sorter or column model
 * generate illegal indexes, such as less than 0 or greater than some max bias.
 * @see InsetPart#AFTER_INSET_BIAS
 * @author Gala
 */
public class BoundaredTableModel implements TableModelWrapper {

    protected class DelegateListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            fireTableChanged(e);
        }
    }

    protected TableModel delegate;
    protected int maxBias = InsetPart.AFTER_INSET_BIAS;
    protected LinearInset rowsInset;
    protected LinearInset columnsInset;
    protected Set<TableModelListener> listeners = new HashSet<>();

    public BoundaredTableModel(TableModel aDelegate, int aMaxBias, LinearInset aRowsInset, LinearInset aColumnsInset) {
        super();
        delegate = aDelegate;
        maxBias = aMaxBias;
        rowsInset = aRowsInset;
        columnsInset = aColumnsInset;
        delegate.addTableModelListener(new DelegateListener());
        //rowsInset.addInsetChangeListener(new RowsInsetListener());
        //columnsInset.addInsetChangeListener(new ColumnsInsetListener());
    }

    protected boolean isLegal(int aIndex) {
        return aIndex >= 0 && aIndex < maxBias;
    }

    @Override
    public int getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    @Override
    public String getColumnName(int aColIndex) {
        if (isLegal(aColIndex)) {
            return delegate.getColumnName(aColIndex);
        } else {
            return "";
        }
    }

    public Class<?> getColumnClass(int aColIndex) {
        if (isLegal(aColIndex)) {
            return delegate.getColumnClass(aColIndex);
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int aRowIndex, int aColIndex) {
        if (isLegal(aRowIndex) && isLegal(aColIndex)) {
            return delegate.isCellEditable(aRowIndex, aColIndex);
        } else {
            return false;
        }
    }

    @Override
    public Object getValueAt(int aRowIndex, int aColIndex) {
        if (isLegal(aRowIndex) && isLegal(aColIndex)) {
            return delegate.getValueAt(aRowIndex, aColIndex);
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int aRowIndex, int aColIndex) {
        if (isLegal(aRowIndex) && isLegal(aColIndex)) {
            delegate.setValueAt(aValue, aRowIndex, aColIndex);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public TableModel unwrap() {
        return delegate;
    }

    protected void fireTableChanged(TableModelEvent e) {
        for (TableModelListener l : listeners) {
            l.tableChanged(e);
        }
    }
}

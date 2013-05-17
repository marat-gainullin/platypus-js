/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * Table model, getting and setting data to an arbitrary rowset. Gets data from
 * an arbitrary rowset or from a script.
 *
 * @author mg
 */
public class RowsetsTableModel extends RowsetsModel implements TableModel {

    protected Set<TableModelListener> listeners = new HashSet<>();
    protected TabularRowsRowsetListener rowsRowsetListener;

    /**
     * Constructor, accepting a rows rowset.
     *
     * @param aRowsRowset Rowset, serves as rows source.
     */
    public RowsetsTableModel(Rowset aRowsRowset, Scriptable aScriptScope, Function aCellsHandler) {
        super(aRowsRowset, aScriptScope, aCellsHandler);
        rowsRowsetListener = new TabularRowsRowsetListener(this);
        rowsRowset.addRowsetListener(rowsRowsetListener);
    }

    @Override
    public int getRowCount() {
        return rowsRowset.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.size()) {
            Row row = rowsRowset.getRow(rowIndex + 1);
            return getValue(row, columnIndex);
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.size()) {
            Row row = rowsRowset.getRow(rowIndex + 1);
            setValue(row, columnIndex, aValue);
        }
    }

    private void postEvent(TableModelEvent e) {
        for (TableModelListener l : listeners) {
            l.tableChanged(e);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void fireRowDeleted(int aRowIndex) {
        TableModelEvent e = new TableModelEvent(this, aRowIndex, aRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        postEvent(e);
    }

    public void fireRowInserted(int aRowIndex) {
        TableModelEvent e = new TableModelEvent(this, aRowIndex, aRowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        postEvent(e);
    }

    /**
     * Fires an event, that tells all listeners that rows structure has been
     * changed.
     */
    public void fireRowsChanged() {
        TableModelEvent e = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        postEvent(e);
    }

    /**
     * Fires an event, that tells all listeners that data in all rows have been
     * changed, but rows structure havn't been changed.
     */
    @Override
    public void fireRowsDataChanged() {
        TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, TableModelEvent.ALL_COLUMNS);
        postEvent(e);
    }

    public void fireRowsRowsetFieldChanged(int aRowsetFieldIndex) {
        int modelColumnIndex = getModelColumnIndex(aRowsetFieldIndex);
        if (modelColumnIndex != -1) {
            TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, modelColumnIndex);
            postEvent(e);
        }
        /*
         for (int i = 0; i < columns.size(); i++) {
         ModelColumn col = columns.get(i);
         if (col instanceof FieldModelColumn && col.getRowset() == rowsRowset && aRowsetFieldIndex == ((FieldModelColumn) col).getRowsetField()) {
         TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, i);
         postEvent(e);
         }
         }
         */
    }

    /**
     * Fires an event, occuring when data or rowset structure of column's rowset
     * is changed. It takes place when column's rowset and rows rowset are not
     * same.
     *
     * @param aColumn ModelColumn instance, the change is related to.
     * @see ModelColumn
     */
    @Override
    public void fireColumnRowsetChanged(ModelColumn aColumn) {
        assert aColumn.getRowset() != rowsRowset : "Model's columns should not generate column's rowset's events";
        int colIndex = getModelColumnIndex(aColumn);
        if (colIndex != -1) {
            TableModelEvent e = new TableModelEvent(this, 0, getRowCount() - 1, colIndex);
            postEvent(e);
        }
    }
}

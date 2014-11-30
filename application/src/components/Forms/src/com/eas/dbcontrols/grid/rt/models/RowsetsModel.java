/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.eas.client.forms.api.components.model.CellRenderEvent;
import com.eas.dbcontrols.grid.rt.columns.ModelColumn;
import com.eas.gui.CascadedStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public abstract class RowsetsModel {

    public static final String BAD_COLUMN_MSG = "Bad column configuration detected at column index %d";
    public static final String COLUMN_BINDING_MISSING_MSG = "Column rowset missing";
    public static final String COLUMN_MISSING_MSG = "Model column missing";
    protected TableColumnModel columns;
    protected JSObject generalOnRender;
    protected JSObject rows;

    public RowsetsModel(TableColumnModel aColumns, JSObject aRows, JSObject aGeneralOnRender) {
        super();
        columns = aColumns;
        rows = aRows;
        generalOnRender = aGeneralOnRender;
    }

    public int getColumnCount() {
        return columns.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    public Object getValue(JSObject anElement, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            try {
                ModelColumn column = (ModelColumn)columns.getColumn(columnIndex);
                return getRowsetsData(column, anElement);
            } catch (Exception ex) {
                severe(ex.getMessage());
                return new CellData(null, null, null);
            }
        }
        return null;
    }

    public void setValue(Row anElement, int columnIndex, Object aValue) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            try {
                ModelColumn column = (ModelColumn)columns.getColumn(columnIndex);
                setRowsetsData(aValue, column, anElement);
            } catch (Exception ex) {
                severe(ex.getMessage());
            }
        }
    }

    /**
     * Returns data from column's rowset. Takes into account that colum's rowset
     * may be the same with rows rowset annd requires special processing.
     *
     * @param aColumn ModelColumn instance, containing information about
     * rowset's column index to ge data from.
     * @param anElement Row instance, typically from rows rowset.
     * @return Rowset's data, from arbitrary rowset's column.
     * @throws InvalidCursorPositionException
     * @throws InvalidColIndexException
     */
    protected Object getRowsetsData(ModelColumn aColumn, JSObject anElement) throws Exception {
        if (aColumn != null) {
            if (aColumn.getField() != null && !aColumn.getField().isEmpty()) {
                Object value = anElement.getColumnObject(aColumn.getRowsetField());
                return complementCellData(new CellData(new CascadedStyle(aColumn.getStyle()), value, aColumn.getView() != null ? aColumn.getView().achiveDisplayValue(value) : value), anElement, aColumn);
            } else {
                //severe(COLUMN_ROWSET_MISSING_MSG);
                // pure script columns are allowed ...
                return complementCellData(new CellData(new CascadedStyle(aColumn.getStyle()), null, null), anElement, aColumn);
            }
        } else {
            severe(COLUMN_MISSING_MSG);
        }
        return new CellData(null, null, null);
    }

    /**
     * Sets data to column's rowset. Takes into account that colum's rowset may
     * not be the same with rows rowset and requires special processing.
     *
     * @param aValue Value to be setted.
     * @param aColumn ModelColumn instance, containing information about
     * rowset's field index to get data from.
     * @param aRow Row instance, typically from rows rowset.
     * @throws InvalidCursorPositionException
     * @throws InvalidColIndexException
     */
    protected void setRowsetsData(Object aValue, ModelColumn aColumn, Row aRow) throws Exception {
        if (aColumn != null) {
            if (aColumn.getField() != null) {
                if (aValue instanceof CellData) {
                    aValue = ((CellData) aValue).getData();
                }
                // all validating/change events posting code is inside of Row class.
                aRow.setColumnObject(aColumn.getRowsetField(), aValue);
            } else {
                severe(COLUMN_BINDING_MISSING_MSG);
            }
        } else {
            severe(COLUMN_MISSING_MSG);
        }
    }

    protected void severe(String aMsg) {
        Logger.getLogger(RowsetsTableModel.class.getName()).log(Level.SEVERE, aMsg);
    }

    public static Object getRowPkValue4Script(Row aRow) {
        Object rowPkValue = null;
        Object[] rowPkValues = aRow.getPKValues();
        if (rowPkValues.length == 1) {
            rowPkValue = rowPkValues[0];
        } else {
            rowPkValue = rowPkValues;
        }
        return rowPkValue;
    }

    /**
     * Fires an event, occuring when data or rowset structure of column's rowset
     * is changed. It takes place when column's rowset and rows rowset are not
     * the same.
     *
     * @param aColumn ModelColumn instance, the change is related to.
     * @see ModelColumn
     */
    public abstract void fireColumnRowsetChanged(ModelColumn aColumn);

    /**
     * Fires an event, that tells all listeners that data in all rows have been
     * changed, but rows structure havn't been changed.
     */
    public abstract void fireRowsDataChanged();

    private CellData complementCellData(final CellData aCellData, final JSObject anElement, final ModelColumn aColumn) throws Exception {
        JSObject lOnRender = aColumn.getOnRender();
        if (lOnRender == null) {
            lOnRender = generalOnRender;
        }
        if (lOnRender != null) {
            CellRenderEvent event = new CellRenderEvent(aColumn, aColumn, aCellData, anElement);
            lOnRender.call(aColumn.getPublished(), new Object[]{event.getPublished()});
        }
        return aCellData;
    }

    public JSObject getGeneralOnRender() {
        return generalOnRender;
    }

    public void setGeneralOnRender(JSObject aValue) {
        generalOnRender = aValue;
    }
}

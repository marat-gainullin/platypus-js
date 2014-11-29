/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.models;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.gui.CascadedStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public abstract class RowsetsModel {

    public static final String BAD_COLUMN_MSG = "Bad column configuration detected at column index %d";
    public static final String COLUMN_ROWSET_MISSING_MSG = "Column rowset missing";
    public static final String COLUMN_MISSING_MSG = "Model column missing";
    protected List<ModelColumn> columns = new ArrayList<>();
    private Map<ModelColumn, Integer> columnsIndicies;
    private Map<Integer, ModelColumn> rowsRowsetFields2ColumnsIndicies;
    protected ApplicationEntity<?, ?, ?> rowsEntity;
    protected Rowset rowsRowset;
    protected Locator pkLocator;
    protected JSObject generalOnRender;

    public RowsetsModel(ApplicationEntity<?, ?, ?> aRowsEntity, Rowset aRowsRowset, JSObject aGeneralCellsHandler) {
        super();
        rowsEntity = aRowsEntity;
        rowsRowset = aRowsRowset;
        pkLocator = createPkLocator();
        generalOnRender = aGeneralCellsHandler;
    }

    public Locator getPkLocator() {
        return pkLocator;
    }

    protected final Locator createPkLocator() throws IllegalStateException {
        Locator locator = rowsRowset.createLocator();
        // According to documentation, we have identify rows with first
        // primary key occured
        for (int i = 1; i <= rowsRowset.getFields().getFieldsCount(); i++) {
            Field field = rowsRowset.getFields().get(i);
            if (field.isPk()) {
                locator.beginConstrainting();
                try {
                    locator.addConstraint(rowsRowset.getFields().find(field.getName()));
                } finally {
                    locator.endConstrainting();
                }
                break;
            }
        }
        return locator;
    }

    public Rowset getRowsRowset() {
        return rowsRowset;
    }

    public int getColumnCount() {
        return columns.size();
    }

    public String getColumnName(int columnIndex) {
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    public ModelColumn getColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

    public Object getValue(Row anElement, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < columns.size()) {
            try {
                ModelColumn column = columns.get(columnIndex);
                if (checkColumnRowset(column, anElement)) {
                    return getRowsetsData(column, anElement);
                }
            } catch (Exception ex) {
                severe(ex.getMessage());
                return new CellData(null, null, null);
            }
        }
        return null;
    }

    public void setValue(Row anElement, int columnIndex, Object aValue) {
        if (columnIndex >= 0 && columnIndex < columns.size()) {
            try {
                ModelColumn column = columns.get(columnIndex);
                int oldCursorPos = rowsRowset.getCursorPos();
                try {
                    if (checkColumnRowset(column, anElement)) {
                        setRowsetsData(aValue, column, anElement);
                    }
                } finally {
                    restoreRowsRowsetCursorPos(rowsRowset, oldCursorPos);
                }
            } catch (Exception ex) {
                severe(ex.getMessage());
            }
        }
    }

    public static void restoreRowsRowsetCursorPos(Rowset aRowset, int aCursorPos) throws InvalidCursorPositionException {
        if (aCursorPos != aRowset.getCursorPos()) {
            if (aCursorPos == 0) {
                aRowset.beforeFirst();
            } else if (aCursorPos > aRowset.size()) {
                aRowset.afterLast();
            } else {
                aRowset.absolute(aCursorPos);
            }
        }
    }

    /**
     * Returns data from column's rowset. Takes into account that colum's rowset
     * may be the same with rows rowset annd requires special processing.
     *
     * @param aColumn ModelColumn instance, containing information about
     * rowset's column index to ge data from.
     * @param aRow Row instance, typically from rows rowset.
     * @return Rowset's data, from arbitrary rowset's column.
     * @throws InvalidCursorPositionException
     * @throws InvalidColIndexException
     */
    protected Object getRowsetsData(ModelColumn aColumn, Row aRow) throws Exception {
        if (aColumn != null) {
            if (aColumn.getRowset() != null) {
                Object value = aRow.getColumnObject(aColumn.getRowsetField());
                return complementCellData(new CellData(new CascadedStyle(aColumn.getStyle()), value, aColumn.getView() != null ? aColumn.getView().achiveDisplayValue(value) : value), aRow, aColumn);
            } else {
                return complementCellData(new CellData(new CascadedStyle(aColumn.getStyle()), null, null), aRow, aColumn);
                //severe(COLUMN_ROWSET_MISSING_MSG); // pure script columns are allowed
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
            if (aColumn.getRowset() != null) {
                if (aValue instanceof CellData) {
                    aValue = ((CellData) aValue).getData();
                }
                if (aColumn.getRowset() != rowsRowset) {
                    aColumn.getRowset().updateObject(aColumn.getRowsetField(), aValue);
                } else {
                    // all validating/change events posting code is inside of Row class.
                    aRow.setColumnObject(aColumn.getRowsetField(), aValue);
                }
            } else {
                severe(COLUMN_ROWSET_MISSING_MSG);
            }
        } else {
            severe(COLUMN_MISSING_MSG);
        }
    }

    /**
     * Returns a rowset, that should be used to get and set data. It may be the
     * same as rows rowset. If it is not, there is a hope that rowsets are
     * interliked in some way by the client code. So we have to position the
     * rows rowset to enable such link and simply get data from or set data to
     * current row of column's rowset. Avoids positioning of the rows rowset if
     * it is also column's rowset.
     *
     * @param aColumn ModelColumn instance, representing data binding
     * information, such as rowset with column's data and a field index in the
     * rowset (1 - based).
     * @param aRow Row instance, typically from rows rowset.
     * @return True if rows rowset is positioned on right row. It also returns
     * true if rows rowset is the same with column's rowset. It returns false if
     * rowsets are different and rows rowset havn't been positioned. It also
     * returns false if passed in parameters have bad values.
     * @throws Exception
     */
    protected boolean checkColumnRowset(ModelColumn aColumn, Row aRow) throws Exception {
        if (aColumn != null) {
            Rowset rowRowset = aColumn.getRowset();
            if (rowRowset != rowsRowset) {
                Object[] keys = aRow.getPKValues();
                return pkLocator.find(keys != null && keys.length > 1 ? new Object[]{keys[0]} : keys) && pkLocator.first();
            } else {
                return true;
            }
        } else {
            return false;
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

    private void invalidateColumnIndicies() {
        columnsIndicies = null;
        rowsRowsetFields2ColumnsIndicies = null;
    }

    private void validateColumnIndicies() {
        if (columnsIndicies == null) {
            columnsIndicies = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                columnsIndicies.put(columns.get(i), i);
            }
        }
        if (rowsRowsetFields2ColumnsIndicies == null) {
            rowsRowsetFields2ColumnsIndicies = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                ModelColumn col = columns.get(i);
                rowsRowsetFields2ColumnsIndicies.put(col.getRowsetField(), col);
            }
        }
    }

    /**
     * Converts <code>ModelColumn</code> instance to model column index.
     *
     * @param aCol <code>ModelColumn</code> instance to convert.
     * @return Model column index.
     */
    protected int getModelColumnIndex(ModelColumn aCol) {
        validateColumnIndicies();
        return columnsIndicies.get(aCol);
    }

    /**
     * Returns model column index by rows rowset column index.
     *
     * @param aRowsRowsetFieldIndex Index of rows rowset column index.
     * @return Model column index.
     */
    protected int getModelColumnIndex(int aRowsRowsetFieldIndex) {
        validateColumnIndicies();
        ModelColumn fcol = rowsRowsetFields2ColumnsIndicies.get(aRowsRowsetFieldIndex);
        if (fcol != null) {
            return columnsIndicies.get(fcol);
        } else {
            return -1;
        }
    }

    /*
     * Adds a model's column to the model.
     * @param aColumn ModelColumn instance to be added.
     */
    public void addColumn(ModelColumn aColumn) {
        addColumn(columns.size(), aColumn);
    }

    /**
     * Adds a model's column to the model at the specified position.
     *
     * @param aColumn ModelColumn instance to be added.
     * @param aIndex position the new column to be added at.
     */
    public void addColumn(int aIndex, ModelColumn aColumn) {
        columns.add(aIndex, aColumn);
        invalidateColumnIndicies();
    }

    /**
     * Removes a column at the specified position.
     *
     * @param aColumnIndex Index of model column. 0-based.
     */
    public void removeColumn(int aColumnIndex) {
        ModelColumn aColumn = columns.remove(aColumnIndex);
        invalidateColumnIndicies();
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

    public boolean positionRowsetWithRow(Row aRow) throws Exception {
        Object rowKey = null;
        if (aRow != null) {
            // According to documentation, we have to identify rows with first
            // primary key occured
            for (int i = 1; i <= aRow.getFields().getFieldsCount(); i++) {
                if (aRow.getFields().get(i).isPk()) {
                    rowKey = aRow.getColumnObject(i);
                    break;
                }
            }
            if (rowKey != null) {
                return pkLocator.find(new Object[]{rowKey}) && pkLocator.first();
            } else {
                if (rowsRowset.size() == 1) {
                    return rowsRowset.first();
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private CellData complementCellData(final CellData aCellData, final Row aRow, final ModelColumn aColumn) throws Exception {
        JSObject lOnRender = aColumn.getOnRender();
        if (lOnRender == null) {
            lOnRender = generalOnRender;
        }
        if (lOnRender != null) {
            Object rowPkValue = getRowPkValue4Script(aRow);
            Object colPkValue = null;
            CellRenderEvent event = new CellRenderEvent(aColumn, rowPkValue, colPkValue, aCellData, aRow);
            lOnRender.call(aColumn.getPublished(), new Object[]{event.getPublished()});
        }
        return aCellData;
    }

    public void removeColumn(ModelColumn aColumn) {
        int columnIndex = columns.indexOf(aColumn);
        if (columnIndex != -1) {
            removeColumn(columnIndex);
        }
    }

    public JSObject getGeneralOnRender() {
        return generalOnRender;
    }

    public void setGeneralOnRender(JSObject aValue) {
        generalOnRender = aValue;
    }
}

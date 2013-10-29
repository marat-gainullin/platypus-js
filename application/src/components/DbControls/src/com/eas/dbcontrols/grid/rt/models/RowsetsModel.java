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
import com.eas.client.model.script.RowHostObject;
import com.eas.dbcontrols.CellRenderEvent;
import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.ModelColumn;
import com.eas.dbcontrols.grid.rt.columns.model.RowModelColumn;
import com.eas.dbcontrols.grid.rt.veers.CellsRowsetsListener;
import com.eas.dbcontrols.grid.rt.veers.ColumnRowsetListener;
import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Gala
 */
public abstract class RowsetsModel {

    public static final String BAD_COLUMN_MSG = "Bad column configuration detected at column index %d";
    public static final String COLUMN_ROWSET_MISSING_MSG = "Column rowset missing";
    public static final String COLUMN_MISSING_MSG = "Model column missing";
    protected List<ModelColumn> columns = new ArrayList<>();
    private Map<ModelColumn, Integer> columnsIndicies;
    private Map<Integer, FieldModelColumn> rowsRowsetFields2ColumnsIndicies;
    protected ApplicationEntity<?, ?, ?> rowsEntity;
    protected Rowset rowsRowset;
    protected Locator pkLocator;
    protected Scriptable scriptScope;
    protected Function generalCellsHandler;

    public RowsetsModel(ApplicationEntity<?, ?, ?> aRowsEntity, Rowset aRowsRowset, Scriptable aScriptScope, Function aGeneralCellsHandler) {
        super();
        rowsEntity = aRowsEntity;
        rowsRowset = aRowsRowset;
        pkLocator = createPkLocator();
        scriptScope = aScriptScope;
        generalCellsHandler = aGeneralCellsHandler;
    }

    public Scriptable getScriptScope() {
        return scriptScope;
    }

    public void setScriptScope(Scriptable aValue) {
        scriptScope = aValue;
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
                if (column instanceof FieldModelColumn) {
                    if (checkColumnRowset(column, anElement)) {
                        return getRowsetsData(column, anElement);
                    }
                } else if (column instanceof RowModelColumn) {
                    RowModelColumn rCol = (RowModelColumn) column;
                    Row cellRow = achieveCellRow(rCol, anElement, false);
                    if (cellRow != null) {
                        Object value = cellRow.getColumnObject(rCol.getCellsValuesFieldIndex());
                        return complementCellData(new CellData(new CascadedStyle(column.getStyle()), value, column.getView() != null ? column.getView().achiveDisplayValue(value) : value), cellRow, column);
                    }
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
                if (column instanceof FieldModelColumn) {
                    int oldCursorPos = rowsRowset.getCursorPos();
                    try {
                        if (checkColumnRowset(column, anElement)) {
                            setRowsetsData(aValue, column, anElement);
                        }
                    } finally {
                        restoreRowsRowsetCursorPos(rowsRowset, oldCursorPos);
                    }
                } else if (column instanceof RowModelColumn) {
                    RowModelColumn rCol = (RowModelColumn) column;
                    Row cellRow = achieveCellRow(rCol, anElement, true);
                    if (cellRow != null) {
                        cellRow.setColumnObject(rCol.getCellsValuesFieldIndex(), aValue);
                    }
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
            assert aColumn instanceof FieldModelColumn;
            if (aColumn.getRowset() != null) {
                Object value = null;
                FieldModelColumn fieldColumn = (FieldModelColumn) aColumn;
                if (fieldColumn.getRowset() != rowsRowset) {
                    value = fieldColumn.getRowset().getObject(fieldColumn.getRowsetField());
                } else {
                    value = aRow.getColumnObject(fieldColumn.getRowsetField());
                }
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
            assert aColumn instanceof FieldModelColumn;
            if (aColumn.getRowset() != null) {
                if (aValue instanceof CellData) {
                    aValue = ((CellData) aValue).getData();
                }
                FieldModelColumn fieldColumn = (FieldModelColumn) aColumn;
                if (fieldColumn.getRowset() != rowsRowset) {
                    fieldColumn.getRowset().updateObject(fieldColumn.getRowsetField(), aValue);
                } else {
                    // all validating/change events posting code is inside of Row class.
                    aRow.setColumnObject(fieldColumn.getRowsetField(), aValue);
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

    /**
     * Returns a row of cell's values rowset, bound to
     * <code>rCol</code>
     *
     * @param rCol RowModelColumn instance.
     * @param aRow Row instance, typically from rows rowset.
     * @return Row instance, the cell's value will be achieved from.
     * @throws Exception
     * @see RowModelColumn
     * @see Row
     */
    protected Row achieveCellRow(RowModelColumn rCol, Row rRow, boolean aForceInsert) throws Exception {
        Row cRow = rCol.getRow();
        Locator cellsLocator = rCol.getCellsLocator();
        if (cRow != null && rRow != null && cellsLocator != null) {
            Object[] rKeys = rRow.getPKValues();
            Object[] cKeys = cRow.getPKValues();
            if(rKeys.length == 0)
                throw new IllegalStateException("Row key is not found for "+rowsEntity.getFormattedNameAndTitle());
            if(rKeys.length > 1)
                throw new IllegalStateException("Row key is ambiguous for "+rowsEntity.getFormattedNameAndTitle());
            if(cKeys.length == 0)
                throw new IllegalStateException("Column key is not found for "+rowsEntity.getFormattedNameAndTitle());
            if(cKeys.length > 1)
                throw new IllegalStateException("Column key is ambiguous for "+rowsEntity.getFormattedNameAndTitle());
            Object[] cellsKeys = new Object[rKeys.length + cKeys.length];
            System.arraycopy(rKeys, 0, cellsKeys, 0, rKeys.length);
            System.arraycopy(cKeys, 0, cellsKeys, rKeys.length, cKeys.length);
            if (aForceInsert && !cellsLocator.find(cellsKeys)) {
                List<Integer> cellColsIndicies = cellsLocator.getFields();
                assert cellColsIndicies.size() == cellsKeys.length : "Rowsets veer. Cells locator's columns indices and supplied keys values array must be the same length.";
                Object[] cellInitingValues = new Object[cellColsIndicies.size() * 2];
                for (int i = 0; i < cellColsIndicies.size(); i++) {
                    cellInitingValues[i * 2] = cellColsIndicies.get(i);
                    cellInitingValues[i * 2 + 1] = cellsKeys[i];
                }
                cellsLocator.getRowset().insert(cellInitingValues);
            }
            if (cellsLocator.find(cellsKeys)) {
                if (rCol.getCellsValuesRowset() != cellsLocator.getRowset()) {
                    int cursorPos = cellsLocator.getRowset().getCursorPos();
                    try {
                        boolean positioned = cellsLocator.first();
                        assert positioned;
                        return rCol.getCellsValuesRowset().getCurrentRow();
                    } finally {
                        restoreRowsRowsetCursorPos(cellsLocator.getRowset(), cursorPos);
                    }
                } else {
                    return cellsLocator.getRow(0);
                }
            }
        }
        return null;
    }

    protected void severe(String aMsg) {
        Logger.getLogger(RowsetsTableModel.class.getName()).severe(aMsg);
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
                if (col instanceof FieldModelColumn && col.getRowset() == rowsRowset) {
                    FieldModelColumn fcol = (FieldModelColumn) col;
                    rowsRowsetFields2ColumnsIndicies.put(fcol.getRowsetField(), fcol);
                }
            }
        }
    }

    /**
     * Converts
     * <code>ModelColumn</code> instance to model column index.
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
        FieldModelColumn fcol = rowsRowsetFields2ColumnsIndicies.get(aRowsRowsetFieldIndex);
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
        if (aColumn instanceof FieldModelColumn) {
            FieldModelColumn fieldColumn = (FieldModelColumn) aColumn;
            if (fieldColumn.getRowset() != null) {
                if (fieldColumn.getRowset() != rowsRowset) {
                    fieldColumn.setRowsetListener(new ColumnRowsetListener(this, fieldColumn));
                }
            } else {
                Logger.getLogger(RowsetsModel.class.getName()).log(Level.CONFIG, String.format(BAD_COLUMN_MSG, aIndex));
            }
        } else {
            RowModelColumn rowColumn = (RowModelColumn) aColumn;
            if (rowColumn.getCellsValuesRowset() != null) {
                rowColumn.setValuesListener(new CellsRowsetsListener(this, rowColumn));
            }
        }
        invalidateColumnIndicies();
    }

    /**
     * Removes a column at the specified position.
     *
     * @param aColumnIndex Index of model column. 0-based.
     */
    public void removeColumn(int aColumnIndex) {
        ModelColumn aColumn = columns.remove(aColumnIndex);
        if (aColumn instanceof FieldModelColumn) {
            FieldModelColumn fieldColumn = (FieldModelColumn) aColumn;
            if (fieldColumn.getRowset() != rowsRowset) {
                fieldColumn.setRowsetListener(null);
            }
        } else {
            RowModelColumn rowColumn = (RowModelColumn) aColumn;
            if (rowColumn.getCellsValuesRowset() != null) {
                rowColumn.setValuesListener(null);
            }
        }
        invalidateColumnIndicies();
    }

    /**
     * Fires an event, occuring when data or rowset structure of column's rowset
     * is changed. It takes place when column's rowset and rows rowset are not
     * same.
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

    private CellData complementCellData(CellData cellData, Row aRow, ModelColumn aColumn) throws Exception {
        Function handler = aColumn.getCellsHandler();
        if (handler == null) {
            handler = generalCellsHandler;
        }
        if (scriptScope != null && handler != null) {
            Context cx = Context.getCurrentContext();
            boolean wasContext = cx != null;
            if (!wasContext) {
                cx = ScriptUtils.enterContext();
            }
            try {
                Object rowPkValue = getRowPkValue4Script(aRow);
                Object colPkValue = null;
                if (aColumn instanceof RowModelColumn) {
                    RowModelColumn rmc = (RowModelColumn) aColumn;
                    colPkValue = getRowPkValue4Script(rmc.getRow());
                }
                Scriptable calcedThis = aColumn.getEventsThis() != null ? aColumn.getEventsThis() : scriptScope;
                handler.call(cx, scriptScope, calcedThis, new Object[]{new CellRenderEvent(calcedThis, rowPkValue, colPkValue, cellData, RowHostObject.publishRow(scriptScope, aRow, rowsEntity))});
            } finally {
                if (!wasContext) {
                    Context.exit();
                }
            }
        }
        return cellData;
    }

    public void removeColumn(ModelColumn aColumn) {
        int columnIndex = columns.indexOf(aColumn);
        if (columnIndex != -1) {
            removeColumn(columnIndex);
        }
    }

    public Function getGeneralCellsHandler() {
        return generalCellsHandler;
    }

    public void setGeneralCellsHandler(Function aValue) {
        generalCellsHandler = aValue;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;

/**
 *
 * @author mg
 */
public abstract class ArrayModel {

    public static final String BAD_COLUMN_MSG = "Bad column configuration detected at column index %d";
    public static final String COLUMN_BINDING_MISSING_MSG = "Column rowset missing";
    public static final String COLUMN_MISSING_MSG = "Model column missing";
    protected TableColumnModel columns;
    protected JSObject generalOnRender;
    protected JSObject elements;

    public ArrayModel(TableColumnModel aColumns, JSObject aElements, JSObject aGeneralOnRender) {
        super();
        columns = aColumns;
        elements = aElements;
        generalOnRender = aGeneralOnRender;
    }

    public JSObject getElements() {
        return elements;
    }

    public void setElements(JSObject aValue) {
        if (elements != null ? !elements.equals(aValue) : aValue != null) {
            // TODO: remove luisteners from old object
            elements = aValue;
            // TODO: add listeners to new object
            fireElementsChanged();
        }
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
                ModelColumn column = (ModelColumn) columns.getColumn(columnIndex);
                return ModelWidget.getPathData(anElement, column.getField());
            } catch (Exception ex) {
                Logger.getLogger(ArrayTableModel.class.getName()).log(Level.SEVERE, ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public void setValue(JSObject anElement, int columnIndex, Object aValue) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            try {
                ModelColumn column = (ModelColumn) columns.getColumn(columnIndex);
                ModelWidget.setPathData(anElement, column.getField(), aValue);
            } catch (Exception ex) {
                Logger.getLogger(ArrayTableModel.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    /**
     * Fires an event, occuring when data or rowset structure of column's rowset
     * is changed. It takes place when column's rowset and elements rowset are
     * not the same.
     *
     * @param aColumn ModelColumn instance, the change is related to.
     * @see ModelColumn
     */
    public abstract void fireColumnFieldChanged(ModelColumn aColumn);

    /**
     * Fires an event, that tells all listeners that elements structure has been changed.
     */
    public abstract void fireElementsChanged();
    
    /**
     * Fires an event, that tells all listeners that data in all elements have
     * been changed, but elements structure havn't been changed.
     */
    public abstract void fireElementsDataChanged();
/*
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
*/
    public JSObject getGeneralOnRender() {
        return generalOnRender;
    }

    public void setGeneralOnRender(JSObject aValue) {
        generalOnRender = aValue;
    }
}

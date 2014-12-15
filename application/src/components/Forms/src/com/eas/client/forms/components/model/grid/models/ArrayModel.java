/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.models;

import com.bearsoft.gui.grid.data.CellData;
import com.eas.client.forms.components.model.CellRenderEvent;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;
import jdk.nashorn.api.scripting.JSObject;

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
        if (elements != aValue) {
            // TODO: remove luisteners from old object
            elements = aValue;
            // TODO: add listeners to new object
            fireElementsDataChanged();
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
                return getObjectsData(column.getField(), anElement);
            } catch (Exception ex) {
                severe(ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public void setValue(JSObject anElement, int columnIndex, Object aValue) {
        if (columnIndex >= 0 && columnIndex < columns.getColumnCount()) {
            try {
                ModelColumn column = (ModelColumn) columns.getColumn(columnIndex);
                setObjectsData(aValue, column.getField(), anElement);
            } catch (Exception ex) {
                severe(ex.getMessage());
            }
        }
    }

    protected Object getObjectsData(String aField, JSObject anElement) throws Exception {
        if (aField != null && !aField.isEmpty()) {
            JSObject target = anElement;
            String[] path = aField.split(".");
            String propName = path[0];
            for (int i = 1; i < path.length; i++) {
                Object oTarget = anElement.getMember(propName);
                propName = path[i];
                if (!(oTarget instanceof JSObject)) {
                    propName = null;
                    break;
                } else {
                    target = (JSObject) oTarget;
                }
            }
            Object value = null;
            if (propName != null) {
                value = ScriptUtils.toJava(target.getMember(propName));
            } else {
                severe("Field path: " + aField + " doesn't exist.");
            }
            return value;
        } else {
            return null;
        }
    }

    protected void setObjectsData(Object aValue, String aField, JSObject anElement) throws Exception {
        if (aField != null && !aField.isEmpty()) {
            if (aValue instanceof CellData) {
                aValue = ((CellData) aValue).getData();
            }
            // All validating/change events posting code is inside of script object class.
            JSObject target = anElement;
            String[] path = aField.split(".");
            String propName = path[0];
            for (int i = 1; i < path.length; i++) {
                Object oTarget = anElement.getMember(propName);
                propName = path[i];
                if (!(oTarget instanceof JSObject)) {
                    propName = null;
                    break;
                } else {
                    target = (JSObject) oTarget;
                }
            }
            if (propName != null) {
                target.setMember(propName, ScriptUtils.toJs(aValue));
            } else {
                severe("Field path: " + aField + " doesn't exist.");
            }
        } else {
            severe(COLUMN_BINDING_MISSING_MSG);
        }
    }

    protected void severe(String aMsg) {
        Logger.getLogger(ArrayTableModel.class.getName()).log(Level.SEVERE, aMsg);
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
     * Fires an event, that tells all listeners that data in all elements have
     * been changed, but elements structure havn't been changed.
     */
    public abstract void fireElementsDataChanged();

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

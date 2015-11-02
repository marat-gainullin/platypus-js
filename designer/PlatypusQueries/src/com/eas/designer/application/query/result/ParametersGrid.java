/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result;

import com.eas.client.forms.Forms;
import com.eas.client.forms.HasJsValue;
import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelDate;
import com.eas.client.forms.components.model.ModelSpin;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.script.Scripts;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public class ParametersGrid extends JTable {

    protected static final int HEADER_COLUMN_INDEX = 0;
    protected static final int LABEL_COLUMN_INDEX = 1;
    protected static final int VALUE_COLUMN_INDEX = 2;
    protected Parameters params;
    protected String labelTitle = "Characteristic";
    protected String valueTitle = "Value";
    protected Map<Parameter, ModelWidget> controls = new HashMap<>();
    protected List<ModelWidget> controlsList = new ArrayList<>();
    protected Set<String> hidingFields = new HashSet<>();
    protected boolean filterPrimaryKeys;
    protected boolean filterForeignKeys;
    protected boolean editable = true;

    public boolean isFilterForeignKeys() {
        return filterForeignKeys;
    }

    public void setFilterForeignKeys(boolean aValue) {
        filterForeignKeys = aValue;
    }

    public boolean isFilterPrimaryKeys() {
        return filterPrimaryKeys;
    }

    public void setFilterPrimaryKeys(boolean aValue) {
        filterPrimaryKeys = aValue;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        controls.values().stream().filter((control) -> (control instanceof HasEditable)).forEach((control) -> {
            ((HasEditable) control).setEditable(aValue);
        });
    }

    public Parameters getParams() {
        return params;
    }

    protected class EntityFieldsModel implements TableModel {

        protected Set<TableModelListener> listeners = new HashSet<>();

        @Override
        public int getRowCount() {
            if (params != null) {
                return params.getFieldsCount();
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "";
                case 1:
                    return labelTitle;
                case 2:
                    return valueTitle;
            }
            return "";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == LABEL_COLUMN_INDEX) {
                return String.class;
            }
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == VALUE_COLUMN_INDEX;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= 0 && rowIndex < getRowCount()) {
                if (params != null) {
                    Parameter f = params.get(rowIndex + 1);
                    if (columnIndex == LABEL_COLUMN_INDEX) {
                        String rowTitle = f.getName();
                        if (f.getDescription() != null && !f.getDescription().isEmpty()) {
                            rowTitle = f.getDescription();
                        }
                        return rowTitle;
                    } else if (columnIndex == HEADER_COLUMN_INDEX) {
                        return f.isNullable() ? "" : " * ";
                    }
                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        private void fireDataChanged() {
            TableModelEvent tme = new TableModelEvent(EntityFieldsModel.this);
            for (TableModelListener l : listeners.toArray(new TableModelListener[]{})) {
                l.tableChanged(tme);
            }
        }
    }

    protected class EntityFieldsCellEditor implements TableCellEditor, TableCellRenderer {

        protected Set<CellEditorListener> listeners = new HashSet<>();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            assert table == ParametersGrid.this;
            if (row >= 0 && row < controlsList.size()) {
                ModelWidget control = controlsList.get(row);
                assert control instanceof Component;
                return (Component) control;
            }
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            ChangeEvent ce = new ChangeEvent(EntityFieldsCellEditor.this);
            for (CellEditorListener l : listeners.toArray(new CellEditorListener[]{})) {
                l.editingStopped(ce);
            }
            return true;
        }

        @Override
        public void cancelCellEditing() {
            ChangeEvent ce = new ChangeEvent(EntityFieldsCellEditor.this);
            for (CellEditorListener l : listeners.toArray(new CellEditorListener[]{})) {
                l.editingCanceled(ce);
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }
    }

    public ParametersGrid() {
        super();
        labelTitle = Forms.getLocalizedString(labelTitle);
        valueTitle = Forms.getLocalizedString(valueTitle);
        setModel(new EntityFieldsModel());
        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        TableColumn tc = getColumnModel().getColumn(VALUE_COLUMN_INDEX);
        if (tc != null) {
            tc.setCellEditor(new EntityFieldsCellEditor());
            tc.setCellRenderer(new EntityFieldsCellEditor());
        }
        tc = getColumnModel().getColumn(HEADER_COLUMN_INDEX);
        if (tc != null) {
            tc.setMaxWidth(15);
            tc.setWidth(15);
        }
        setRowHeight(24);
    }
    /*
     public ParametersGrid(String... aHidingFields) {
     this();
     if (aHidingFields != null) {
     for (String aHidingField : aHidingFields) {
     if (aHidingField != null) {
     hidingFields.add(aHidingField.toLowerCase());
     }
     }
     }
     }
     */

    public void setParams(Parameters aValue) throws Exception {
        cleanup();
        params = aValue;
        if (params != null) {
            fillControls();
        }
    }

    public String getLabelTitle() {
        return labelTitle;
    }

    public String getValueTitle() {
        return valueTitle;
    }

    public void setLabelTitle(String labelTitle) {
        this.labelTitle = labelTitle;
        TableColumn tc = getColumnModel().getColumn(LABEL_COLUMN_INDEX);
        if (tc != null) {
            tc.setHeaderValue(labelTitle);
        }
    }

    public void setValueTitle(String aValue) {
        valueTitle = aValue;
        TableColumn tc = getColumnModel().getColumn(VALUE_COLUMN_INDEX);
        if (tc != null) {
            tc.setHeaderValue(aValue);
        }
    }

    public void stopEditing() {
        if (isEditing()) {
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    public Map<Parameter, ModelWidget> getControls() {
        return controls;
    }

    private void fillControls() throws Exception {
        assert params != null;
        cleanup();
        // fill in the controls
        int cCount = params.getFieldsCount();
        for (int i = 0; i < cCount; i++) {
            Parameter param = params.get(i + 1);
            ModelWidget widget = Forms.chooseWidgetByType(param.getType());
            if (Scripts.DATE_TYPE_NAME.equals(param.getType())) {
                ((ModelDate) widget).setFormat("dd.MM.yyyy HH:mm:ss.SSS");
            }
            ((JComponent) widget).setBorder(null);
            if (widget instanceof HasEditable) {
                ((HasEditable) widget).setEditable(editable);
            }
            ((JComponent) widget).setName(param.getName());
            if (widget instanceof ModelCheckBox) {
                ((ModelCheckBox) widget).setAlign(SwingConstants.CENTER);
            }
            if (widget instanceof ModelSpin) {
                ((ModelSpin) widget).setMin(-Double.MAX_VALUE);
                ((ModelSpin) widget).setMax(Double.MAX_VALUE);
            }
            ((HasJsValue) widget).setJsValue(param.getValue());
            controls.put(param, widget);
            controlsList.add(widget);
        }
        // notify all of change
        ((EntityFieldsModel) getModel()).fireDataChanged();
    }

    protected void cleanup() throws Exception {
        controls.clear();
        controlsList.clear();
    }
}

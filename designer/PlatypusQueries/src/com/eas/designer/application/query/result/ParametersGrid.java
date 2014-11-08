/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result;

import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
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
    protected Map<Parameter, ScalarDbControl> controls = new HashMap<>();
    protected List<ScalarDbControl> controlsList = new ArrayList<>();
    protected Set<String> hidingFields = new HashSet<>();
    protected boolean filterPrimaryKeys;
    protected boolean filterForeignKeys;
    protected boolean editable = true;
    protected String booleanFieldsMask;

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

    public String getBooleanFieldsMask() {
        return booleanFieldsMask;
    }

    public void setBooleanFieldsMask(String aValue) {
        booleanFieldsMask = aValue;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        controls.values().stream().filter((control) -> (control instanceof DbControlPanel)).forEach((control) -> {
            ((DbControlPanel) control).setEditable(aValue);
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
            listeners.stream().forEach((tml) -> {
                tml.tableChanged(tme);
            });
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
                ScalarDbControl control = controlsList.get(row);
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
            listeners.stream().forEach((l) -> {
                l.editingStopped(ce);
            });
            return true;
        }

        @Override
        public void cancelCellEditing() {
            ChangeEvent ce = new ChangeEvent(EntityFieldsCellEditor.this);
            listeners.stream().forEach((l) -> {
                l.editingCanceled(ce);
            });
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
        labelTitle = DbControlsUtils.getLocalizedString(labelTitle);
        valueTitle = DbControlsUtils.getLocalizedString(valueTitle);
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
        setRowHeight(20);
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

    public Map<Parameter, ScalarDbControl> getControls() {
        return controls;
    }

    private void fillControls() throws Exception {
        assert params != null;
        cleanup();
        // fill in the controls
        // TODO: Hack of DbControlPanel. Remove after widgets refactoring
        ApplicationDbModel fakeModel = new ApplicationDbModel(null);
        int cCount = params.getFieldsCount();
        for (int i = 0; i < cCount; i++) {
            Parameter param = params.get(i + 1);
            Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(param.getTypeInfo().getSqlType());
            if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
                Class<?> lControlClass = compatibleControlsClasses[0];
                if (booleanFieldsMask != null && Pattern.matches(booleanFieldsMask, param.getName())) {
                    lControlClass = DbCheck.class;
                }
                if (lControlClass != null) {
                    Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                    if (infoClass != null) {
                        Logger.getLogger(ParametersGrid.class.getName()).log(Level.FINEST, "Creating control for parameter {0} of type {1} with control class {2}", new Object[]{param.getName(), param.getTypeInfo().getSqlTypeName(), lControlClass.getName()});
                        DbControlDesignInfo cdi = (DbControlDesignInfo) infoClass.newInstance();
                        if (cdi instanceof DbDateDesignInfo) {
                            DbDateDesignInfo dateDesignInfo = (DbDateDesignInfo) cdi;
                            if (param.getTypeInfo().getSqlType() == java.sql.Types.TIMESTAMP) {
                                dateDesignInfo.setDateFormat(DbDate.DD_MM_YYYY_HH_MM_SS);
                            } else if (param.getTypeInfo().getSqlType() == java.sql.Types.TIME) {
                                dateDesignInfo.setDateFormat(DbDate.HH_MM_SS);
                            }
                        }
                        DbSwingFactory factory = new DbSwingFactory(null);
                        cdi.accept(factory);
                        assert factory.getComp() instanceof ScalarDbControl;
                        ScalarDbControl control = (ScalarDbControl) factory.getComp();
                        control.setModel(fakeModel);
                        control.setStandalone(true);
                        control.configure();
                        control.setBorderless(true);

                        if (control instanceof DbControlPanel) {
                            ((DbControlPanel) control).setBorder(null);
                            ((DbControlPanel) control).setEditable(editable);
                            ((DbControlPanel) control).setName(param.getName());
                            if (control instanceof DbCheck) {
                                ((DbControlPanel) control).setAlign(SwingConstants.CENTER);
                            }
                            ((DbControlPanel) control).setValue(param.getValue());
                        }
                        controls.put(param, control);
                        controlsList.add(control);
                    }
                }
            }
        }
        // notify all of change
        ((EntityFieldsModel) getModel()).fireDataChanged();
    }

    protected void cleanup() throws Exception {
        // cleanup
        for (ScalarDbControl control : controls.values()) {
            control.setModel(null);
            control.cleanup();
        }
        controls.clear();
        controlsList.clear();
    }
}

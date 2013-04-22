/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.output;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.CollectionListener;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.PlatypusQuerySupport;
import com.eas.designer.application.query.editing.StoredFieldAddEdit;
import com.eas.designer.application.query.editing.StoredFieldDeleteEdit;
import com.eas.designer.application.query.editing.StoredFieldDescriptionEdit;
import com.eas.designer.application.query.editing.StoredFieldTypeEdit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class OutputColumnsModel implements TableModel, CollectionListener<Fields, Field>, PropertyChangeListener {

    protected PlatypusQueryDataObject dataObject;
    protected Fields fields;
    protected Set<TableModelListener> listeners = new HashSet<>();
    public static final int COLUMN_TYPE = 0;
    public static final int COLUMN_ALIAS = 1;
    public static final int COLUMN_SOURCE = 2;
    public static final int COLUMN_DESCRIPTION = 3;
    public static final int COLUMN_COUNT = 4;
    protected static final Class<?>[] columnsClasses = new Class<?>[COLUMN_COUNT];
    protected static final String[] columnsLabels = new String[COLUMN_COUNT];

    public OutputColumnsModel(PlatypusQueryDataObject aDataObject) throws Exception {
        super();

        columnsClasses[COLUMN_TYPE] = DataTypeInfo.class;
        columnsClasses[COLUMN_ALIAS] = String.class;
        columnsClasses[COLUMN_SOURCE] = String.class;
        columnsClasses[COLUMN_DESCRIPTION] = String.class;

        columnsLabels[COLUMN_TYPE] = NbBundle.getMessage(OutputColumnsModel.class, "ColumnType");
        columnsLabels[COLUMN_ALIAS] = NbBundle.getMessage(OutputColumnsModel.class, "ColumnAlias");
        columnsLabels[COLUMN_SOURCE] = NbBundle.getMessage(OutputColumnsModel.class, "ColumnSource");
        columnsLabels[COLUMN_DESCRIPTION] = NbBundle.getMessage(OutputColumnsModel.class, "ColumnDescription");

        setDataObject(aDataObject);
    }

    public void setDataObject(PlatypusQueryDataObject aDataObject) throws Exception {
        if (dataObject != null) {
            dataObject.removePropertyChangeListener(this);
        }
        dataObject = aDataObject;
        if (dataObject != null) {
            dataObject.addPropertyChangeListener(this);
            setFields(dataObject.getOutputFields());
        } else {
            setFields(null);
        }
    }

    public void fireCellChanged(int aRow, int aCol) {
        TableModelEvent event = new TableModelEvent(this, aRow - 1, aRow - 1, aCol);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    public void fireEverythingChanged(int oldRowCount) {
        TableModelEvent event = new TableModelEvent(this, 0, oldRowCount - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
        int newRowCount = fields != null ? fields.getFieldsCount() : 0;
        event = new TableModelEvent(this, 0, newRowCount - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        for (TableModelListener l : listeners) {
            l.tableChanged(event);
        }
    }

    public void setFields(Fields aFields) {
        // TODO: Take care of field's property listeners while fields collection changes.
        // It will be needed since fields been edited in a query.
        // But for now, fields collection editing is absent and so, don't need to take care of it.
        if (fields != null) {
            for (Field field : fields.toCollection()) {
                field.getChangeSupport().removePropertyChangeListener(this);
            }
            fields.getCollectionSupport().removeListener(this);
        }
        fields = aFields;
        if (fields != null) {
            fields.getCollectionSupport().addListener(this);
            for (Field field : fields.toCollection()) {
                field.getChangeSupport().addPropertyChangeListener(this);
            }
        }
    }

    @Override
    public int getRowCount() {
        try {
            if (fields != null) {
                return fields.getFieldsCount();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnsLabels[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnsClasses[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != COLUMN_SOURCE;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Field field = fields.get(rowIndex + 1);
            StoredFieldMetadata storedField = findStoredField(field);
            switch (columnIndex) {
                case COLUMN_SOURCE:
                    String colTable = field.getTableName();
                    String colSchema = field.getSchemaName();
                    if (colSchema != null && !colSchema.isEmpty()) {
                        colTable = colSchema + "." + colTable;
                    }
                    return colTable;
                case COLUMN_ALIAS:
                    return field.getName();
                case COLUMN_DESCRIPTION:
                    String descValue = storedField != null ? storedField.getDescription() : field.getDescription();
                    return descValue != null ? descValue : field.getDescription();
                case COLUMN_TYPE:
                    DataTypeInfo typeValue = storedField != null ? storedField.getTypeInfo() : field.getTypeInfo();
                    return typeValue != null ? typeValue : field.getTypeInfo();
                default:
                    return null;
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            Field field = fields.get(rowIndex + 1);
            switch (columnIndex) {
                case COLUMN_SOURCE:
                    break;
                case COLUMN_ALIAS:
                    break;
                case COLUMN_DESCRIPTION:
                    if (aValue == null || aValue instanceof String) {
                        UndoableEdit edit = null;
                        String newDescription = (String) aValue;
                        if (newDescription != null && newDescription.isEmpty()) {
                            newDescription = null;
                        }
                        StoredFieldMetadata storedField = findStoredField(field);
                        if (storedField != null) {// Change or delete
                            if ((newDescription == null || newDescription.equals(field.getDescription()))
                                    && (storedField.getTypeInfo() == null || storedField.getTypeInfo().getSqlType() == field.getTypeInfo().getSqlType())) {
                                edit = new StoredFieldDeleteEdit(dataObject, storedField);
                                edit.redo();
                            } else {
                                edit = new StoredFieldDescriptionEdit(dataObject, storedField, storedField.getDescription(), newDescription);
                                edit.redo();
                            }
                        } else {// Add and change
                            // Such approach allow us to reuse events generation
                            storedField = new StoredFieldMetadata(field.getName());
                            edit = new CompoundEdit();

                            StoredFieldAddEdit addEdit = new StoredFieldAddEdit(dataObject, storedField);
                            StoredFieldDescriptionEdit changeEdit = new StoredFieldDescriptionEdit(dataObject, storedField, storedField.getDescription(), newDescription);

                            addEdit.redo();
                            changeEdit.redo();
                            edit.addEdit(addEdit);
                            edit.addEdit(changeEdit);
                            ((CompoundEdit) edit).end();
                        }
                        postEdit(edit);
                    }
                    break;
                case COLUMN_TYPE:
                    if (aValue == null || aValue instanceof DataTypeInfo) {
                        DataTypeInfo newTypeInfo = (DataTypeInfo) aValue;
                        StoredFieldMetadata storedField = findStoredField(field);
                        UndoableEdit edit = null;
                        if (storedField != null) {// Change or delete
                            if ((storedField.getDescription() == null || storedField.getDescription().equals(field.getDescription()))
                                    && (newTypeInfo == null || newTypeInfo.getSqlType() == field.getTypeInfo().getSqlType())) {
                                edit = new StoredFieldDeleteEdit(dataObject, storedField);
                                edit.redo();
                            } else {
                                edit = new StoredFieldTypeEdit(dataObject, storedField, storedField.getTypeInfo(), newTypeInfo);
                                edit.redo();
                            }
                        } else {// Add and change
                            // Such approach allow us to reuse events generation
                            storedField = new StoredFieldMetadata(field.getName());
                            edit = new CompoundEdit();

                            StoredFieldAddEdit addEdit = new StoredFieldAddEdit(dataObject, storedField);
                            StoredFieldTypeEdit changeEdit = new StoredFieldTypeEdit(dataObject, storedField, storedField.getTypeInfo(), newTypeInfo);

                            addEdit.redo();
                            changeEdit.redo();
                            edit.addEdit(addEdit);
                            edit.addEdit(changeEdit);
                            ((CompoundEdit) edit).end();
                        }
                        postEdit(edit);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
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

    @Override
    public void added(Fields c, Field v) {
    }

    @Override
    public void added(Fields c, Collection<Field> clctn) {
    }

    @Override
    public void removed(Fields c, Field v) {
    }

    @Override
    public void removed(Fields c, Collection<Field> clctn) {
    }

    @Override
    public void cleared(Fields c) {
    }
    
        @Override
    public void reodered(Fields c) {
    }
    
    private StoredFieldMetadata findStoredField(Field field) throws Exception {
        for (StoredFieldMetadata addition : dataObject.getOutputFieldsHints()) {
            if (addition.getBindedColumn().equalsIgnoreCase(field.getName())) {
                return addition;
            }
        }
        return null;
    }

    private void postEdit(UndoableEdit edit) {
        dataObject.getLookup().lookup(PlatypusQuerySupport.class).getModelUndo().addEdit(edit);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Field) {
            Field field = (Field) evt.getSource();
            int fIndex = fields.find(field.getName());
            if (fIndex >= 1 && fIndex <= fields.getFieldsCount()) {
                if ("typeInfo".equals(evt.getPropertyName())) {
                    fireCellChanged(fIndex, COLUMN_TYPE);
                }
                if ("description".equals(evt.getPropertyName())) {
                    fireCellChanged(fIndex, COLUMN_DESCRIPTION);
                }
            }
        } else if (evt.getSource() == dataObject) {
            if (PlatypusQueryDataObject.OUTPUT_FIELDS.equals(evt.getPropertyName())) {
                setFields((Fields) evt.getNewValue());
                fireEverythingChanged(evt.getOldValue() != null ? ((Fields) evt.getOldValue()).getFieldsCount() : 0);
            }
        }
    }
}

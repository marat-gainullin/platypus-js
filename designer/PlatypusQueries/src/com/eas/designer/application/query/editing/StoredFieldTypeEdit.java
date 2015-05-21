/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class StoredFieldTypeEdit extends StoredFieldEdit {

    protected DataTypeInfo oldTypeInfo;
    protected DataTypeInfo newTypeInfo;

    public StoredFieldTypeEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField, DataTypeInfo aOldTypeInfo, DataTypeInfo aNewTypeInfo) {
        super(aDataObject, aStoredField);
        oldTypeInfo = aOldTypeInfo;
        newTypeInfo = aNewTypeInfo;
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            Fields fields = dataObject.getOutputFields();
            if (fields.contains(storedField.getBindedColumn())) {
                Field field = fields.get(storedField.getBindedColumn());
                DataTypeInfo oldValue = storedField.getTypeInfo();
                storedField.setTypeInfo(oldTypeInfo);
                field.getChangeSupport().firePropertyChange(Field.TYPE_INFO_PROPERTY, oldValue, storedField.getTypeInfo());
            }
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            Fields fields = dataObject.getOutputFields();
            if (fields.contains(storedField.getBindedColumn())) {
                Field field = fields.get(storedField.getBindedColumn());
                DataTypeInfo oldValue = storedField.getTypeInfo();
                storedField.setTypeInfo(newTypeInfo);
                field.getChangeSupport().firePropertyChange(Field.TYPE_INFO_PROPERTY, oldValue, storedField.getTypeInfo());
            }
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }
}

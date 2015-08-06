/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

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

    protected String oldTypeInfo;
    protected String newTypeInfo;

    public StoredFieldTypeEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField, String aOldTypeInfo, String aNewTypeInfo) {
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
                String oldValue = storedField.getType();
                storedField.setType(oldTypeInfo);
                field.getChangeSupport().firePropertyChange(Field.TYPE_PROPERTY, oldValue, storedField.getType());
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
                String oldValue = storedField.getType();
                storedField.setType(newTypeInfo);
                field.getChangeSupport().firePropertyChange(Field.TYPE_PROPERTY, oldValue, storedField.getType());
            }
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }
}

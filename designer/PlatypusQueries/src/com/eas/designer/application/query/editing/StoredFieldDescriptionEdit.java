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
public class StoredFieldDescriptionEdit extends StoredFieldEdit {

    protected String oldDescription;
    protected String newDescription;

    public StoredFieldDescriptionEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField, String aOldDescription, String aNewDescription) {
        super(aDataObject, aStoredField);
        oldDescription = aOldDescription;
        newDescription = aNewDescription;
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            Fields fields = dataObject.getOutputFields();
            if (fields.contains(storedField.getBindedColumn())) {
                Field field = fields.get(storedField.getBindedColumn());
                String oldValue = storedField.getDescription();
                storedField.setDescription(oldDescription);
                field.getChangeSupport().firePropertyChange("description", oldValue, storedField.getDescription());
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
                String oldValue = storedField.getDescription();
                storedField.setDescription(newDescription);
                field.getChangeSupport().firePropertyChange("description", oldValue, storedField.getDescription());
            }
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.metadata.Field;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class StoredFieldDeleteEdit extends StoredFieldEdit {

    public StoredFieldDeleteEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField) {
        super(aDataObject, aStoredField);
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            dataObject.getOutputFieldsHints().add(storedField);
            Field field = dataObject.getOutputFields().get(storedField.getBindedColumn());
            if (field != null) {
                field.getChangeSupport().firePropertyChange(Field.TYPE_PROPERTY, null, field.getType());
            }
            dataObject.setOutputFieldsHintsModified(true);
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }

    @Override
    public boolean canUndo() {
        return super.canUndo();
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            dataObject.getOutputFieldsHints().remove(storedField);
            Field field = dataObject.getOutputFields().get(storedField.getBindedColumn());
            if (field != null) {
                field.getChangeSupport().firePropertyChange(Field.TYPE_PROPERTY, null, field.getType());
            }
            dataObject.setOutputFieldsHintsModified(true);
        } catch (Exception ex) {
            CannotUndoException lex = new CannotUndoException();
            lex.initCause(ex);
            throw lex;
        }
    }

    @Override
    public boolean canRedo() {
        return true;
    }
}

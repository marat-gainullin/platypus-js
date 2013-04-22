/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.query.editing;

import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class StoredFieldAddEdit extends StoredFieldDeleteEdit{

    public StoredFieldAddEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField) {
        super(aDataObject, aStoredField);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.redo();
    }

    @Override
    public boolean canUndo() {
        return super.canRedo();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.undo();
    }
    
    @Override
    public boolean canRedo() {
        return super.canUndo();
    }
}

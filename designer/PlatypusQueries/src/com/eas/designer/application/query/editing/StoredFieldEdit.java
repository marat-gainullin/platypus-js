/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.metadata.Fields;
import com.eas.client.model.QueryDocument.StoredFieldMetadata;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import javax.swing.undo.UndoableEdit;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public abstract class StoredFieldEdit implements UndoableEdit {

    protected PlatypusQueryDataObject dataObject;
    protected StoredFieldMetadata storedField;

    public StoredFieldEdit(PlatypusQueryDataObject aDataObject, StoredFieldMetadata aStoredField) {
        dataObject = aDataObject;
        storedField = aStoredField;
    }

    @Override
    public boolean canUndo() {
        if (storedField != null) {
            try {
                Fields fields = dataObject.getOutputFields();
                return fields.contains(storedField.getBindedColumn());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canRedo() {
        if (storedField != null) {
            try {
                Fields fields = dataObject.getOutputFields();
                return fields.contains(storedField.getBindedColumn());
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void die() {
        storedField = null;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return storedField != null;
    }

    @Override
    public String getPresentationName() {
        return "";
    }

    @Override
    public String getUndoPresentationName() {
        return getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return getPresentationName();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.edits;

import com.eas.client.geo.RowsetFeatureDescriptor;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author pk
 */
public class ModifyCRSEdit extends AbstractUndoableEdit {

    private final RowsetFeatureDescriptor feature;
    private final String oldValue;
    private final String newValue;

    public ModifyCRSEdit(RowsetFeatureDescriptor aDescriptor, String aOldValue, String aNewValue) {
        feature = aDescriptor;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        feature.setCrsWkt(newValue);
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        feature.setCrsWkt(oldValue);
    }
}

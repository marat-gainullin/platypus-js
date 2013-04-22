/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.edits;

import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.RowsetFeatureDescriptor;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author pk
 */
public class ModifyStyleEdit extends AbstractUndoableEdit
{
    private RowsetFeatureDescriptor feature;
    private FeatureStyleDescriptor oldValue, newValue;

    public ModifyStyleEdit(RowsetFeatureDescriptor aDescriptor, FeatureStyleDescriptor aOldValue, FeatureStyleDescriptor aNewValue)
    {
        feature = aDescriptor;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void die()
    {
        feature = null;
        oldValue = null;
        newValue = null;
        super.die();
    }

    @Override
    public void redo() throws CannotRedoException
    {
        try {
            super.redo();
            feature.setStyle(newValue);
        } catch (Exception ex) {
            throw new CannotRedoException();
        }
    }

    @Override
    public void undo() throws CannotUndoException
    {
        try {
            super.undo();
            feature.setStyle(oldValue);
        } catch (Exception ex) {
            throw new CannotUndoException();
        }
    }
}

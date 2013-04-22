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
public class ModifyBindingEdit extends AbstractUndoableEdit{
    private final String oldValue;
    private final RowsetFeatureDescriptor desc;
    private final String newValue;

    public ModifyBindingEdit(RowsetFeatureDescriptor aDesc, String aOldValue, String aNewValue)
    {
        desc = aDesc;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        desc.setGeometryBinding(newValue);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        desc.setGeometryBinding(oldValue);
    }
}

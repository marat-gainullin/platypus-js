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
 * @author mg
 */
public class ModifySelectableEdit extends AbstractUndoableEdit
{
    private RowsetFeatureDescriptor desc;
    private final boolean oldValue;
    private final boolean newValue;

    public ModifySelectableEdit(RowsetFeatureDescriptor aDescriptor, boolean aOldValue, boolean aNewValue)
    {
        super();
        desc = aDescriptor;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void die()
    {
        super.die();
        desc = null;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        desc.setSelectable(newValue);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        desc.setSelectable(oldValue);
    }
}

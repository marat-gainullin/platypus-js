/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.edits;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author pk
 */
public class ModifyFeaturesEdit extends AbstractUndoableEdit
{
    private DbMapDesignInfo designInfo;
    private List<RowsetFeatureDescriptor> oldValue;
    private List<RowsetFeatureDescriptor> newValue;

    public ModifyFeaturesEdit(DbMapDesignInfo aDesignInfo, List<RowsetFeatureDescriptor> aOldValue, List<RowsetFeatureDescriptor> aNewValue)
    {
        super();
        designInfo = aDesignInfo;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void die()
    {
        super.die();
        designInfo = null;
        oldValue = null;
        newValue = null;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        designInfo.setFeatures(newValue);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        designInfo.setFeatures(oldValue);
    }

    @Override
    public boolean canUndo() {
        return super.isSignificant();
    }

    @Override
    public boolean canRedo() {
        return super.isSignificant();
    }
}

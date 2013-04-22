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
public class ModifyTypeValueEdit extends AbstractUndoableEdit{
    private final RowsetFeatureDescriptor feature;
    private final Integer oldValue;
    private final Integer newValue;

    public ModifyTypeValueEdit(RowsetFeatureDescriptor aDescriptor, Integer aOldValue, Integer aNewValue)
    {
        feature = aDescriptor;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        feature.setTypeValue(newValue);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        feature.setTypeValue(oldValue);
    }
}

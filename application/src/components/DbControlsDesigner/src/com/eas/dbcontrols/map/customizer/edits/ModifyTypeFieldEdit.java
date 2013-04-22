/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.map.customizer.edits;

import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.geo.RowsetFeatureDescriptor;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class ModifyTypeFieldEdit  extends AbstractUndoableEdit{
    private final RowsetFeatureDescriptor feature;
    private final ModelElementRef oldValue;
    private final ModelElementRef newValue;

    public ModifyTypeFieldEdit(RowsetFeatureDescriptor aDescriptor, ModelElementRef aOldValue, ModelElementRef aNewValue)
    {
        super();
        feature = aDescriptor;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        feature.setTypeRef(newValue);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        feature.setTypeRef(oldValue);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.edits;

import com.eas.dbcontrols.map.customizer.ProjectionParametersTableModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.geotools.parameter.Parameter;

/**
 *
 * @author pk
 */
public class ModifyParameterValueEdit extends AbstractUndoableEdit
{
    private ProjectionParametersTableModel model;
    private Parameter parameter;
    private Object oldValue;
    private Object newValue;

    public ModifyParameterValueEdit(ProjectionParametersTableModel aModel, Parameter aParameter, Object aOldValue, Object aNewValue)
    {
        super();
        model = aModel;
        parameter = aParameter;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    @Override
    public void die()
    {
        model = null;
        parameter=null;
        oldValue=null;
        newValue=null;
        super.die();
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        parameter.setValue(newValue);
        model.parameterChanged(parameter);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        parameter.setValue(oldValue);
        model.parameterChanged(parameter);
    }
}

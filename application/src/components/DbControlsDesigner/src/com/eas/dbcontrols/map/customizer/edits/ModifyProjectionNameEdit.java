/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map.customizer.edits;

import com.eas.dbcontrols.map.DbMapDesignInfo;
import java.util.logging.Logger;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author pk
 */
public class ModifyProjectionNameEdit extends AbstractUndoableEdit
{
    private DbMapDesignInfo designInfo;
    private String oldName;
    private String newName;

    public ModifyProjectionNameEdit(DbMapDesignInfo aDesignInfo, String aOldName, String aNewName)
    {
        super();
        designInfo = aDesignInfo;
        oldName = aOldName;
        newName = aNewName;
    }

    @Override
    public void die()
    {
        super.die();
        designInfo = null;
        oldName = null;
        newName = null;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        super.redo();
        Logger.getLogger(ModifyProjectionNameEdit.class.getName()).finest(String.format("Modifying projection from %s to %s", oldName, newName));
        designInfo.setProjectionName(newName);
    }

    @Override
    public void undo() throws CannotUndoException
    {
        super.undo();
        Logger.getLogger(ModifyProjectionNameEdit.class.getName()).finest(String.format("Modifying projection from %s to %s", newName, oldName));
        designInfo.setProjectionName(oldName);
    }
}

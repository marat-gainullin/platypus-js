/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlEdit;
import com.eas.dbcontrols.grid.DbGridColumn;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbColumnControlChangeEdit extends DbControlEdit
{
    protected DbGridColumn column = null;
    protected DbControlDesignInfo before = null;
    protected DbControlDesignInfo after = null;

    public DbColumnControlChangeEdit(DbGridColumn aColumn, DbControlDesignInfo aBefore, DbControlDesignInfo aAfter)
    {
        super();
        column = aColumn;
        before = aBefore;
        after = aAfter;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        if(column != null)
            column.setControlInfo(before);
    }

    @Override
    public void redo() throws CannotRedoException
    {
        if(column != null)
            column.setControlInfo(after);
    }

    @Override
    public void die()
    {
        column = null;
        before = null;
        after = null;
        super.die();
    }


}

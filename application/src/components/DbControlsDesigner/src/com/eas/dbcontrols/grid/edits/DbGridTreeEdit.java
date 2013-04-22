/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlEdit;
import com.eas.dbcontrols.FakeDbControlEvents;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.grid.DbGridTreeDesignInfo;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbGridTreeEdit extends DbControlEdit
{
    public static Method linkMethod = null;

    static {
        try {
            linkMethod = FakeDbControlEvents.class.getMethod("link", new Class[]{Object.class});
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DbGridTreeEdit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DbGridTreeEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected DbGridDesignInfo gridInfo = null;

    protected DbGridTreeDesignInfo before = null;
    protected DbGridTreeDesignInfo after = null;

    public DbGridTreeEdit(DbGridDesignInfo gridInfo, DbGridTreeDesignInfo before, DbGridTreeDesignInfo after)
    {
        super();
        this.gridInfo = gridInfo;
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        if(gridInfo != null)
            gridInfo.setTreeDesignInfo(before);
    }

    @Override
    public void redo() throws CannotRedoException
    {
        if(gridInfo != null)
            gridInfo.setTreeDesignInfo(after);
    }

    @Override
    public void die()
    {
        gridInfo = null;
        before = null;
        after = null;
        super.die();
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.DbControlEdit;
import com.eas.dbcontrols.FakeDbControlEvents;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.grid.DbGridColumn;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbColumnChangeEdit extends DbControlEdit {

    public static Method handleCellMethod = null;

    static {
        try {
            handleCellMethod = FakeDbControlEvents.class.getMethod("handleCell", new Class[]{Object.class, Object.class, Object.class, Object.class});
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(DbColumnChangeEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    protected ScriptEvents scriptEvents;
    protected DbGridColumn column = null;
    protected DbGridColumn before = null;
    protected DbGridColumn after = null;

    public DbColumnChangeEdit(ScriptEvents aScriptEvents, DbGridColumn aColumn, DbGridColumn aBefore, DbGridColumn aAfter) {
        super();
        scriptEvents = aScriptEvents;
        column = aColumn;
        before = aBefore;
        after = aAfter;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (column != null) {
            column.lightAssign(before);
            column.setControlInfo(before.getControlInfo());
            column.getCellDesignInfo().setCellControlInfo(before.getCellDesignInfo().getCellControlInfo());
            if ((after.getCellFunction() == null) ? (before.getCellFunction() != null) : !after.getCellFunction().equals(before.getCellFunction())) {
                DbControlChangeEdit.synchronizeEvents(scriptEvents, handleCellMethod, after.getCellFunction(), before.getCellFunction());
            }
            if ((after.getSelectFunction() == null) ? (before.getSelectFunction() != null) : !after.getSelectFunction().equals(before.getSelectFunction())) {
                DbControlChangeEdit.synchronizeEvents(scriptEvents, DbControlChangeEdit.selectValueMethod, after.getSelectFunction(), before.getSelectFunction());
            }
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        if (column != null) {
            column.lightAssign(after);
            column.setControlInfo(after.getControlInfo());
            column.getCellDesignInfo().setCellControlInfo(after.getCellDesignInfo().getCellControlInfo());

            if ((before.getCellFunction() == null) ? (after.getCellFunction() != null) : !before.getCellFunction().equals(after.getCellFunction())) {
                DbControlChangeEdit.synchronizeEvents(scriptEvents, handleCellMethod, before.getCellFunction(), after.getCellFunction());
            }
            if ((before.getSelectFunction() == null) ? (after.getSelectFunction() != null) : !before.getSelectFunction().equals(after.getSelectFunction())) {
                DbControlChangeEdit.synchronizeEvents(scriptEvents, DbControlChangeEdit.selectValueMethod, before.getSelectFunction(), after.getSelectFunction());
            }
        }
    }

    @Override
    public void die() {
        column = null;
        before = null;
        after = null;
        super.die();
    }
}

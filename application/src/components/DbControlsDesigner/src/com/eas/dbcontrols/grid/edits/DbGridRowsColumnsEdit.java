/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.grid.DbGridRowsColumnsDesignInfo;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbGridRowsColumnsEdit extends DbGridGeneralEdit {

    protected ScriptEvents scriptEvents;
    protected DbGridRowsColumnsDesignInfo before = null;
    protected DbGridRowsColumnsDesignInfo after = null;

    public DbGridRowsColumnsEdit(ScriptEvents aScriptEvents, DbGridDesignInfo aGridInfo, DbGridRowsColumnsDesignInfo aBefore, DbGridRowsColumnsDesignInfo aAfter) {
        super(aGridInfo);
        scriptEvents = aScriptEvents;
        before = aBefore;
        after = aAfter;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (gridInfo != null) {
            // old rows info - after
            // new rows info - before
            if (before.getRowsDatasource() != null) {
                Long newRowsDatasourceID = before.getRowsDatasource().getEntityId();
                gridInfo.replaceRowsDatasourceID(newRowsDatasourceID);
            }
            gridInfo.setRowsColumnsDesignInfo(before);
            DbControlChangeEdit.synchronizeEvents(scriptEvents, DbColumnChangeEdit.handleCellMethod, after.getGeneralRowFunction(), before.getGeneralRowFunction());
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        if (gridInfo != null) {
            // old rows info - before
            // new rows info - after
            if (after.getRowsDatasource() != null) {
                Long newRowsDatasourceID = after.getRowsDatasource().getEntityId();
                gridInfo.replaceRowsDatasourceID(newRowsDatasourceID);
            }
            gridInfo.setRowsColumnsDesignInfo(after);
            DbControlChangeEdit.synchronizeEvents(scriptEvents, DbColumnChangeEdit.handleCellMethod, before.getGeneralRowFunction(), after.getGeneralRowFunction());
        }
    }

    @Override
    public void die() {
        before = null;
        after = null;
        super.die();
    }
}

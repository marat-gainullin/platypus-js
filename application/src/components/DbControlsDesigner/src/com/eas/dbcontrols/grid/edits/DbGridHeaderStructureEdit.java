/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlChangeEdit;
import com.eas.dbcontrols.DbControlEdit;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import java.util.Collection;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author mg
 */
public class DbGridHeaderStructureEdit extends DbControlEdit {

    protected ScriptEvents scriptEvents;
    protected DbGridDesignInfo gridInfo = null;
    protected HeaderStructure before = null;
    protected HeaderStructure after = null;

    public DbGridHeaderStructureEdit(ScriptEvents aScriptEvents, DbGridDesignInfo aGridInfo) {
        super();
        scriptEvents = aScriptEvents;
        gridInfo = aGridInfo;
        before = HeaderStructure.grabHeaderStructure(gridInfo);
    }

    public void grabStructureAfter() {
        after = HeaderStructure.grabHeaderStructure(gridInfo);
    }

    @Override
    public void undo() throws CannotUndoException {
        before.applyStructure(gridInfo);
        synchronizeEvents(after, before);
    }

    @Override
    public void redo() throws CannotRedoException {
        after.applyStructure(gridInfo);
        synchronizeEvents(before, after);
    }

    @Override
    public void die() {
        gridInfo = null;
        before = null;
        after = null;
        super.die();
    }

    protected void synchronizeEvents(HeaderStructure oldStructure, HeaderStructure newStructure) {
        // Let's define witch columns were added
        Collection<DbGridColumn> oldCols = oldStructure.toCollection();
        Collection<DbGridColumn> newCols = newStructure.toCollection();
        newCols.removeAll(oldCols);
        // Now newCols collection conatains only added columns
        for (DbGridColumn col : newCols) {
            scriptEvents.incHandlerUseWithoutPositioning(DbControlChangeEdit.selectValueMethod, col.getSelectFunction());
            scriptEvents.incHandlerUseWithoutPositioning(DbColumnChangeEdit.handleCellMethod, col.getCellFunction());
        }

        // Let's define witch columns were removed
        oldCols = oldStructure.toCollection();
        newCols = newStructure.toCollection();
        // Now oldCols collection conatains only removed columns
        oldCols.removeAll(newCols);
        for (DbGridColumn col : oldCols) {
            scriptEvents.decHandlerUse(col.getSelectFunction());
            scriptEvents.decHandlerUse(col.getCellFunction());
        }
    }

    public void synchronizeEventsRedo() {
        synchronizeEvents(before, after);
    }
}

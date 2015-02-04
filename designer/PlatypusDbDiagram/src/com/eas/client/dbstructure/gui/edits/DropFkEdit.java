/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.eas.client.dbstructure.SqlActionsController;

/**
 *
 * @author mg
 */
public class DropFkEdit extends CreateFkEdit {

    public DropFkEdit(SqlActionsController aSqlController, ForeignKeySpec aFk, Field aFmd) {
        super(aSqlController, aFk, aFmd);
    }

    @Override
    protected void doRedoWork() throws Exception {
        super.doUndoWork();
    }

    @Override
    protected void doUndoWork() throws Exception {
        super.doRedoWork();
    }
}

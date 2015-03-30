/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;

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

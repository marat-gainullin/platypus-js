/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DropConstraintAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;

/**
 *
 * @author mg
 */
public class CreateFkEdit extends DbStructureEdit {

    protected ForeignKeySpec fk;
    protected Field fmd;

    public CreateFkEdit(SqlActionsController aSqlController, ForeignKeySpec aFk, Field aFmd) {
        super(aSqlController);
        fk = aFk;
        fmd = aFmd;
    }

    @Override
    protected void doUndoWork() throws Exception {
        DropConstraintAction sqlAction = sqlController.createDropConstraintAction(fk);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(fk.getTable());
            ex.setParam2(fk.getField());
            throw ex;
        } else {
            if (fmd != null) {
                fmd.setFk(null);
            }
        }
        sqlController.tableChanged(fmd.getTableName());
    }

    @Override
    protected void doRedoWork() throws Exception {
        CreateConstraintAction sqlAction = sqlController.createCreateConstraintAction(fk);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(fk.getTable());
            ex.setParam2(fk.getField());
            throw ex;
        } else {
            if (fmd != null) {
                fmd.setFk(fk);
            }
        }
        sqlController.tableChanged(fmd.getTableName());
    }

    @Override
    protected String getChangingTableName() {
        return fk.getTable();
    }
}

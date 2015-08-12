/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DropConstraintAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.ForeignKeySpec;

/**
 *
 * @author mg
 */
public class RecreateFkEdit extends DbStructureEdit {

    protected ForeignKeySpec oldFkSpec;
    protected ForeignKeySpec newFkSpec;

    public RecreateFkEdit(SqlActionsController aSqlController, ForeignKeySpec aOldFkSpec, ForeignKeySpec aNewFkSpec) {
        super(aSqlController);
        oldFkSpec = aOldFkSpec;
        newFkSpec = aNewFkSpec;
    }

    @Override
    protected void doUndoWork() throws Exception {
        dropBySpec(newFkSpec);
        createBySpec(oldFkSpec);
        sqlController.tableChanged(newFkSpec.getTable());
    }

    @Override
    protected void doRedoWork() throws Exception {
        dropBySpec(oldFkSpec);
        createBySpec(newFkSpec);
        sqlController.tableChanged(newFkSpec.getTable());
    }

    private void createBySpec(ForeignKeySpec aFk) throws DbActionException {
        CreateConstraintAction sqlAction = sqlController.createCreateConstraintAction(aFk);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(aFk.getTable());
            ex.setParam2(aFk.getField());
            throw ex;
        }
    }

    private void dropBySpec(ForeignKeySpec fk) throws DbActionException {
        DropConstraintAction sqlAction = sqlController.createDropConstraintAction(fk);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(fk.getTable());
            ex.setParam2(fk.getField());
            throw ex;
        }
    }
}

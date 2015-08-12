/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateTableAction;
import com.eas.client.dbstructure.SqlActionsController.DropTableAction;
import com.eas.client.dbstructure.exceptions.DbActionException;

/**
 *
 * @author mg
 */
public class CreateTableEdit extends DbStructureEdit {

    protected String tableName;

    public CreateTableEdit(SqlActionsController aSqlController, String aTableName) {
        super(aSqlController);
        tableName = aTableName;
    }

    @Override
    protected void doUndoWork() throws Exception {
        DropTableAction laction = sqlController.createDropTableAction(tableName);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            throw ex;
        }
        sqlController.tableRemoved(tableName);
    }

    @Override
    protected void doRedoWork() throws Exception {
        CreateTableAction laction = sqlController.createCreateTableAction(tableName, tableName + "_Id");
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            throw ex;
        }
        sqlController.tableAdded(tableName);
    }

    @Override
    protected String getChangingTableName() {
        return tableName;
    }
}

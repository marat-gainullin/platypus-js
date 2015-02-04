/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.SqlAction;
import com.eas.client.dbstructure.exceptions.DbActionException;

/**
 *
 * @author mg
 */
public class ChangeTableCommentEdit extends DbStructureEdit {

    protected String tableName;
    protected String oldComment;
    protected String newComment;

    public ChangeTableCommentEdit(SqlActionsController aSqlController, String aTableName, String aOldComment, String aNewComment) {
        super(aSqlController);
        tableName = aTableName;
        oldComment = aOldComment;
        if (oldComment == null) {
            oldComment = "";
        }
        newComment = aNewComment;
        if (newComment == null) {
            newComment = "";
        }
    }

    @Override
    protected void doUndoWork() throws Exception {
        SqlAction laction = sqlController.createDescribeTableAction(tableName, oldComment);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            throw ex;
        }
    }

    @Override
    protected void doRedoWork() throws Exception {
        SqlAction laction = sqlController.createDescribeTableAction(tableName, newComment);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            throw ex;
        }
    }

    @Override
    protected String getChangingTableName() {
        return tableName;
    }
}

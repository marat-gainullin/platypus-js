/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.DescribeFieldAction;
import com.eas.client.dbstructure.SqlActionsController.ModifyFieldAction;
import com.eas.client.dbstructure.SqlActionsController.RenameFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.JdbcField;

/**
 *
 * @author mg
 */
public class ModifyFieldEdit extends DbStructureEdit {

    protected Fields fields;
    protected JdbcField oldFieldSpec;
    protected JdbcField newFieldSpec;
    protected String tableName;
    protected boolean namesAreDifferent;
    protected boolean contentsAreDifferent;
    protected boolean commentsAreDifferent;

    public ModifyFieldEdit(SqlActionsController aSqlController, String aTableName, Fields aFields, JdbcField aOldFieldSpec, JdbcField aNewFieldSpec) {
        super(aSqlController);
        fields = aFields;
        oldFieldSpec = aOldFieldSpec;
        newFieldSpec = aNewFieldSpec;
        tableName = aTableName;
        namesAreDifferent = !oldFieldSpec.getName().toLowerCase().equals(newFieldSpec.getName().toLowerCase());
        String oldDescription = oldFieldSpec.getDescription() != null ? oldFieldSpec.getDescription().toLowerCase() : "";
        String newDescription = newFieldSpec.getDescription() != null ? newFieldSpec.getDescription().toLowerCase() : "";
        commentsAreDifferent = !oldDescription.equals(newDescription);
        Field lNewFieldSpec = new Field(newFieldSpec);
        lNewFieldSpec.setName(oldFieldSpec.getName());
        lNewFieldSpec.setDescription(oldFieldSpec.getDescription());
        contentsAreDifferent = !oldFieldSpec.isEqual(lNewFieldSpec);
        assert contentsAreDifferent || namesAreDifferent || commentsAreDifferent;
    }

    @Override
    protected void doUndoWork() throws Exception {
        if (namesAreDifferent) {
            fields.invalidateFieldsHash();
            RenameFieldAction sqlAction1 = sqlController.createRenameFieldAction(tableName, newFieldSpec.getName(), oldFieldSpec);
            if (!sqlAction1.execute()) {
                DbActionException ex = new DbActionException(sqlAction1.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(newFieldSpec.getName());
                throw ex;
            }
        }
        if (contentsAreDifferent) {
            ModifyFieldAction sqlAction = sqlController.createModifyFieldAction(tableName, newFieldSpec, oldFieldSpec);
            if (!sqlAction.execute()) {
                DbActionException ex = new DbActionException(sqlAction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(newFieldSpec.getName());
                throw ex;
            }
        }
        if (commentsAreDifferent) {
            DescribeFieldAction sqlAction = sqlController.createDescribeFieldAction(tableName, oldFieldSpec.getName(), oldFieldSpec.getDescription());
            if (!sqlAction.execute()) {
                DbActionException ex = new DbActionException(sqlAction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(oldFieldSpec.getName());
                throw ex;
            }
        }
        sqlController.tableChanged(tableName);
    }

    @Override
    protected void doRedoWork() throws Exception {
        if (namesAreDifferent) {
            fields.invalidateFieldsHash();
            RenameFieldAction sqlAction1 = sqlController.createRenameFieldAction(tableName, oldFieldSpec.getName(), newFieldSpec);
            if (!sqlAction1.execute()) {
                DbActionException ex = new DbActionException(sqlAction1.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(oldFieldSpec.getName());
                throw ex;
            }
        }
        if (contentsAreDifferent) {
            ModifyFieldAction sqlAction = sqlController.createModifyFieldAction(tableName, oldFieldSpec, newFieldSpec);
            if (!sqlAction.execute()) {
                DbActionException ex = new DbActionException(sqlAction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(oldFieldSpec.getName());
                throw ex;
            }
        }
        if (commentsAreDifferent) {
            DescribeFieldAction sqlAction = sqlController.createDescribeFieldAction(tableName, newFieldSpec.getName(), newFieldSpec.getDescription());
            if (!sqlAction.execute()) {
                DbActionException ex = new DbActionException(sqlAction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(newFieldSpec.getName());
                throw ex;
            }
        }
        sqlController.tableChanged(tableName);
    }

    @Override
    protected String getChangingTableName() {
        return tableName;
    }
}

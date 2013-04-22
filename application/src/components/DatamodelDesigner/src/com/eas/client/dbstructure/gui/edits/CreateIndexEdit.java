/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateIndexAction;
import com.eas.client.dbstructure.SqlActionsController.DropIndexAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.edits.NotSavable;

/**
 *
 * @author mg
 */
public class CreateIndexEdit extends DbStructureEdit implements NotSavable {

    protected DbTableIndexSpec indexSpec;
    protected FieldsEntity entity;
    protected int indexPosition = -1;

    public CreateIndexEdit(SqlActionsController aSqlController, FieldsEntity aEntity, DbTableIndexSpec aIndex, int aIndexPosition) {
        super(aSqlController);
        entity = aEntity;
        indexSpec = aIndex;
        indexPosition = aIndexPosition;
    }

    @Override
    protected void doUndoWork() throws Exception {
        DropIndexAction sqlAction = sqlController.createDropIndexAction(entity.getTableName(), indexSpec);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(entity.getTableName());
            ex.setParam2(indexSpec.getName());
            throw ex;
        } else {
            entity.getModel().removeEntityIndex(entity, indexSpec);
        }
    }

    @Override
    protected void doRedoWork() throws Exception {
        CreateIndexAction sqlAction = sqlController.createCreateIndexAction(entity.getTableName(), indexSpec);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(entity.getTableName());
            ex.setParam2(indexSpec.getName());
            throw ex;
        } else {
            entity.getModel().addEntityIndex(entity, indexSpec, indexPosition);
        }
    }

    @Override
    protected String getChangingTableName() {
        return entity.getTableName();
    }
}

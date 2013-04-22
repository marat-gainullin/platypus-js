/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.SQLUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.AddFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DescribeFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DropFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.model.dbscheme.FieldsEntity;

/**
 *
 * @author mg
 */
public class NewFieldEdit extends DbStructureEdit {

    protected Field field = null;
    protected FieldsEntity entity = null;

    public NewFieldEdit(SqlActionsController aSqlController, FieldsEntity aFieldsEntity, Field aField) {
        super(aSqlController);
        field = aField;
        entity = aFieldsEntity;
        createFieldIfNeeded();
    }

    @Override
    protected void doUndoWork() throws Exception {
        DropFieldAction sqlAction = sqlController.createDropFieldAction(entity.getTableName(), field);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(field.getTableName());
            ex.setParam2(field.getName());
            throw ex;
        }
    }

    @Override
    protected void doRedoWork() throws Exception {
        AddFieldAction sqlAction = sqlController.createAddFieldAction(entity.getTableName(), field);
        if (!sqlAction.execute()) {
            DbActionException ex = new DbActionException(sqlAction.getErrorString());
            ex.setParam1(field.getTableName());
            ex.setParam2(field.getName());
            throw ex;
        }
        assert field.getName() != null && !field.getName().isEmpty();
        if (field.getDescription() != null && !field.getDescription().isEmpty()) {
            DescribeFieldAction sqlDescirbeAction = sqlController.createDescribeFieldAction(entity.getTableName(), field.getName(), field.getDescription());
            if (!sqlDescirbeAction.execute()) {
                DbActionException ex = new DbActionException(sqlDescirbeAction.getErrorString());
                ex.setParam1(field.getTableName());
                ex.setParam2(field.getName());
                throw ex;
            }
        }
    }

    private void createFieldIfNeeded() {
        if (field == null) {
            field = createField();
        }
    }

    private Field createField() {
        com.eas.client.model.gui.edits.fields.NewFieldEdit<FieldsEntity> substEdit = new com.eas.client.model.gui.edits.fields.NewFieldEdit<>(entity);
        Field rsmd = substEdit.getField();
        if (SQLUtils.isSameTypeGroup(rsmd.getTypeInfo().getSqlType(), java.sql.Types.VARCHAR)
                && rsmd.getSize() == 0) {
            rsmd.setSize(100);
        }
        rsmd.setTableName(entity.getTableName());
        return rsmd;
    }

    @Override
    protected String getChangingTableName() {
        return entity.getTableName();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.AddFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DescribeFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DropFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.Field;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.sqldrivers.resolvers.TypesResolver;

/**
 *
 * @author mg
 */
public class NewFieldEdit extends DbStructureEdit {

    protected Field field;
    protected FieldsEntity entity;

    public NewFieldEdit(SqlActionsController aSqlController, FieldsEntity aFieldsEntity, Field aField) throws Exception {
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

    public Field getField() {
        return field;
    }

    public static Field createField(FieldsEntity aEntity) throws Exception {
        com.eas.client.model.gui.edits.fields.NewFieldEdit<FieldsEntity> substEdit = new com.eas.client.model.gui.edits.fields.NewFieldEdit<>(aEntity);
        Field rsmd = substEdit.getField();
        TypesResolver resolver = aEntity.getModel().getBasesProxy().getMetadataCache(aEntity.getModel().getDatasourceName()).getDatasourceSqlDriver().getTypesResolver();
        String defaultType = resolver.getSupportedTypes().iterator().next();
        rsmd.setType(defaultType);
        rsmd.setSize(200);
        rsmd.setTableName(aEntity.getTableName());
        return rsmd;
    }
    
    @Override
    protected String getChangingTableName() {
        return entity.getTableName();
    }
    
    private void createFieldIfNeeded() throws Exception {
        if (field == null) {
            field = createField(entity);
        }
    }
}

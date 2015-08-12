/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.AddFieldAction;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DescribeFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DropConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DropFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DropFieldEdit extends DbStructureEdit {

    protected String tableName;
    protected JdbcField field;
    protected List<ForeignKeySpec> inFks = new ArrayList<>();
    protected List<ForeignKeySpec> outFks = new ArrayList<>();

    public DropFieldEdit(SqlActionsController aSqlController, JdbcField aField, FieldsEntity tableEntity) {
        super(aSqlController);
        tableName = tableEntity.getTableName();
        field = new JdbcField(aField);
        extractInFks(tableEntity);
        extractOutFks(tableEntity);
    }

    @Override
    protected void doUndoWork() throws Exception {
        createField();
        createConstraints(inFks);
        createConstraints(outFks);
        sqlController.tableChanged(field.getTableName());
    }

    @Override
    protected void doRedoWork() throws Exception {
        dropConstraints(inFks);
        dropConstraints(outFks);
        dropField();
        sqlController.tableChanged(field.getTableName());
    }

    protected void dropField() throws Exception {
        DropFieldAction laction = sqlController.createDropFieldAction(tableName, field);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            ex.setParam2(field.getName());
            throw ex;
        }
    }

    protected void dropConstraints(List<ForeignKeySpec> fks) throws Exception {
        for (ForeignKeySpec fk : fks) {
            DropConstraintAction caction = sqlController.createDropConstraintAction(fk);
            if (!caction.execute()) {
                DbActionException ex = new DbActionException(caction.getErrorString());
                ex.setParam1(fk.getCName());
                throw ex;
            }
        }
    }

    private void createConstraints(List<ForeignKeySpec> fks) throws DbActionException {
        for (ForeignKeySpec fk : fks) {
            CreateConstraintAction laction = sqlController.createCreateConstraintAction(fk);
            if (!laction.execute()) {
                DbActionException ex = new DbActionException(laction.getErrorString());
                ex.setParam1(fk.getCName());
                throw ex;
            }
        }
    }

    private void createField() throws DbActionException {
        AddFieldAction laction = sqlController.createAddFieldAction(tableName, field);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            ex.setParam2(field.getName());
            throw ex;
        }
        if (field.getDescription() != null && !field.getDescription().isEmpty()) {
            DescribeFieldAction daction = sqlController.createDescribeFieldAction(tableName, field.getName(), field.getDescription());
            if (!daction.execute()) {
                DbActionException ex = new DbActionException(daction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(field.getName());
                throw ex;
            }
        }
    }

    private void extractInFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getInRelations();
            for (Relation<FieldsEntity> rel : rels) {
                if (rel.getRightField() == field) {
                    inFks.add(DbStructureUtils.constructFkSpecByRelation(rel));
                }
            }
        }
    }

    private void extractOutFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getOutRelations();
            for (Relation<FieldsEntity> rel : rels) {
                if (rel.getLeftField() == field) {
                    outFks.add(DbStructureUtils.constructFkSpecByRelation(rel));
                }
            }
        }
    }
}

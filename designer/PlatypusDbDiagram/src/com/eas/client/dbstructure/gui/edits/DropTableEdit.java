/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.DbStructureUtils;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DefineTableAction;
import com.eas.client.dbstructure.SqlActionsController.DropConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DropTableAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class DropTableEdit extends DbStructureEdit {

    protected Fields fields;
    protected List<ForeignKeySpec> inFks = new ArrayList<>();
    protected List<ForeignKeySpec> outFks = new ArrayList<>();
    protected String tableName;

    public DropTableEdit(SqlActionsController aSqlController, String aTableName, Fields aFields, FieldsEntity tableEntity) {
        super(aSqlController);
        tableName = aTableName;
        fields = aFields;
        extractInFks(tableEntity);
        extractOutFks(tableEntity);
    }

    @Override
    protected void doUndoWork() throws Exception {
        createTable();
        createConstraints(inFks);
        createConstraints(outFks);
        sqlController.tableAdded(tableName);
    }

    @Override
    protected void doRedoWork() throws Exception {
        dropConstraints(inFks);
        dropConstraints(outFks);
        dropTable();
        sqlController.tableRemoved(tableName);
    }

    protected void dropTable() throws Exception {
        DropTableAction laction = sqlController.createDropTableAction(tableName);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
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

    private static void extractFks(Set<Relation<FieldsEntity>> rels, List<ForeignKeySpec> fks, boolean allowSelfReferences) {
        if (rels != null) {
            for (Relation<FieldsEntity> rel : rels) {
                if (allowSelfReferences || rel.getLeftEntity() != rel.getRightEntity()) {
                    fks.add(DbStructureUtils.constructFkSpecByRelation(rel));
                }
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

    private void createTable() throws DbActionException {
        DefineTableAction laction = sqlController.createDefineTableAction(tableName, fields);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            throw ex;
        }
    }

    private void extractInFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getInRelations();
            List<ForeignKeySpec> fks = inFks;
            extractFks(rels, fks, false);
        }
    }

    private void extractOutFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getOutRelations();
            List<ForeignKeySpec> fks = outFks;
            extractFks(rels, fks, true);
        }
    }
}

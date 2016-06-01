/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

import com.eas.client.SqlCompiledQuery;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.util.IdGenerator;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DbStructureUtils {

    public static ForeignKeySpec constructFkSpecByRelation(Relation<FieldsEntity> aRel) {
        FieldsEntity lEntity = aRel.getLeftEntity();
        FieldsEntity rEntity = aRel.getRightEntity();
        String fkName = aRel.getFkName();
        if (fkName == null) {
            fkName = "FK_" + IdGenerator.genStringId();
        }
        ForeignKeyRule fkUpdateRule = aRel.getFkUpdateRule();
        if (fkUpdateRule == null) {
            fkUpdateRule = ForeignKeyRule.CASCADE;
        }
        ForeignKeyRule fkDeleteRule = aRel.getFkDeleteRule();
        if (fkDeleteRule == null) {
            fkDeleteRule = ForeignKeyRule.CASCADE;
        }
        boolean fkDeferrable = aRel.isFkDeferrable();
        return new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), aRel.getLeftField().getName(), fkName, fkUpdateRule, fkDeleteRule, fkDeferrable, rEntity.getTableSchemaName(), rEntity.getTableName(), aRel.getRightField().getName(), null);
    }

    public static int getRecordsCountByField(FieldsEntity tableEntity, String aFieldName) {
        if (tableEntity != null) {
            try {
                String fullTableName = tableEntity.getTableName();
                String schemaName = tableEntity.getTableSchemaName();
                if (schemaName != null && !schemaName.isEmpty()) {
                    fullTableName = schemaName + "." + fullTableName;
                }
                SqlCompiledQuery query = new SqlCompiledQuery(tableEntity.getModel().getBasesProxy(), tableEntity.getTableDatasourceName(), "select count(*) cnt from " + fullTableName + " where " + aFieldName + " is not null");
                Integer count = query.executeQuery((ResultSet r) -> {
                    if (r.next()) {
                        Object cnt = r.getObject(1);
                        if (cnt instanceof Number) {
                            return ((Number) cnt).intValue();
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }, null, null, null);
                return count != null ? count : 0;
            } catch (Exception ex) {
                Logger.getLogger(DbStructureUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
}

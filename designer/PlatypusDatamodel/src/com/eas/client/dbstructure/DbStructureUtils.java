/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DbStructureUtils {

    protected static java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/client/dbstructure/messages"); // NOI18N

    public static String getString(String key) {
        if (key != null && bundle.containsKey(key)) {
            try {
                return bundle.getString(key);
            } catch (Exception ex) {
                return key;
            }
        } else {
            return key;
        }
    }

    public static String getString(String key, String param1, String param2) {
        if (key != null && bundle.containsKey(key)) {
            try {
                String message = bundle.getString(key);
                if (param1 != null) {
                    message = message.replace("{1}", param1);
                }
                if (param2 != null) {
                    message = message.replace("{2}", param2);
                }
                return message;
            } catch (Exception ex) {
                return key;
            }
        } else {
            return key;
        }
    }

    public static ForeignKeySpec constructFkSpecByRelation(Relation<FieldsEntity> aRel) {
        FieldsEntity lEntity = aRel.getLeftEntity();
        FieldsEntity rEntity = aRel.getRightEntity();
        String fkName = aRel.getFkName();
        if (fkName == null) {
            fkName = "FK_" + String.valueOf(IDGenerator.genID());
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
                Rowset rs = query.executeQuery(null, null);
                if (rs != null) {
                    if (rs.first()) {
                        Object cnt = rs.getObject(1);
                        if (cnt instanceof Number) {
                            return ((Number) cnt).intValue();
                        } else {
                            return 0;
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(DbStructureUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
}

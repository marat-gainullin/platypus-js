/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.ClientConstants;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.Relation;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author mg
 */
public class DeleteRelationRiddleTask implements RiddleTask {

    protected com.eas.client.model.Relation<QueryEntity> toDelete;
    protected Set<Object> deleted = new HashSet<>();
    protected Set<Object> left = new HashSet<>();
    protected Set<Object> right = new HashSet<>();
    protected Map<String, Table> tables = new HashMap<>();

    public DeleteRelationRiddleTask(com.eas.client.model.Relation<QueryEntity> aToDelete, Map<String, Table> aTables) {
        super();
        toDelete = aToDelete;
        tables = aTables;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        if (aExpression instanceof Relation) {
            assert aExpression instanceof BinaryExpression : "Relations expressions must be of BinaryExpression class";
            BinaryExpression rExpression = (BinaryExpression) aExpression;
            if (left.contains(rExpression.getLeftExpression()) && right.contains(rExpression.getRightExpression())
                    || right.contains(rExpression.getLeftExpression()) && left.contains(rExpression.getRightExpression())) {
                return true;
            }
        } else {
            observe(aExpression);
        }
        return false;
    }

    @Override
    public void markAsDeleted(Object aExpression) {
        deleted.add(aExpression);
    }

    @Override
    public boolean markedAsDeleted(Object aExpression) {
        return deleted.contains(aExpression);
    }

    @Override
    public void observe(Object aExpression) {
        QueryEntity lqe = toDelete.getLeftEntity();
        QueryEntity rqe = toDelete.getRightEntity();
        if (aExpression instanceof Column) {
            Column col = (Column) aExpression;
            if (col.getTable() != null && col.getTable().getName() != null) {
                Table table = tables.get(col.getTable().getName().toLowerCase());
                if (table != null) {
                    if (isDerivedFrom(table, lqe) && col.getColumnName().equalsIgnoreCase(toDelete.getLeftField().getName())) {
                        left.add(aExpression);
                    } else if (isDerivedFrom(table, rqe) && col.getColumnName().equalsIgnoreCase(toDelete.getRightField().getName())) {
                        right.add(aExpression);
                    }
                }
            }
        } else if (aExpression instanceof NamedParameter) {
            NamedParameter param = (NamedParameter) aExpression;
            if (lqe instanceof QueryParametersEntity && param.getName().equalsIgnoreCase(toDelete.getLeftField().getName())) {
                left.add(aExpression);
            } else if (rqe instanceof QueryParametersEntity && param.getName().equalsIgnoreCase(toDelete.getRightField().getName())) {
                right.add(aExpression);
            }
        } else if (aExpression instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) aExpression;
            if (left.contains(be.getLeftExpression()) || left.contains(be.getRightExpression())) {
                left.add(be);
            }
            if (right.contains(be.getLeftExpression()) || right.contains(be.getRightExpression())) {
                right.add(be);
            }
        } else if (aExpression instanceof Parenthesis) {
            Parenthesis p = (Parenthesis) aExpression;
            if (left.contains(p.getExpression())) {
                left.add(p);
            }
            if (right.contains(p.getExpression())) {
                right.add(p);
            }
        } else if (aExpression instanceof InverseExpression) {
            InverseExpression i = (InverseExpression) aExpression;
            if (left.contains(i.getExpression())) {
                left.add(i);
            }
            if (right.contains(i.getExpression())) {
                right.add(i);
            }
        }
    }

    public static boolean isDerivedFrom(Table aTable, QueryEntity aQueryEntity) {
        if (aTable != null) {
            String tableName = aTable.getWholeTableName();
            if (aTable.getAlias() != null && !aTable.getAlias().getName().isEmpty()) {
                tableName = aTable.getAlias().getName();
            }

            String entityTableName = null;
            if (aQueryEntity.getQueryName() != null) {
                entityTableName = ClientConstants.STORED_QUERY_REF_PREFIX + aQueryEntity.getQueryName();
            } else {
                entityTableName = aQueryEntity.getTableName();
                if (aQueryEntity.getTableSchemaName() != null && !aQueryEntity.getTableSchemaName().isEmpty()) {
                    entityTableName = aQueryEntity.getTableSchemaName() + "." + entityTableName;
                }
            }
            if (aQueryEntity.getAlias() != null && !aQueryEntity.getAlias().isEmpty()) {
                entityTableName = aQueryEntity.getAlias();
            }
            return tableName.equalsIgnoreCase(entityTableName);
        } else {
            return false;
        }
    }

    public static boolean isDerivedFromIgnoreAlias(Table aTable, QueryEntity aQueryEntity) {
        if (aTable != null) {
            String tableName = aTable.getWholeTableName();

            String entityTableName = null;
            if (aQueryEntity.getQueryName() != null) {
                entityTableName = ClientConstants.STORED_QUERY_REF_PREFIX + aQueryEntity.getQueryName();
            } else {
                entityTableName = aQueryEntity.getTableName();
                if (aQueryEntity.getTableSchemaName() != null && !aQueryEntity.getTableSchemaName().isEmpty()) {
                    entityTableName = aQueryEntity.getTableSchemaName() + "." + entityTableName;
                }
            }
            return tableName.equalsIgnoreCase(entityTableName);
        } else {
            return false;
        }
    }
}

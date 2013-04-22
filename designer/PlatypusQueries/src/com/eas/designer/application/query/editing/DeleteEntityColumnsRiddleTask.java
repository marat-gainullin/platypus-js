/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.model.query.QueryEntity;
import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

/**
 *
 * @author mg
 */
public class DeleteEntityColumnsRiddleTask implements RiddleTask {

    protected QueryEntity entity;
    protected Set<Object> deleted = new HashSet<>();
    protected Map<String, Table> tables = new HashMap<>();

    public DeleteEntityColumnsRiddleTask(QueryEntity aEntity, Map<String, Table> aTables) {
        super();
        entity = aEntity;
        tables = aTables;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        if (aExpression instanceof Column) {
            Column col = (Column) aExpression;
            if (col.getTable().getName() != null) {
                Table table = tables.get(col.getTable().getName().toLowerCase());
                if (table != null) {
                    return DeleteRelationRiddleTask.isDerivedFrom(table, entity);
                }
            }
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
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.model.query.QueryEntity;
import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.Map;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Alias;

/**
 *
 * @author mg
 */
public class ChangeAliasRiddleTask implements RiddleTask {

    protected String oldAlias;
    protected String newAlias;
    protected QueryEntity entity;
    protected Map<String, Table> tables;

    public ChangeAliasRiddleTask(QueryEntity aEntity, String aOldAlias, String aNewAlias, Map<String, Table> aTables) {
        super();
        entity = aEntity;
        oldAlias = aOldAlias;
        newAlias = aNewAlias;
        tables = aTables;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        Table table = null;
        if (aExpression instanceof Table) {
            table = (Table) aExpression;
        } else if (aExpression instanceof Column) {
            table = ((Column) aExpression).getTable();
        }
        if (table != null && table.getName() != null) {
            if (tables.values().contains(table))// From clause
            {
                if (DeleteRelationRiddleTask.isDerivedFrom(table, entity)) {
                    if (table.getAlias() == null){
                        table.setAlias(new Alias());
                        table.getAlias().setName(newAlias);
                    } else {
                        table.getAlias().setName(newAlias);
                    }
                }
            } else { // Select, Where, etc.
                if ((oldAlias == null || oldAlias.isEmpty())
                        && newAlias != null && !newAlias.isEmpty()) { // Alias adding
                    if (tables.containsKey(table.getWholeTableName().toLowerCase())
                            && DeleteRelationRiddleTask.isDerivedFrom(table, entity)) {
                        table.setName(newAlias);
                        table.setSchemaName(null);
                        table.setAlias(null);
                    }
                } else if (oldAlias != null && !oldAlias.isEmpty()
                        && (newAlias == null || newAlias.isEmpty())) { // Alias removing
                    if (table.getName().equalsIgnoreCase(oldAlias)) {
                        if (newAlias == null || newAlias.isEmpty()) {
                            table.setName(tables.get(oldAlias.toLowerCase()).getName());
                            table.setSchemaName(tables.get(oldAlias.toLowerCase()).getSchemaName());
                            table.setAlias(null);
                        }
                    }
                } else { // Alias changing
                    if(table.getName() != null && table.getName().equalsIgnoreCase(oldAlias))
                        table.setName(newAlias);
                }
            }
        }
        return false;
    }

    @Override
    public void markAsDeleted(Object aExpression) {
    }

    @Override
    public boolean markedAsDeleted(Object aExpression) {
        return false;
    }

    @Override
    public void observe(Object aExpression) {
    }
}

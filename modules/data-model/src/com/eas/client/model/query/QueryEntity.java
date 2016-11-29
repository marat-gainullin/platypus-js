/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.SQLUtils;
import com.eas.client.SqlQuery;
import com.eas.client.model.Entity;
import com.eas.client.model.visitors.ModelVisitor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class QueryEntity extends Entity<QueryModel, SqlQuery, QueryEntity> {

    public static final String ALIAS_PROPERTY = "alias";
    protected String alias;

    public QueryEntity() {
        super();
    }

    public QueryEntity(QueryModel aModel) {
        super(aModel);
    }

    public QueryEntity(String aQueryName) {
        super(aQueryName);
    }

    @Override
    public void accept(ModelVisitor<QueryEntity, QueryModel> visitor) {
        visitor.visit(this);
    }

    @Override
    public String getTableDatasourceName() {
        if (model != null) {
            return model.getDatasourceName();
        } else {
            return null;
        }
    }

    @Override
    public void setTableDatasourceName(String tableDbId) {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String aValue) {
        String oldValue = alias;
        alias = aValue;
        changeSupport.firePropertyChange(ALIAS_PROPERTY, oldValue, aValue);
    }

    public String getFullTableName() {
        String fullTableName = tableName;
        if (getTableSchemaName() != null && !getTableSchemaName().isEmpty()) {
            fullTableName = getTableSchemaName() + "." + fullTableName;
        }
        return fullTableName;
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            if (queryName != null) {
                SqlQuery q = model.queries.getCachedQuery(queryName);
                if (q != null) {
                    query = q.copy();
                }
            } else if (tableName != null) {
                try {
                    query = SQLUtils.validateTableSqlQuery(getTableDatasourceName(), getTableName(), getTableSchemaName(), model.getBasesProxy());
                } catch (Exception ex) {
                    query = null;
                    Logger.getLogger(QueryEntity.class.getName()).log(Level.WARNING, ex.toString());
                }
            } else {
                assert false : "Entity must have queryName or tableName to validate it's query";
            }
        }
    }
}

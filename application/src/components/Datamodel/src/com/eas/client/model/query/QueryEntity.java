/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.SQLUtils;
import com.eas.client.model.Entity;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.queries.SqlQuery;

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

    public QueryEntity(String aSqlId) {
        super(aSqlId);
    }

    @Override
    public String getTableDbId() {
        if (model != null) {
            return model.getDbId();
        }
        return null;
    }

    @Override
    public void setTableDbId(String tableDbId) {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String aValue) {
        String oldValue = alias;
        alias = aValue;
        changeSupport.firePropertyChange(ALIAS_PROPERTY, oldValue, aValue);
    }

    @Override
    public void accept(ModelVisitor<QueryEntity> visitor) {
        if (visitor instanceof QueryModelVisitor) {
            ((QueryModelVisitor) visitor).visit(this);
        }
    }

    @Override
    public void validateQuery() throws Exception {
        if (query == null) {
            try {
                if (queryId != null) {
                    query = model.getClient().getAppQuery(queryId);
                } else if (tableName != null) {
                    query = SQLUtils.validateTableSqlQuery(getTableDbId(), getTableName(), getTableSchemaName(), model.getClient());
                } else {
                    assert false : "Query entity needs table name or a sub query name";
                }
            } catch (Exception ex) {
                query = null;
            }
        }
    }
}

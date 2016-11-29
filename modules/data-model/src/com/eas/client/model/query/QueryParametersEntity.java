/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.query;

import com.eas.client.SqlQuery;
import com.eas.client.metadata.Fields;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.model.visitors.QueryModelVisitor;

/**
 *
 * @author mg
 */
public class QueryParametersEntity extends QueryEntity {

    public QueryParametersEntity() {
        super();
        entityId = QueryModel.PARAMETERS_ENTITY_ID;
    }

    @Override
    public void accept(ModelVisitor<QueryEntity, QueryModel> visitor) {
        ((QueryModelVisitor)visitor).visit(this);
    }

    @Override
    public Fields getFields() {
        return model.getParameters();
    }

    @Override
    public void validateQuery() throws Exception {
        // no op for parameters entity
    }

    @Override
    public boolean validate() throws Exception {
        // no op for parameters entity
        return false;
    }

    @Override
    protected boolean isTagValid(String aTagName) {
        return true;
    }

    @Override
    public QueryParametersEntity copy() throws Exception {
        QueryParametersEntity copied = new QueryParametersEntity();
        assign(copied);
        return copied;
    }

    @Override
    public String getQueryName() {
        return null;
    }

    @Override
    public SqlQuery getQuery() {
        return null;
    }

    @Override
    public String getTableDatasourceName() {
        return null;
    }

    @Override
    public void setTableDatasourceName(String tableDbId) {
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public void setTableName(String aTableName) {
    }

    @Override
    public Long getEntityId() {
        return QueryModel.PARAMETERS_ENTITY_ID;
    }
}

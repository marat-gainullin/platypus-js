/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model.query;

import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.visitors.ModelVisitor;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.queries.SqlQuery;

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
    public Fields getFields() {
        return model.getParameters();
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
    public void accept(ModelVisitor<QueryEntity> visitor) {
        if (visitor instanceof QueryModelVisitor) {
            ((QueryModelVisitor) visitor).visit(this);
        }
    }

    @Override
    public String getQueryId() {
        return null;
    }

    @Override
    public SqlQuery getQuery() {
        return null;
    }

    @Override
    public String getTableDbId() {
        return null;
    }

    @Override
    public void setTableDbId(String tableDbId) {
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
        return ApplicationModel.PARAMETERS_ENTITY_ID;
    }
}

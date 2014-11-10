/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;

/**
 *
 * @author mg
 */
public interface QueryModelVisitor extends ModelVisitor<QueryEntity, QueryModel> {

    @Override
    public void visit(QueryModel aModel);

    public void visit(QueryParametersEntity aEntity);
}

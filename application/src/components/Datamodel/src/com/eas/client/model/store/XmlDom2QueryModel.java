/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.model.Relation;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.visitors.QueryModelVisitor;
import com.eas.client.queries.QueriesProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class XmlDom2QueryModel extends XmlDom2Model<QueryEntity> implements QueryModelVisitor{
    
    public XmlDom2QueryModel(Document aDoc) {
        super();
        doc = aDoc;
    }
    
    public XmlDom2QueryModel(Element aModelElement) {
        super();
        modelElement = aModelElement;
    }

    public static QueryModel transform(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries, Document aDoc) throws Exception {
        QueryModel model = new QueryModel(aBasesProxy, aQueries);
        model.accept(new XmlDom2QueryModel(aDoc));
        return model;
    }

    public static QueryModel transform(DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueries, Element aModelElement) throws Exception {
        QueryModel model = new QueryModel(aBasesProxy, aQueries);
        model.accept(new XmlDom2QueryModel(aModelElement));
        return model;
    }
    
    @Override
    public void visit(QueryModel aModel) {
        readModel(aModel);
        if (currentNode.hasAttribute(Model2XmlDom.DATAMODEL_DB_ID)) {
            String dbIdAttr = currentNode.getAttribute(Model2XmlDom.DATAMODEL_DB_ID);
            if (dbIdAttr != null && !"null".equals(dbIdAttr)) {
                aModel.setDbId(dbIdAttr);
            }
        }
    }

    @Override
    public void visit(QueryParametersEntity entity) {
        readEntityDesignAttributes(entity);
        readOldUserData(entity);
    }

    @Override
    public void visit(QueryEntity entity) {
        entity.setAlias(currentNode.getAttribute(Model2XmlDom.ENTITY_TABLE_ALIAS));
        entity.setTitle(currentNode.getAttribute(QueryModel.DATASOURCE_TITLE_TAG_NAME));
        readEntity(entity);
    }

    @Override
    public void visit(Relation<QueryEntity> relation) {
        super.visit(relation);
        if (currentModel != null) {
            currentModel.addRelation(relation);
        }
    }
}

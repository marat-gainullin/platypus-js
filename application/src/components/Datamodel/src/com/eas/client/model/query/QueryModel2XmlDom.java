/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.query;

import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.model.visitors.QueryModelVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class QueryModel2XmlDom extends Model2XmlDom<QueryEntity> implements QueryModelVisitor {

    static Document transform(QueryModel aModel) {
        QueryModel2XmlDom transformer = new QueryModel2XmlDom();
        return transformer.model2XmlDom(aModel);
    }

    @Override
    public void visit(QueryModel aModel) {
        writeModel(aModel);
        if (aModel.getDbId() != null && currentNode != null && currentNode instanceof Element) {
            Element el = (Element) currentNode;
            el.setAttribute(DATAMODEL_DB_ID, String.valueOf(aModel.getDbId()));
        }
    }

    @Override
    public void visit(QueryEntity entity) {
        if (entity != null) {
            Element node = doc.createElement(ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(QueryModel.DATASOURCE_TITLE_TAG_NAME, entity.getTitle());
            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(entity.getEntityID()));
            if (entity.getQueryId() != null) {
                node.setAttribute(QUERY_ID_ATTR_NAME, String.valueOf(entity.getQueryId()));
            }
            node.setAttribute(TABLE_SCHEMA_NAME_ATTR_NAME, entity.getTableSchemaName());
            node.setAttribute(TABLE_NAME_ATTR_NAME, entity.getTableName());
            node.setAttribute(ENTITY_TABLE_ALIAS, entity.getAlias());
            writeEntityDesignAttributes(node, entity);
        }
    }

    @Override
    public void visit(QueryParametersEntity entity) {
        Element node = doc.createElement(PARAMETERS_ENTITY_TAG_NAME);
        currentNode.appendChild(node);
        writeEntityDesignAttributes(node, entity);
    }
}

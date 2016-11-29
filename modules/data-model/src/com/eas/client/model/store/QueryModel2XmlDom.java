/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.metadata.Parameters;
import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.client.model.visitors.QueryModelVisitor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 */
public class QueryModel2XmlDom extends Model2XmlDom<QueryEntity, QueryModel> implements QueryModelVisitor {

    public static Document transform(QueryModel aModel) {
        QueryModel2XmlDom transformer = new QueryModel2XmlDom();
        return transformer.model2XmlDom(aModel);
    }

    @Override
    protected void writeEntities(QueryModel aModel) throws DOMException {
        Parameters parameters = aModel.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            Element paramsNode = doc.createElement(PARAMETERS_TAG_NAME);
            currentNode.appendChild(paramsNode);
            Node lCurrentNode = currentNode;
            try {
                currentNode = paramsNode;
                for (int i = 0; i < parameters.getParametersCount(); i++) {
                    visit(parameters.get(i + 1));
                }
            } finally {
                currentNode = lCurrentNode;
            }
        }
        // Special processing of parameters entity in order to save events and design information.
        if (aModel.getParametersEntity() != null) {
            aModel.getParametersEntity().accept(this);
        }
        super.writeEntities(aModel);
    }

    @Override
    public void visit(QueryModel aModel) {
        writeModel(aModel);
        if (aModel.getDatasourceName() != null && currentNode != null && currentNode instanceof Element) {
            Element el = (Element) currentNode;
            el.setAttribute(Model2XmlDom.DATAMODEL_DATASOURCE, String.valueOf(aModel.getDatasourceName()));
        }
    }

    @Override
    public void visit(QueryEntity entity) {
        if (entity != null) {
            Element node = doc.createElement(ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(DATASOURCE_TITLE_ATTR_NAME, entity.getTitle());
            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(entity.getEntityId()));
            if (entity.getQueryName() != null) {
                node.setAttribute(QUERY_ID_ATTR_NAME, String.valueOf(entity.getQueryName()));
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

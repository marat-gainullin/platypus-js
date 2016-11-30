/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel; 
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public class ApplicationModel2XmlDom<E extends ApplicationEntity<M, ?, E>, M extends ApplicationModel<E, ?>> extends Model2XmlDom<E, M> implements ApplicationModelVisitor<E, M> {

    protected static final String REFERENCE_RELATION_TAG_NAME = "referenceRelation";
    protected static final String SCALAR_PROP_NAME_ATTR_NAME = "scalarPropertyName";
    protected static final String COLLECTION_PROP_NAME_ATTR_NAME = "collectionPropertyName";

    public static <E extends ApplicationEntity<M, ?, E>, M extends ApplicationModel<E, ?>> Document transform(M aModel) {
        ApplicationModel2XmlDom<E, M> transformer = new ApplicationModel2XmlDom<>();
        return transformer.model2XmlDom(aModel);
    }

    @Override
    public void visit(M aModel) {
        writeModel(aModel);
    }

    @Override
    public void writeModel(M aModel) {
        super.writeModel(aModel);
        if (aModel != null && aModel.getReferenceRelations() != null) {
            aModel.getReferenceRelations().stream().forEach((ReferenceRelation relation) -> {
                relation.accept(this);
            });
        }
    }

    @Override
    public void visit(E aEntity) {
        writeApplicationEntity(aEntity);
    }

    protected void writeApplicationEntity(E aEntity) {
        if (aEntity != null) {
            Element node = doc.createElement(ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(DATASOURCE_NAMEE_ATTR_NAME, aEntity.getName());
            node.setAttribute(DATASOURCE_TITLE_ATTR_NAME, aEntity.getTitle());
            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(aEntity.getEntityId()));
            if (aEntity.getQueryName() != null) {
                node.setAttribute(QUERY_ID_ATTR_NAME, String.valueOf(aEntity.getQueryName()));
            }
            if (aEntity.getTableDatasourceName() != null) {
                node.setAttribute(TABLE_DB_ID_ATTR_NAME, String.valueOf(aEntity.getTableDatasourceName()));
            }
            node.setAttribute(TABLE_SCHEMA_NAME_ATTR_NAME, aEntity.getTableSchemaName());
            node.setAttribute(TABLE_NAME_ATTR_NAME, aEntity.getTableName());
            writeEntityDesignAttributes(node, aEntity);
        }
    }

    @Override
    public void visit(ReferenceRelation<E> aRelation) {
        Element el = doc.createElement(REFERENCE_RELATION_TAG_NAME);
        writeRelation(aRelation, el);
        if (aRelation.getScalarPropertyName() != null && !aRelation.getScalarPropertyName().isEmpty()) {
            el.setAttribute(SCALAR_PROP_NAME_ATTR_NAME, aRelation.getScalarPropertyName());
        }
        if (aRelation.getCollectionPropertyName() != null && !aRelation.getCollectionPropertyName().isEmpty()) {
            el.setAttribute(COLLECTION_PROP_NAME_ATTR_NAME, aRelation.getCollectionPropertyName());
        }
    }
}

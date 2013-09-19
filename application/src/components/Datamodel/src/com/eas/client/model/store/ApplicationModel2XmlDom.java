/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.model.Model;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.script.StoredFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class ApplicationModel2XmlDom<E extends ApplicationEntity<?, ?, E>> extends Model2XmlDom<E> implements ApplicationModelVisitor<E> {

    protected static final String REFERENCE_RELATION_TAG_NAME = "referenceRelation";
    protected static final String SCALAR_PROP_NAME_ATTR_NAME = "scalarPropertyName";
    protected static final String COLLECTION_PROP_NAME_ATTR_NAME = "collectionPropertyName";

    public static <E extends ApplicationEntity<?, ?, E>> Document transform(ApplicationModel<E, ?, ?, ?> aModel) {
        ApplicationModel2XmlDom<E> transformer = new ApplicationModel2XmlDom<>();
        return transformer.model2XmlDom(aModel);
    }

    @Override
    public void visit(ApplicationModel<E, ?, ?, ?> aModel) {
        writeModel(aModel);
    }

    @Override
    public void writeModel(Model<E, ?, ?, ?> aModel) {
        super.writeModel(aModel);
        if (aModel != null && ((ApplicationModel<E, ?, ?, ?>) aModel).getReferenceRelations() != null) {
            for (ReferenceRelation<E> relation : ((ApplicationModel<E, ?, ?, ?>) aModel).getReferenceRelations()) {
                relation.accept(this);
            }
        }
    }

    @Override
    public void visit(ApplicationParametersEntity entity) {
        Element node = doc.createElement(PARAMETERS_ENTITY_TAG_NAME);
        currentNode.appendChild(node);
        // Hack. Assume that ApplicationParametersEntity interface is supported only by 
        // ApplicationEntity descendants.
        E appEntity = (E) entity;
        writeEntityEventsAttributes(node, appEntity);
        writeEntityDesignAttributes(node, appEntity);
    }

    @Override
    public void visit(E aEntity) {
        writeApplicationEntity(aEntity);
    }

    protected void writeApplicationEntity(E aEntity) {
        if (aEntity != null) {
            Element node = doc.createElement(ENTITY_TAG_NAME);
            currentNode.appendChild(node);

            node.setAttribute(Model.DATASOURCE_NAME_TAG_NAME, aEntity.getName());
            node.setAttribute(Model.DATASOURCE_TITLE_TAG_NAME, aEntity.getTitle());
            node.setAttribute(ENTITY_ID_ATTR_NAME, String.valueOf(aEntity.getEntityId()));
            if (aEntity.getQueryId() != null) {
                node.setAttribute(QUERY_ID_ATTR_NAME, String.valueOf(aEntity.getQueryId()));
            }
            if (aEntity.getTableDbId() != null) {
                node.setAttribute(TABLE_DB_ID_ATTR_NAME, String.valueOf(aEntity.getTableDbId()));
            }
            node.setAttribute(TABLE_SCHEMA_NAME_ATTR_NAME, aEntity.getTableSchemaName());
            node.setAttribute(TABLE_NAME_ATTR_NAME, aEntity.getTableName());
            writeEntityEventsAttributes(node, aEntity);
            writeEntityDesignAttributes(node, aEntity);
        }
    }

    protected void writeEntityEventsAttributes(Element node, E entity) {
        // It's considered, that all design code uses StoredFunction.
        // Since it is true, unconditional casting is correct.
        StoredFunction eventHandler = (StoredFunction) entity.getOnAfterChange();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnAfterDelete();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnAfterInsert();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnAfterScroll();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnFiltered();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnRequeried();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnBeforeChange();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnBeforeDelete();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnBeforeInsert();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME, eventHandler.getName());
        }
        eventHandler = (StoredFunction) entity.getOnBeforeScroll();
        if (eventHandler != null && eventHandler.getName() != null && !eventHandler.getName().isEmpty()) {
            node.setAttribute(Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME, eventHandler.getName());
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

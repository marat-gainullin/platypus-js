/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.model.Model;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.script.StoredFunction;
import com.eas.xml.dom.XmlDomUtils;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class XmlDom2ApplicationModel<E extends ApplicationEntity<?, ?, E>> extends XmlDom2Model<E> implements ApplicationModelVisitor<E> {

    public XmlDom2ApplicationModel(Document aDoc) {
        super();
        doc = aDoc;
    }

    @Override
    public void visit(ApplicationModel<E, ?, ?, ?> aModel) {
        readModel(aModel);
    }

    protected void readEntityEventsAttributes(Element node, final E entity) {
        final String afterChangeHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME);
        entity.setOnAfterChange(afterChangeHandlerName != null && !afterChangeHandlerName.isEmpty() ? new StoredFunction(afterChangeHandlerName) : null);
        final String afterDeleteHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME);
        entity.setOnAfterDelete(afterDeleteHandlerName != null && !afterDeleteHandlerName.isEmpty() ? new StoredFunction(afterDeleteHandlerName) : null);
        final String afterInsertHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME);
        entity.setOnAfterInsert(afterInsertHandlerName != null && !afterInsertHandlerName.isEmpty() ? new StoredFunction(afterInsertHandlerName) : null);
        final String afterScrollHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME);
        entity.setOnAfterScroll(afterScrollHandlerName != null && !afterScrollHandlerName.isEmpty() ? new StoredFunction(afterScrollHandlerName) : null);
        final String afterFilterHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME);
        entity.setOnFiltered(afterFilterHandlerName != null && !afterFilterHandlerName.isEmpty() ? new StoredFunction(afterFilterHandlerName) : null);
        final String afterRequeryHandlerName = node.getAttribute(Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME);
        entity.setOnRequeried(afterRequeryHandlerName != null && !afterRequeryHandlerName.isEmpty() ? new StoredFunction(afterRequeryHandlerName) : null);
        final String beforechangeHandlerName = node.getAttribute(Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME);
        entity.setOnBeforeChange(beforechangeHandlerName != null && !beforechangeHandlerName.isEmpty() ? new StoredFunction(beforechangeHandlerName) : null);
        final String beforeDeleteHandlerName = node.getAttribute(Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME);
        entity.setOnBeforeDelete(beforeDeleteHandlerName != null && !beforeDeleteHandlerName.isEmpty() ? new StoredFunction(beforeDeleteHandlerName) : null);
        final String beforeInsertHandlerName = node.getAttribute(Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME);
        entity.setOnBeforeInsert(beforeInsertHandlerName != null && !beforeInsertHandlerName.isEmpty() ? new StoredFunction(beforeInsertHandlerName) : null);
        final String beforeScrollHandlerName = node.getAttribute(Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME);
        entity.setOnBeforeScroll(beforeScrollHandlerName != null && !beforeScrollHandlerName.isEmpty() ? new StoredFunction(beforeScrollHandlerName) : null);
        //entity.setOnBeforeScroll(null); for migration from 1.0 to 2.0 purposes only!
    }

    @Override
    public void visit(E aEntity) {
        readApplicationEntity(aEntity);
    }

    protected void readApplicationEntity(E aEntity) {
        aEntity.setName(currentNode.getAttribute(Model.DATASOURCE_NAME_TAG_NAME));
        aEntity.setTitle(currentNode.getAttribute(Model.DATASOURCE_TITLE_TAG_NAME));
        readEntityEventsAttributes(currentNode, aEntity);
        readEntity(aEntity);
    }

    @Override
    protected void readRelations() {
        super.readRelations();
        List<Element> nl = XmlDomUtils.elementsByTagName(currentNode, ApplicationModel2XmlDom.REFERENCE_RELATION_TAG_NAME);
        if (nl != null) {
            Element lcurrentNode = currentNode;
            try {
                for (int i = 0; i < nl.size(); i++) {
                    currentNode = nl.get(i);
                    ReferenceRelation<E> relation = new ReferenceRelation<>();
                    relation.accept(this);
                }
            } finally {
                currentNode = lcurrentNode;
            }
        }
    }
    
    @Override
    public void visit(Relation<E> relation) {
        super.visit(relation);
        if (currentModel != null) {
            currentModel.addRelation(relation);
        }
    }

    @Override
    public void visit(ReferenceRelation<E> aRelation) {
        super.visit(aRelation);
        final String scalarPropertyName = currentNode.getAttribute(ApplicationModel2XmlDom.SCALAR_PROP_NAME_ATTR_NAME);
        final String collectionPropertyName = currentNode.getAttribute(ApplicationModel2XmlDom.COLLECTION_PROP_NAME_ATTR_NAME);
        aRelation.setScalarPropertyName(scalarPropertyName);
        aRelation.setCollectionPropertyName(collectionPropertyName);
        if (currentModel != null) {
            ((ApplicationModel<E, ?, ?, ?>)currentModel).getReferenceRelations().add(aRelation);
        }
    }

    @Override
    public void visit(ApplicationParametersEntity entity) {
        // Hack. Assume that ApplicationParametersEntity interface is supported only by 
        // ApplicationEntity descendants.
        E appEntity = (E) entity;
        readEntityDesignAttributes(appEntity);
        readEntityEventsAttributes(currentNode, appEntity);
        readOldUserData(appEntity);
    }
}

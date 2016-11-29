/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.model.Relation;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.visitors.ApplicationModelVisitor;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.Query;
import com.eas.script.Scripts;
import com.eas.xml.dom.XmlDomUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 * @param <E>
 * @param <M>
 */
public class XmlDom2ApplicationModel<E extends ApplicationEntity<M, ?, E>, M extends ApplicationModel<E, ?>> extends XmlDom2Model<E, M> implements ApplicationModelVisitor<E, M> {

    public XmlDom2ApplicationModel(Document aDoc) {
        super();
        doc = aDoc;
    }

    public XmlDom2ApplicationModel(Element aTag) {
        super();
        modelElement = aTag;
    }

    @Override
    public void visit(M aModel) {
        Runnable resolver = readModel(aModel);
        QueriesProxy<?> queries = aModel.getQueries();
        aModel.getEntities().values().stream().forEach((entity) -> {
            try {
                Query q = queries.getCachedQuery(entity.getQueryName());
                if (q == null) {
                    queries.getQuery(entity.getQueryName(), Scripts.getSpace(), null, null);
                }
                entity.validateQuery();
            } catch (Exception ex) {
                Logger.getLogger(XmlDom2ApplicationModel.class.getName()).log(Level.WARNING, null, ex);
            }
        });
        resolver.run();
    }

    @Override
    public void visit(E aEntity) {
        readApplicationEntity(aEntity);
    }

    protected void readApplicationEntity(E aEntity) {
        aEntity.setName(XmlDomUtils.getAttribute(currentNode, "n", Model2XmlDom.DATASOURCE_NAMEE_ATTR_NAME));
        aEntity.setTitle(XmlDomUtils.getAttribute(currentNode, "tt", Model2XmlDom.DATASOURCE_TITLE_ATTR_NAME));
        readEntity(aEntity);
    }

    @Override
    protected void readRelations() {
        super.readRelations();
        List<Element> nl = XmlDomUtils.elementsByTagName(currentNode, "rr", ApplicationModel2XmlDom.REFERENCE_RELATION_TAG_NAME);
        if (nl != null) {
            Element lcurrentNode = currentNode;
            try {
                nl.stream().forEach((Element nl1) -> {
                    currentNode = nl1;
                    ReferenceRelation<E> relation = new ReferenceRelation<>();
                    relation.accept(this);
                });
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
        final String scalarPropertyName = XmlDomUtils.getAttribute(currentNode, "spn", ApplicationModel2XmlDom.SCALAR_PROP_NAME_ATTR_NAME);
        final String collectionPropertyName = XmlDomUtils.getAttribute(currentNode, "cpn", ApplicationModel2XmlDom.COLLECTION_PROP_NAME_ATTR_NAME);
        aRelation.setScalarPropertyName(scalarPropertyName != null ? scalarPropertyName.trim() : null);
        aRelation.setCollectionPropertyName(collectionPropertyName != null ? collectionPropertyName.trim() : null);
        if (currentModel != null) {
            ((ApplicationModel<E, ?>) currentModel).getReferenceRelations().add(aRelation);
        }
    }

}

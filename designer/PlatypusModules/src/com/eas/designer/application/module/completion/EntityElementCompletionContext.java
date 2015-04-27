/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.Relation;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 * A completion context for an data model entity row.
 *
 * @author vv
 */
public class EntityElementCompletionContext extends CompletionContext {

    ApplicationDbEntity entity;
    protected Map<String, ApplicationDbEntity> ormScalars = new TreeMap<>();
    protected Set<String> ormCollections = new TreeSet<>();

    public EntityElementCompletionContext(ApplicationDbEntity anEntity) {
        super(null);
        entity = anEntity;
        entity.getModel().getReferenceRelationsByEntity(entity).forEach((Relation<ApplicationDbEntity> aRelation) -> {
            if (aRelation instanceof ReferenceRelation<?>) {
                ReferenceRelation<ApplicationDbEntity> rr = (ReferenceRelation<ApplicationDbEntity>) aRelation;
                if (rr.getLeftEntity() == entity) {
                    if (rr.getScalarPropertyName() != null && !rr.getScalarPropertyName().isEmpty()) {
                        ormScalars.put(rr.getScalarPropertyName(), rr.getRightEntity());
                    }
                }
                if (rr.getRightEntity() == entity) {
                    if (rr.getCollectionPropertyName() != null && !rr.getCollectionPropertyName().isEmpty()) {
                        ormCollections.add(rr.getCollectionPropertyName());
                    }
                }
            }
        });
    }

    @Override
    public CompletionContext getChildContext(CompletionPoint.CompletionToken token, int offset) throws Exception {
        ApplicationDbEntity scalarEntity = ormScalars.get(token.name);
        if (scalarEntity != null) {
            return new EntityElementCompletionContext(scalarEntity);
        } else {
            return super.getChildContext(token, offset);
        }
    }

    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        ormScalars.entrySet().forEach((Map.Entry<String, ApplicationDbEntity> aEntry) -> {
            addItem(resultSet, point.getFilter(), new BeanCompletionItem(Object.class, aEntry.getKey(), aEntry.getValue().getName(), point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        });
        ormCollections.forEach((String aCollectionPropName)->{
            addItem(resultSet, point.getFilter(), new BeanCompletionItem(Object.class, aCollectionPropName, null, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
        });
        fillFieldsValues(entity.getFields(), point, resultSet);
    }

    protected static void fillFieldsValues(Fields aFields, CompletionPoint point, CompletionResultSet resultSet) {
        if (aFields != null) {
            for (Field field : aFields.toCollection()) {
                addItem(resultSet, point.getFilter(), new FieldCompletionItem(field, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
        } else {
            throw new IllegalStateException("ORM properties are unavailable");
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.CollectionListener;
import com.eas.client.model.Entity;
import com.eas.client.queries.Query;
import com.eas.designer.datamodel.nodes.EntityNodeChildren.EntityFieldKey;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public abstract class EntityNodeChildren<T> extends Children.Keys<T> implements PropertyChangeListener {

    protected Entity entity;
    protected List<T> keys = new ArrayList<>();
    private Fields tempFields;
    protected CollectionListener<Fields, Field> fieldsListener = new FieldsToNodesNotifier();
    protected Lookup lookup;
    protected UndoRedo.Manager undoRedo;

    public EntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super();
        entity = anEnity;
        lookup = aLookup;
        undoRedo = aUndoReciever;
        setFields(entity.getFields());
    }

    protected abstract T createKey(Field aField);

    protected List<T> computeKeys() {
        keys.clear();
        Query query = entity.getQuery();
        if (query != null) {
            for (int i = 1; i <= query.getParameters().getParametersCount(); i++) {
                keys.add(createKey(query.getParameters().get(i)));
            }
            Fields fields = entity.getFields();
            if (fields != null) {
                for (int i = 1; i <= entity.getFields().getFieldsCount(); i++) {
                    keys.add(createKey(entity.getFields().get(i)));
                }
            }
        }
        return keys;
    }

    @Override
    protected void addNotify() {
        entity.getChangeSupport().addPropertyChangeListener(Entity.QUERY_VALID_PROPERTY, this);
        setKeys(computeKeys());
    }

    @Override
    protected void removeNotify() {
        entity.getChangeSupport().removePropertyChangeListener(Entity.QUERY_VALID_PROPERTY, this);
        entity.getModel().getChangeSupport().removePropertyChangeListener(this);
        setFields(null);
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Entity.QUERY_VALID_PROPERTY.equalsIgnoreCase(evt.getPropertyName())) {
            setFields(entity.getQuery() != null ? entity.getFields() : null);
            setKeys(computeKeys());
        }
    }

    private void setFields(Fields aFields) {
        if (tempFields != null) {
            tempFields.getCollectionSupport().removeListener(fieldsListener);
        }
        tempFields = aFields;
        if (tempFields != null) {
            tempFields.getCollectionSupport().addListener(fieldsListener);
        }
    }

    protected class FieldsToNodesNotifier implements CollectionListener<Fields, Field> {

        @Override
        public void added(Fields c, Field v) {
            keys.add(createKey(v));
            setKeys(keys);
        }

        @Override
        public void added(Fields c, Collection<Field> clctn) {
            setKeys(computeKeys());
        }

        @Override
        public void removed(Fields c, Field v) {
            keys.remove(createKey(v));
            setKeys(keys);
        }

        @Override
        public void removed(Fields c, Collection<Field> clctn) {
            setKeys(computeKeys());
        }

        @Override
        public void cleared(Fields c) {
            setKeys(computeKeys());
        }

        @Override
        public void reodered(Fields c) {
            setKeys(computeKeys());
        }
    }

    public static class EntityFieldKey {

        public final Field field;

        public EntityFieldKey(Field aField) {
            field = aField;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(field);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EntityFieldKey other = (EntityFieldKey) obj;
            return other.field == field;
        }
    }
}

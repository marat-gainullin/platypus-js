/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.view;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.util.CollectionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public abstract class FieldsListModel<F extends Field> implements ListModel<F> {

    public static class FieldsModel extends FieldsListModel<Field> {

        @Override
        public Field getElementAt(int index) {
            if (index >= 0 && index < getSize()) {
                return fields.get(index + 1);
            } else {
                return null;
            }
        }
    }

    public static class ParametersModel extends FieldsListModel<Parameter> {

        @Override
        public Parameter getElementAt(int index) {
            if (index >= 0 && index < getSize()) {
                return (Parameter) fields.get(index + 1);
            } else {
                return null;
            }
        }
    }
    protected Fields fields;
    protected FieldsChangesListener fieldChangeReflector = new FieldsChangesListener();
    protected Set<ListDataListener> listeners = new HashSet<>();

    protected class FieldsChangesListener implements CollectionListener<Fields, Field> {

        @Override
        public void added(Fields aCollection, Field added) {
            fireAdded(aCollection.find(added.getName()) - 1);
        }

        @Override
        public void added(Fields aCollection, Collection<Field> added) {
            fireDataChanged();
        }
        
        @Override
        public void reodered(Fields c) {
            fireDataChanged();
        }

        @Override
        public void removed(Fields aCollection, Field removed) {
            fireDataChanged();
        }

        @Override
        public void removed(Fields aCollection, Collection<Field> added) {
            fireDataChanged();
        }

        @Override
        public void cleared(Fields aCollection) {
            fireDataChanged();
        }
    }

    public FieldsListModel() {
        super();
    }

    public FieldsListModel(Fields aFields) {
        this();
        setFields(aFields);
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields aFields) {
        if (fields != null) {
            fields.getCollectionSupport().removeListener(fieldChangeReflector);
        }
        fields = aFields;
        if (fields != null) {
            fields.getCollectionSupport().addListener(fieldChangeReflector);
        }
        fireDataChanged();
    }

    @Override
    public int getSize() {
        if (fields != null) {
            return fields.getFieldsCount();
        } else {
            return 0;
        }
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void fireAdded(int aIndex) {
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, aIndex, aIndex);
        for (ListDataListener ll : listeners) {
            ll.contentsChanged(event);
        }
    }

    public void fireRemoved(int aIndex) {
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, aIndex, aIndex);
        for (ListDataListener ll : listeners) {
            ll.contentsChanged(event);
        }
    }

    public void fireDataChanged() {
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
        for (ListDataListener ll : listeners) {
            ll.contentsChanged(event);
        }
    }

    public int getFieldNameIndex(String aFieldName) {
        if (fields != null) {
            return fields.find(aFieldName) - 1;
        } else {
            return -1;
        }
    }
}

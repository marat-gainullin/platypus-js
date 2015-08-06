/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.FieldsEntity;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author vv
 */
public class TableIndexesChildren extends Children.Keys<DbTableIndexSpec> {

    FieldsEntity entity;
    Lookup lookup;
    private final PropertyChangeListener entityListener;

    public TableIndexesChildren(FieldsEntity anEntity, Lookup aLookup) {
        super();
        entity = anEntity;
        entity.achiveIndexes();
        lookup = aLookup;
        entityListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setKeys(getKeys());
            }

        };
        entity.getChangeSupport().addPropertyChangeListener(FieldsEntity.INDEXES_PROPERTY, entityListener);
    }

    @Override
    protected void addNotify() {
        setKeys(getKeys());
    }

    @Override
    protected void removeNotify() {
        entity.getChangeSupport().removePropertyChangeListener(FieldsEntity.INDEXES_PROPERTY, entityListener);
        setKeys(Collections.EMPTY_LIST);
        super.removeNotify();
    }

    protected List<DbTableIndexSpec> getKeys() {
        List<DbTableIndexSpec> sortedKeys = new ArrayList(entity.getIndexes());
        Collections.sort(sortedKeys, (DbTableIndexSpec o1, DbTableIndexSpec o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        return sortedKeys;
    }

    public void update() {
        setKeys(getKeys());
    }

    @Override
    protected Node[] createNodes(DbTableIndexSpec key) {
        try {
            ColumnsOrderSupport cso = new ColumnsOrderSupport();
            TableIndexNode node = new TableIndexNode(key, entity, new ProxyLookup(lookup, Lookups.fixed(cso)));
            cso.setTableIndexNode(node);
            return new Node[]{node};
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }
}

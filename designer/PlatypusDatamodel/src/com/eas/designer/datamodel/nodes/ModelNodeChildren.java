/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.SqlQuery;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelEditingListener;
import com.eas.client.model.Relation;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author mg
 */
public abstract class ModelNodeChildren<E extends Entity<?, SqlQuery, E>, MV extends Model<E, SqlQuery>> extends Children.Keys<Object> implements ModelEditingListener<E> {

    protected MV model;
    protected NodePropertiesUndoRecorder undoRecordrer;
    protected UndoRedo.Manager undoReciever;
    protected Lookup lookup;
    protected java.util.Map<E, EntityNode> locator;

    public ModelNodeChildren(MV aModel, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super();
        model = aModel;
        undoReciever = aUndoReciever;
        undoRecordrer = new EntityNodePropertiesUndoRecorder(aUndoReciever);
        lookup = aLookup;
    }

    protected void validateLocator(){
        if(locator == null){
            Node[] lnodes = getNodes();// May be implicit locator = null code invocation
            locator = new HashMap<>();
            for (Node node : lnodes) {
                if (node instanceof EntityNode) {
                    EntityNode<E> entityNode = (EntityNode<E>) node;
                    locator.put(entityNode.getEntity(), entityNode);
                }
            }
        }
    }
    
    public EntityNode nodeByEntity(E e){
        validateLocator();
        return locator.get(e);
    }
    
    @Override
    protected void addNotify() {
        locator = null;
        model.addEditingListener(this);
        setKeys(getKeys());
    }

    @Override
    public void removeNotify() {
        locator = null;
        model.removeEditingListener(this);
        setKeys(Collections.EMPTY_SET);
    }

    @Override
    protected Node[] createNodes(Object key) {
        try {
            locator = null;
            Node createdNode = createNode((E) key);
            createdNode.addPropertyChangeListener(undoRecordrer);
            return new Node[]{createdNode};
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    protected Collection getKeys() {
        return model.getEntities().values();
    }

    @Override
    protected void destroyNodes(Node[] aNodes) {
        try {
            locator = null;
            for (Node node : aNodes) {
                node.removePropertyChangeListener(undoRecordrer);
                node.destroy();
            }
            super.destroyNodes(aNodes);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected abstract EntityNode<E> createNode(E key) throws Exception;

    @Override
    public void entityAdded(E e) {
        setKeys(getKeys());
    }

    @Override
    public void entityRemoved(E e) {
        setKeys(getKeys());
    }

    @Override
    public void relationAdded(Relation<E> aRelation) {
    }

    @Override
    public void relationRemoved(Relation<E> aRelation) {
    }

    @Override
    public void entityIndexesChanged(E e) {
    }
}

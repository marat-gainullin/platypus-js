/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import org.openide.awt.UndoRedo;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author mg
 */
public abstract class NodePropertiesUndoRecorder implements NodeListener {

    protected UndoRedo.Manager undoReciever;
    protected boolean undoing;

    public void setUndoing(boolean aValue) {
        undoing = aValue;
    }

    public NodePropertiesUndoRecorder(UndoRedo.Manager aUndoReciever) {
        super();
        undoReciever = aUndoReciever;
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
    }

    protected String convertNodePropNameToEntityPropName(String propertyName) {
        return propertyName;
    }
}

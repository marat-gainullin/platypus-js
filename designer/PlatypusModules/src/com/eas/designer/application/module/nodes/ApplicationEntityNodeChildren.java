/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.designer.datamodel.nodes.EntityNodeChildren;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.designer.datamodel.nodes.QueryParameterNode;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ApplicationEntityNodeChildren extends EntityNodeChildren<EntityNodeChildren.EntityFieldKey> {

    public ApplicationEntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(anEnity, aUndoReciever, aLookup);
    }

    @Override
    protected EntityFieldKey createKey(Field aField) {
        return new EntityFieldKey(aField);
    }

    @Override
    protected Node[] createNodes(EntityFieldKey key) {
        locator = null;
        Node node;
        if (key.field instanceof Parameter) {
            node = new QueryParameterNode((Parameter) key.field, lookup);
        } else {
            node = new FieldNode(key.field, lookup);
        }
        return new Node[]{node};
    }
}

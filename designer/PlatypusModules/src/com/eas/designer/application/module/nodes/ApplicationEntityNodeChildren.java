/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.nodes;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationDbParametersEntity;
import com.eas.designer.datamodel.nodes.EntityNodeChildren;
import com.eas.designer.datamodel.nodes.FieldNode;
import com.eas.designer.datamodel.nodes.ModelParameterNode;
import com.eas.designer.datamodel.nodes.QueryParameterNode;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class ApplicationEntityNodeChildren extends EntityNodeChildren {

    public ApplicationEntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(anEnity, aUndoReciever, aLookup);
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key instanceof EntityFieldKey) {
            EntityFieldKey efk = (EntityFieldKey) key;
            Node node;
            if (entity instanceof ApplicationDbParametersEntity) {
                node = new ModelParameterNode(efk.field, lookup);
            } else if (efk.field instanceof Parameter) {
                node = new QueryParameterNode((Parameter) efk.field, lookup);
            } else {
                node = new FieldNode(efk.field, lookup);
            }
            return new Node[]{node};
        } else {
            return null;
        }

    }
}

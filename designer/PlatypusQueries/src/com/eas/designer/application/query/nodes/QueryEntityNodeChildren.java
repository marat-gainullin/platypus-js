/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.designer.explorer.model.nodes.EntityNodeChildren;
import com.eas.designer.explorer.model.nodes.FieldNode;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class QueryEntityNodeChildren extends EntityNodeChildren {

    public QueryEntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(anEnity, aUndoReciever, aLookup);
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key instanceof EntityFieldKey) {
            EntityFieldKey efk = (EntityFieldKey) key;
            Node node;
            if (efk.field instanceof Parameter) {
                node = new QueryModelParametersNode((Parameter) efk.field, lookup);
            } else {
                node = new FieldNode(efk.field, lookup);
            }
            return new Node[]{node};
        } else {
            return null;
        }

    }
}

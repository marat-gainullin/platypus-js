/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.model.query.QueryEntity;
import com.eas.designer.datamodel.nodes.EntityNode;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class QueryParametersEntityNode extends EntityNode<QueryEntity> {
    
    public QueryParametersEntityNode(QueryEntity aEntity, UndoRedo.Manager aUndoReciever, Lookup aLookup) throws Exception {
        super(aEntity, aUndoReciever, new QueryEntityNodeChildren(aEntity, aUndoReciever, aLookup), aLookup);
    }
}

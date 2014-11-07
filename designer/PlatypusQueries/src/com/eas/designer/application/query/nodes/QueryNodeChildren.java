/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.model.query.QueryEntity;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.query.QueryParametersEntity;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.nodes.QueryNodeChildren.OutputFieldsNodeKey;
import com.eas.designer.datamodel.nodes.EntityNode;
import com.eas.designer.datamodel.nodes.FieldsOrderSupport;
import com.eas.designer.datamodel.nodes.ModelNodeChildren;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author vv
 */
public class QueryNodeChildren extends ModelNodeChildren<QueryEntity, QueryModel> {

    protected PlatypusQueryDataObject dataObject;
    protected OutputFieldsNodeKey outputNodeKey = new OutputFieldsNodeKey();

    public QueryNodeChildren(PlatypusQueryDataObject aDataObject, QueryModel aModel, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(aModel, aUndoReciever, aLookup);
        dataObject = aDataObject;
    }

    @Override
    protected Collection getKeys() {
        Collection entities = super.getKeys();
        List keys = new ArrayList();
        keys.add(((QueryModel) model).getParametersEntity());
        keys.addAll(entities);
        keys.add(outputNodeKey);
        return keys;
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key instanceof QueryEntity) {
            try {
                Node createdNode = createNode((QueryEntity) key);
                createdNode.addPropertyChangeListener(undoRecordrer);
                return new Node[]{createdNode};
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        } else if (key instanceof OutputFieldsNodeKey) {
            return new Node[]{new OutputFieldsNode(dataObject)};
        }
        throw new IllegalStateException("Unkown type of node key.");//NOI18N
    }

    @Override
    protected EntityNode createNode(QueryEntity key) throws Exception {
        EntityNode node;
        FieldsOrderSupport fos;
        Lookup lkp;
        if (key instanceof QueryParametersEntity) {
            fos = new FieldsOrderSupport();
            lkp = Lookups.fixed(key, fos);
            node = new QueryParametersEntityNode(key, undoReciever, new ProxyLookup(lookup, lkp));
            fos.setEntityNode(node);
        } else {
            lkp = Lookups.fixed(key);
            node = new QueryEntityNode(key, undoReciever, new ProxyLookup(lookup, lkp));
        }
        return node;
    }

    public class OutputFieldsNodeKey {
    }
}

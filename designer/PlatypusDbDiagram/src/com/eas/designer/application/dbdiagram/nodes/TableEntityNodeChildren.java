/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.model.Entity;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.designer.application.dbdiagram.nodes.TableIndexesNode.TableIndexesKey;
import com.eas.designer.explorer.model.nodes.EntityNodeChildren;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author vv
 */
public class TableEntityNodeChildren extends EntityNodeChildren {

    public TableEntityNodeChildren(Entity anEnity, UndoRedo.Manager aUndoReciever, Lookup aLookup) {
        super(anEnity, aUndoReciever, aLookup);
    }

    @Override
    protected List getKeys() {
        List fieldsKeys = super.getKeys();
        fieldsKeys.add(new TableIndexesKey());
        return fieldsKeys;
    }

    @Override
    protected Node[] createNodes(Object key) {
        if (key instanceof EntityFieldKey) {
            EntityFieldKey efk = (EntityFieldKey) key;
            Node node;
            try {
                node = new TableFieldNode(efk.field, lookup);
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
            return new Node[]{ node };
        } else if (key instanceof TableIndexesKey) {
            assert entity instanceof FieldsEntity;
            return new Node[]{ new TableIndexesNode(new TableIndexesChildren((FieldsEntity)entity, lookup), (FieldsEntity)entity, lookup) };
        } else {
            return null;
        }
    }
}

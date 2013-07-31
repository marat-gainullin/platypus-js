/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author vv
 */
public class FieldsOrderSupport extends Index.Support {

    EntityNode node;

    public FieldsOrderSupport() {
        super();
    }

    public void setEntityNode(EntityNode aNode) {
        node = aNode;
    }

    @Override
    public Node[] getNodes() {
        if (node != null) {
            return node.getChildren().getNodes(true);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getNodesCount() {
        return getNodes().length;
    }

    @Override
    public void reorder(int[] perm) {
        node.reorder(perm);
    }
}

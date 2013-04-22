/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author vv
 */
public class ColumnsOrderSupport extends Index.Support {

    TableIndexNode node;

    public ColumnsOrderSupport() {
        super();
    }

    public void setTableIndexNode(TableIndexNode aNode) {
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
        DbTableIndexSpec newIndex = node.getIndex().copy();
        assert newIndex.getColumns().size() == perm.length;
        int i = 0;
        for (DbTableIndexColumnSpec indexColumn : newIndex.getColumns()) {
            indexColumn.setOrdinalPosition(perm[i]);
            i++;
        }
        node.modifyIndex(newIndex);
    }
}

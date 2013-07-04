/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vy
 */
class FilteredTablesModel extends DefaultTreeModel {

    private FilteredTablesNode.STRUCTURE_TYPE showedType;

    public FilteredTablesModel(TreeNode root) {
        this(root, false);
    }

    public FilteredTablesModel(TreeNode root, boolean asksAllowsChildren) {
        this(root, false, null);
    }

    public FilteredTablesModel(TreeNode root, boolean asksAllowsChildren, FilteredTablesNode.STRUCTURE_TYPE aShowedType) {
        super(root, asksAllowsChildren);
        showedType = aShowedType;
    }

    public FilteredTablesNode.STRUCTURE_TYPE getShowedType() {
        return showedType;
    }

    public void setShowedType(FilteredTablesNode.STRUCTURE_TYPE aShowedType) {
        showedType = aShowedType;
        Object rootNode = getRoot();
        if (rootNode instanceof FilteredTablesNode) {
            ((FilteredTablesNode) rootNode).setType(aShowedType);
        }
    }
}

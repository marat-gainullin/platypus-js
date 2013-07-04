/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author vy
 */
public class FilteredTablesNode extends DefaultMutableTreeNode {

    public enum STRUCTURE_TYPE {

        SOURCE, DESTINATION, BOTH
    };
    private STRUCTURE_TYPE structureType;

    public STRUCTURE_TYPE getStructureType() {
        return structureType;
    }

    public FilteredTablesNode() {
        super();
    }

    public FilteredTablesNode(STRUCTURE_TYPE aStructureType) {
        super();
        structureType = aStructureType;
    }

    public FilteredTablesNode(Object userObject, STRUCTURE_TYPE aStructureType) {
        super(userObject);
        structureType = aStructureType;
    }

    public FilteredTablesNode(Object userObject, boolean allowsChildren, STRUCTURE_TYPE aStructureType) {
        super(userObject, allowsChildren);
        structureType = aStructureType;
    }

    @Override
    public int getIndex(TreeNode aChild) {
        if (structureType == null) {
            return super.getIndex(aChild);
        }
        int realIndex = -1;
        int visibleIndex = -1;
        Enumeration e = children.elements();
        while (e.hasMoreElements()) {
            Object nextNode = e.nextElement();
            STRUCTURE_TYPE type = null;
            if (nextNode instanceof FilteredTablesNode) {
                type = ((FilteredTablesNode) nextNode).getStructureType();
            }
            if (type == null || type.equals(structureType)) {
                visibleIndex++;
            }
            if (nextNode.equals(aChild)) {
                return visibleIndex;
            }
        }
        return realIndex;
    }

    @Override
    public TreeNode getChildAt(int index) {
        if (structureType == null) {
            return super.getChildAt(index);
        }
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }

        int realIndex = -1;
        int visibleIndex = -1;
        Enumeration e = children.elements();
        while (e.hasMoreElements()) {
            Object nextNode = e.nextElement();
            STRUCTURE_TYPE type = null;
            if (nextNode instanceof FilteredTablesNode) {
                type = ((FilteredTablesNode) nextNode).getStructureType();
            }
            if (type == null || type.equals(structureType)) {
                visibleIndex++;
            }
            realIndex++;
            if (visibleIndex == index) {
                return (TreeNode) children.elementAt(realIndex);
            }
        }
        throw new ArrayIndexOutOfBoundsException("index unmatched");
    }

    @Override
    public int getChildCount() {
        if (structureType == null) {
            return super.getChildCount();
        }
        if (children == null) {
            return 0;
        }

        int count = 0;
        Enumeration e = children.elements();
        while (e.hasMoreElements()) {
            Object nextNode = e.nextElement();
            STRUCTURE_TYPE type = null;
            if (nextNode instanceof FilteredTablesNode) {
                type = ((FilteredTablesNode) nextNode).getStructureType();
            }
            if (type == null || type.equals(structureType)) {
                count++;
            }
        }
        return count;

    }

    public STRUCTURE_TYPE getType() {
        return structureType;
    }

    public void setType(STRUCTURE_TYPE aStructureType) {
        structureType = aStructureType;
    }
}

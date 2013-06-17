/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author vy
 */
public class MetadataTreeCellRenderer extends DefaultTreeCellRenderer {

    private Icon defaultIcon;
    private Icon equalsIcon;
    private Icon sameIcon;
    private Icon notSameIcon;
    private Icon notExistsIcon;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        assert value instanceof DefaultMutableTreeNode;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        Icon icon = null;
        if (userObject != null) {
            icon = getIcon(userObject);
            if (userObject instanceof DbTableInfo) {
                DbTableInfo info = (DbTableInfo) userObject;
                if (info.isEditable()) {
                    JCheckBox checkBoxRenderer = new JCheckBox();
                    JPanel p = new JPanel();
                    p.setLayout(new FlowLayout(0, 0, 0));
                    p.add(checkBoxRenderer);
                    JLabel lb = new JLabel();
                    lb.setText(info.toString());
                    checkBoxRenderer.setSelected(info.isChoice());
                    lb.setIcon(icon);
                    p.add(lb);
                    if (selected) {
                        lb.setForeground(getTextSelectionColor());
                        p.setBackground(getBackgroundSelectionColor());
                    } else {
                        lb.setForeground(getTextNonSelectionColor());
                        p.setBackground(getBackgroundNonSelectionColor());
                    }
                    return p;
                }
            }

        }
        setIcon(icon);
        return this;
    }

    public Icon getIcon(Object userObject) {
        assert userObject != null;
        if (userObject instanceof DbStructureInfo) {
            DbStructureInfo info = (DbStructureInfo) userObject;
            DbStructureInfo.COMPARE_TYPE compareType = info.getCompareType();
            if (compareType == null) {
                return defaultIcon;
            }
            switch (compareType) {
                case NOT_EXIST:
                    return notExistsIcon;
                case NOT_SAME:
                    return notSameIcon;
                case NOT_EQUAL:
                    return sameIcon;
                case EQUAL:
                    return equalsIcon;
                default:
                    return defaultIcon;
            }
        }
        return null;
    }

    public Icon getDefaultIcon() {
        return defaultIcon;
    }

    public void setDefaultIcon(Icon aIcon) {
        defaultIcon = aIcon;
    }

    public Icon getNotSameIcon() {
        return notSameIcon;
    }

    public void setNotSameIcon(Icon aIcon) {
        notSameIcon = aIcon;
    }

    public Icon getNotExistsIcon() {
        return notExistsIcon;
    }

    public void setNotExistsIcon(Icon aIcon) {
        notExistsIcon = aIcon;
    }

    public Icon getSameIcon() {
        return sameIcon;
    }

    public void setSameIcon(Icon aIcon) {
        sameIcon = aIcon;
    }

    public Icon getEqualsIcon() {
        return equalsIcon;
    }

    public void setEqualsIcon(Icon aIcon) {
        equalsIcon = aIcon;
    }
}

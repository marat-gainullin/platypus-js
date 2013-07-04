/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author vy
 */
public class ChoicedTreeCellEditor extends DefaultCellEditor {

    protected DbTableInfo nodeData;
    protected JCheckBox editor = new JCheckBox();

    public ChoicedTreeCellEditor() {
        super(new JCheckBox());
    }

    @Override
    public boolean isCellEditable(EventObject aEvent) {
        if (aEvent != null && (aEvent.getSource() instanceof JTree)) {
            JTree tree = ((JTree) aEvent.getSource());
            if (aEvent instanceof MouseEvent) {
                MouseEvent event = (MouseEvent) aEvent;
                TreePath path = tree.getPathForLocation(event.getX(), event.getY());
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof DbTableInfo && ((DbTableInfo) userObject).isEditable()) {
                    if (event.getClickCount() == 2) {
                        if (tree.isExpanded(path)) {
                            tree.collapsePath(path);
                        } else {
                            tree.expandPath(path);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree aTree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();
        if (userObject instanceof DbTableInfo) {
            nodeData = (DbTableInfo) userObject;

            JPanel pn = new JPanel();
            pn.setLayout(new FlowLayout(0, 0, 0));
            pn.add(editor);
            JLabel lb = new JLabel();
            lb.setText(nodeData.toString());
            editor.setSelected(nodeData.isChoice());
            TreeCellRenderer cellRenderer = aTree.getCellRenderer();
            if (cellRenderer instanceof MetadataTreeCellRenderer) {
                MetadataTreeCellRenderer renderer = (MetadataTreeCellRenderer) cellRenderer;
                lb.setIcon(renderer.getIcon(userObject));
                lb.setForeground(renderer.getTextSelectionColor());
                pn.setBackground(renderer.getBackgroundSelectionColor());
            }
            editor.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    nodeData.setChoice(editor.isSelected());
                }
            });
            pn.add(lb);
            return pn;
        }
        return editor;
    }
}

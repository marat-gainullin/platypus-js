/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import com.bearsoft.gui.grid.IconCache;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TableModelWrapper;
import com.bearsoft.gui.grid.data.TreedModel;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

/**
 * Renderer, used as leading component for tree column rendering.
 *
 * @author Gala
 */
public class TreeColumnLeadingComponent<T> extends NonRepaintablePanel {

    protected static final int SPACE_UNITY_SIZE = 16;
    protected static Icon legsCollapsedIcon;
    protected static Icon legsExpandedIcon;
    protected TableModel model;
    protected TableFront2TreedModel<T> front;
    protected Integer operatingRow;
    protected PreferredBiasLabel centerLabel;
    protected NonRepaintableLabel rightLabel;
    //
    protected Icon folderIcon;
    protected Icon openFolderIcon;
    protected Icon leafIcon;
    protected boolean leaf;
    //frequent runtime
    protected Icon legsIcon;
    protected Icon nodeIcon;

    static {
        Object expandedIcon = UIManager.get("Tree.expandedIcon");
        Object collapsedIcon = UIManager.get("Tree.collapsedIcon");
        if (expandedIcon instanceof Icon) {
            legsExpandedIcon = (Icon) expandedIcon;
        } else {
            legsExpandedIcon = IconCache.getIcon("treeExpanded.png");
        }
        if (collapsedIcon instanceof Icon) {
            legsCollapsedIcon = (Icon) collapsedIcon;
        } else {
            legsCollapsedIcon = IconCache.getIcon("treeCollapsed.png");
        }
    }

    /**
     * Leading component constructor.
     *
     * @param aModel Model of the column's table. Typically it's table model
     * front to treed model.
     * @param aRepaintable A repaintable flag. It's considered, that repaintable
     * components are used in editors. So, if the flag is true, than component's
     * work will be optimized for cells editing, and for cells rendering
     * otherwise.
     */
    public TreeColumnLeadingComponent(TableModel aModel, boolean aRepaintable, Icon aFolderIcon, Icon anOpenFolderIcon, Icon aLeafIcon) {
        super(aRepaintable);
        model = aModel;
        front = achieveTreeTableFront(model);
        centerLabel = new PreferredBiasLabel(aRepaintable);
        rightLabel = new NonRepaintableLabel(aRepaintable);
        //rightLabel.setText(" ");
        rightLabel.setOpaque(false);
        folderIcon = aFolderIcon;
        openFolderIcon = anOpenFolderIcon;
        leafIcon = aLeafIcon;

        centerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centerLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        centerLabel.setOpaque(false);
        setOpaque(false);
        add(centerLabel, BorderLayout.CENTER);
        add(rightLabel, BorderLayout.EAST);
    }

    /**
     * Discardes all prepared information, related to row to be rendered or
     * edited. Typically this methos is called by editors, but not renderers.
     *
     * @see #prepareRow(int)
     */
    public void unprepare() {
        operatingRow = null;
    }

    /**
     * Prepares a row to be rendered or edited.
     *
     * @param aRow A row number to be prepared. It is in model space.
     * @see #unprepare()
     */
    public void prepareRow(int aRow) {
        if (operatingRow == null || operatingRow != aRow) {
            operatingRow = aRow;
            T elToRender = front.getElementAt(operatingRow);
            TreedModel<T> treedModel = front.unwrap();
            T currElement = elToRender;
            int level = -1;
            while (currElement != null) {
                currElement = treedModel.getParentOf(currElement);
                level++;
            }
            boolean expanded = front.isExpanded(elToRender);
            legsIcon = legsCollapsedIcon;
            nodeIcon = folderIcon;
            if (expanded) {
                legsIcon = legsExpandedIcon;
                nodeIcon = openFolderIcon;
            }
            leaf = treedModel.isLeaf(elToRender);
            if (leaf) {
                nodeIcon = leafIcon;
                legsIcon = null;
                level++;
            }
            if (!leaf && expanded) {// lazy trees make us to do things like this
                List<T> children = treedModel.getChildrenOf(elToRender);
                if (children == null || children.isEmpty()) {
                    nodeIcon = leafIcon;
                    legsIcon = null;
                }
            }
            centerLabel.setBias(level * SPACE_UNITY_SIZE);
            centerLabel.setIcon(legsIcon);
            rightLabel.setIcon(nodeIcon);
        }
    }

    public boolean isLeaf() {
        return leaf;
    }

    public TableFront2TreedModel<T> getFront() {
        return front;
    }

    public static <T> TableFront2TreedModel<T> achieveTreeTableFront(TableModel aModel) {
        TableModel currentModel = aModel;
        while (!(currentModel instanceof TableFront2TreedModel<?>) && currentModel instanceof TableModelWrapper) {
            TableModelWrapper wrapper = (TableModelWrapper) currentModel;
            currentModel = wrapper.unwrap();
        }
        if (currentModel instanceof TableFront2TreedModel<?>) {
            return (TableFront2TreedModel<T>) currentModel;
        } else {
            return null;
        }
    }

    public Icon getNodeIcon() {
        return nodeIcon;
    }

    public Icon getLegsIcon() {
        return legsIcon;
    }
}

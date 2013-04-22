/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.rendering;

import com.bearsoft.gui.grid.IconCache;
import com.bearsoft.gui.grid.data.TableFront2TreedModel;
import com.bearsoft.gui.grid.data.TableModelWrapper;
import com.bearsoft.gui.grid.data.TreedModel;
import com.eas.gui.CascadedStyle;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

/**
 * Renderer, used as leading component for tree column rendering.
 * @author Gala
 */
public class TreeColumnLeadingComponent<T> extends NonRepaintablePanel implements PropertyChangeListener {

    protected static final int SPACE_UNITY_SIZE = 16;
    protected static Icon legsCollapsedIcon = null;
    protected static Icon legsExpandedIcon = null;
    protected CascadedStyle style;
    protected TableModel model;
    protected TableFront2TreedModel<T> front;
    protected Integer operatingRow = null;
    protected PreferredBiasLabel centerLabel;
    protected NonRepaintableLabel rightLabel;
    protected Icon legsIcon;
    protected Icon nodeIcon;
    protected boolean leaf;

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
     * @param aModel Model of the column's table. Typically it's table model front to treed model.
     * @param aStyle A <code>CascadedStyle</code> instance, used to render graphic attributes.
     * @param aRepaintable A repaintable flag. It's considered, that repaintable components are used in editors.
     * So, if the flag is true, than component's work will be optimized for cells editing, and for cells rendering otherwise.
     */
    public TreeColumnLeadingComponent(TableModel aModel, CascadedStyle aStyle, boolean aRepaintable) {
        super(aRepaintable);
        model = aModel;
        front = achieveTreeTableFront(model);
        setStyle(aStyle);
        centerLabel = new PreferredBiasLabel(aRepaintable);
        rightLabel = new NonRepaintableLabel(aRepaintable);
        //rightLabel.setText(" ");
        rightLabel.setOpaque(false);

        centerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centerLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        centerLabel.setOpaque(false);
        setOpaque(false);
        add(centerLabel, BorderLayout.CENTER);
        add(rightLabel, BorderLayout.EAST);
    }

    /**
     * Discardes all prepared information, related to row to be rendered or edited.
     * Typically this methos is called by editors, but not renderers.
     * @see #prepareRow(int) 
     */
    public void unprepare() {
        operatingRow = null;
    }

    /**
     * Prepares a row to be rendered or edited.
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
            nodeIcon = style.getFolderIcon();
            if (expanded) {
                legsIcon = legsExpandedIcon;
                nodeIcon = style.getOpenFolderIcon();
            }
            leaf = treedModel.isLeaf(elToRender);
            if (leaf) {
                nodeIcon = style.getLeafIcon();
                legsIcon = null;
                level++;
            }
            if (!leaf && expanded) {// lazy trees make us to do things like this
                List<T> children = treedModel.getChildrenOf(elToRender);
                if (children == null || children.isEmpty()) {
                    nodeIcon = style.getLeafIcon();
                    legsIcon = null;
                }
            }
            centerLabel.setBias(level * SPACE_UNITY_SIZE);
            centerLabel.setIcon(legsIcon);
            rightLabel.setIcon(nodeIcon);
        }
    }

    public CascadedStyle getStyle() {
        return style;
    }

    public void setStyle(CascadedStyle aStyle) {
        if (style != null) {
            style.getChangeSupport().removePropertyChangeListener(this);
        }
        style = aStyle;
        if (style != null) {
            style.getChangeSupport().addPropertyChangeListener(this);
        }
    }

    public Icon getLegsIcon() {
        return legsIcon;
    }

    public Icon getNodeIcon() {
        return nodeIcon;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == style) {
            unprepare();
        }
    }
}
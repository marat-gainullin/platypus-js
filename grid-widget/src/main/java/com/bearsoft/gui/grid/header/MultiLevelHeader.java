/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import com.bearsoft.gui.grid.header.cell.HeaderCell;
import com.eas.gui.ScriptColor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.RowSorterListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public class MultiLevelHeader extends JPanel {

    public static final int PICK_MARGIN_SIZE = 2;
    // design data
    protected TableColumnModel columnModel;
    protected RowSorter<? extends TableModel> rowSorter;
    //protected InnerColumnsListener columnModelListner;
    protected RowSorterListener sorterListener;
    protected JTable table;
    // calculated data
    protected MultiLevelHeader neightbour;
    protected List<GridColumnsNode> roots;
    protected Map<GridColumnsNode, GridBagConstraints> group2Constraints = new HashMap<>();
    protected GridColumnsNode pressed4ResizeColGroup;
    protected GridColumnsNode resizingColGroup;
    protected GridColumnsNode movingColGroup;
    protected JTableHeader[] slaveHeaders;

    public MultiLevelHeader() {
        super();
    }

    public void setSlaveHeaders(JTableHeader... aSlaveHeaders) {
        slaveHeaders = aSlaveHeaders;
    }

    public MultiLevelHeader getNeightbour() {
        return neightbour;
    }

    public void setNeightbour(MultiLevelHeader aValue) {
        neightbour = aValue;
    }

    @Override
    public void scrollRectToVisible(Rectangle aRect) {
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public final void setColumnModel(TableColumnModel aModel) {
        columnModel = aModel;
        /*
         columnModelListner = new InnerColumnsListener(this);
         if (columnModel != null) {
         columnModel.addColumnModelListener(columnModelListner);
         }
         */
    }

    public RowSorter<? extends TableModel> getRowSorter() {
        return rowSorter;
    }

    public void setRowSorter(RowSorter<? extends TableModel> aSorter) {
        rowSorter = aSorter;
        sorterListener = new SorterListener(this);
        if (rowSorter != null) {
            rowSorter.addRowSorterListener(sorterListener);
        }
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable aValue) {
        table = aValue;
    }
    /*
     public InnerColumnsListener getColumnModelListener() {
     return columnModelListner;
     }
     */

    public GridColumnsNode getPressed4ResizeColGroup() {
        return pressed4ResizeColGroup;
    }

    public void setPressed4ResizeColGroup(GridColumnsNode aColGroup) {
        if (aColGroup == null || aColGroup.isResizable()) {
            pressed4ResizeColGroup = aColGroup;
        }
    }

    public GridColumnsNode getResizingColGroup() {
        return resizingColGroup;
    }

    public void setResizingColGroup(GridColumnsNode aColGroup) {
        if (aColGroup == null || aColGroup.isResizable()) {
            resizingColGroup = aColGroup;
        }
    }

    public GridColumnsNode getMovingColGroup() {
        return movingColGroup;
    }

    public void setMovingColGroup(GridColumnsNode aColGroup) {
        if (aColGroup == null || aColGroup.isMovable()) {
            movingColGroup = aColGroup;
        }
    }

    @Override
    public void doLayout() {
        if (table != null) {
            table.setSize(getSize().width, table.getSize().height);
            table.doLayout();
        }
        super.doLayout();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.width = columnModel.getTotalColumnWidth();
        if (neightbour != null) {
            MultiLevelHeader oldNeightbour = neightbour.getNeightbour();
            neightbour.setNeightbour(null);
            try {
                Dimension neightbourD = neightbour.getPreferredSize();
                d.height = Math.max(neightbourD.height, d.height);
            } finally {
                neightbour.setNeightbour(oldNeightbour);
            }
        }
        return d;
    }

    private int processGroups1(List<GridColumnsNode> aGroups, int aLevel, int maxLevel) {
        int deepChildrenCount = 0;
        for (GridColumnsNode group : aGroups) {
            GridBagConstraints constraints = group2Constraints.get(group);
            if (constraints == null) {
                constraints = new GridBagConstraints();
                constraints.weightx = 0;
                constraints.weighty = 1;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.fill = GridBagConstraints.BOTH;
                group2Constraints.put(group, constraints);
            }
            constraints.gridy = aLevel;
            if (group.hasChildren()) {
                constraints.gridheight = 1;
            } else {
                constraints.gridheight = maxLevel - constraints.gridy + 1;
                ++deepChildrenCount;
            }
            int childrenCount = processGroups1(group.getChildren(), aLevel + 1, maxLevel);
            deepChildrenCount += childrenCount;

            constraints.gridwidth = Math.max(1, childrenCount);
        }
        return deepChildrenCount;
    }

    private void processGroups2(List<GridColumnsNode> aGroups) {
        if (!aGroups.isEmpty()) {
            GridColumnsNode group = aGroups.get(0);
            GridBagConstraints constraints = group2Constraints.get(group);
            assert constraints != null;
            if (group.getParent() != null) {
                GridBagConstraints parentConstraints = group2Constraints.get(group.getParent());
                assert parentConstraints != null;
                constraints.gridx = parentConstraints.gridx;
            } else {
                constraints.gridx = 0;
            }
            processGroups2(group.getChildren());
        }
        for (int i = 1; i < aGroups.size(); i++) {
            GridColumnsNode groupPrev = aGroups.get(i - 1);
            GridColumnsNode group = aGroups.get(i);
            GridBagConstraints constraints = group2Constraints.get(group);
            assert constraints != null;
            GridBagConstraints prevConstraints = group2Constraints.get(groupPrev);
            assert prevConstraints != null;
            constraints.gridx = prevConstraints.gridx + prevConstraints.gridwidth;
            processGroups2(group.getChildren());
        }
    }

    private int getMaxLevel(int aLevel, List<GridColumnsNode> aRoots) {
        int maxLevel = aLevel;
        for (GridColumnsNode aRoot : aRoots) {
            if (aRoot.hasChildren()) {
                int level = getMaxLevel(aLevel + 1, aRoot.getChildren());
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        return maxLevel;
    }

    protected GridColumnsNode getRoot(GridColumnsNode aGroup) {
        GridColumnsNode cGroup = aGroup;
        GridColumnsNode rGroup = aGroup;
        while (cGroup.getParent() != null) {
            cGroup = cGroup.getParent();
            rGroup = cGroup;
        }
        return rGroup;
    }

    public void regenerate() {
        removeAll();
        if (columnModel == null) {
            throw new NullPointerException("TreedTableHeader needs a column model, but it is absent.");
        }
        group2Constraints.clear();
        if (roots == null) {
            setRoots(wrapColumnsCalculateRoots());
        }
        int maxLevel = getMaxLevel(0, roots);
        processGroups1(roots, 0, maxLevel);
        processGroups2(roots);
        fillControl();
        invalidate();
        repaint();
    }

    public List<GridColumnsNode> getRoots() {
        return roots;
    }

    private void clearTableColumn(List<GridColumnsNode> aForest) {
        for (GridColumnsNode node : aForest) {
            node.setTableColumn(null);
            node.setStyleSource(null);
            clearTableColumn(node.getChildren());
        }
    }

    public final void setRoots(List<GridColumnsNode> aValue) {
        if (roots != aValue) {
            if (roots != null) {
                clearTableColumn(roots);// avoid GridColumnsNode leak
            }
            roots = aValue;
            if (roots != null) {
                List<GridColumnsNode> leaves = new ArrayList<>();
                MultiLevelHeader.achieveLeaves(roots, leaves);
            }
        }
    }

    protected List<GridColumnsNode> wrapColumnsCalculateRoots() {
        List<GridColumnsNode> roots = new ArrayList<>();
        Set<GridColumnsNode> met = new HashSet<>();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn col = columnModel.getColumn(i);
            GridColumnsNode group = new GridColumnsNode(col);
            GridColumnsNode rootGroup = getRoot(group);
            if (!met.contains(rootGroup)) {
                met.add(rootGroup);
                roots.add(rootGroup);
            }
        }
        return roots;
    }

    private void fillControl() {
        setFocusCycleRoot(false);
        setLayout(new GridBagLayout());
        int maxRow = -1;
        int maxColumn = -1;
        for (Map.Entry<GridColumnsNode, GridBagConstraints> entry : group2Constraints.entrySet()) {
            GridBagConstraints gbc = entry.getValue();
            if (maxRow < gbc.gridy) {
                maxRow = gbc.gridy;
            }
            if (maxColumn < gbc.gridx) {
                maxColumn = gbc.gridx;
            }
            add(new HeaderCell(entry.getKey(), this), gbc);
        }
        GridBagConstraints goatGbc = new GridBagConstraints();
        goatGbc.gridx = maxColumn + 1;
        goatGbc.gridy = maxRow + 1;
        goatGbc.weightx = 1;
        goatGbc.weighty = 1;
        add(new HeaderCell(new GridColumnsNode(), this), goatGbc);
    }

    public static void achieveLeaves(GridColumnsNode aGroup, List<GridColumnsNode> aLeaves) {
        if (aGroup.isLeaf()) {
            aLeaves.add(aGroup);
        }
        for (int i = 0; i < aGroup.getChildren().size(); i++) {
            achieveLeaves(aGroup.getChildren().get(i), aLeaves);
        }
    }
    
    public static void achieveLeaves(List<GridColumnsNode> aRoots, List<GridColumnsNode> aLeaves) {
        for (GridColumnsNode node : aRoots) {
            if (node.isLeaf()) {
                aLeaves.add(node);
            } else {
                achieveLeaves(node.getChildren(), aLeaves);
            }
        }
    }

    public void checkStructure() {
        List<GridColumnsNode> leaves = new ArrayList<>();
        MultiLevelHeader.achieveLeaves(roots, leaves);
        assert leaves.size() == columnModel.getColumnCount();
        for (int i = 0; i < leaves.size(); i++) {
            assert leaves.get(i).getTableColumn() == columnModel.getColumn(i);
        }
    }

    public static void simulateMouseEntered(HeaderCell aCell, MouseEvent e) {
        Point pt = e.getPoint();
        Component releasedComponent = aCell.getHeader().getComponentAt(new Point(aCell.getX() + pt.x, aCell.getY() + pt.y));
        if (releasedComponent != null
                && releasedComponent instanceof HeaderCell
                && releasedComponent != aCell) {
            aCell.getHeader().setResizingColGroup(null);
            aCell.getHeader().setPressed4ResizeColGroup(null);
            aCell.getHeader().setMovingColGroup(null);
            MouseListener[] ml = releasedComponent.getMouseListeners();
            for (MouseListener l : ml) {
                l.mouseEntered(e);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isOpaque() && g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            Color backColor = getBackground();
            Color ltBackColor = ScriptColor.brighter(backColor, 0.95);
            Color dkBackColor = ScriptColor.darker(backColor, 0.95);
            Dimension size = getSize();
            Paint gradient1 = new GradientPaint(new Point2D.Float(0, 0), ltBackColor, new Point2D.Float(0, size.height / 2), backColor);
            Paint gradient2 = new GradientPaint(new Point2D.Float(0, size.height / 2 + 1), dkBackColor, new Point2D.Float(0, size.height), backColor);
            g2d.setPaint(gradient1);
            g2d.fillRect(0, 0, size.width, size.height / 2);
            g2d.setPaint(gradient2);
            g2d.fillRect(0, size.height / 2, size.width, size.height / 2);
        }
    }
}

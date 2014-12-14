/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

import com.bearsoft.gui.grid.header.cell.HeaderCell;
import com.eas.gui.CascadedStyle;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
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
    protected List<GridColumnsGroup> roots;
    protected Map<GridColumnsGroup, GridBagConstraints> group2Constraints = new HashMap<>();
    protected GridColumnsGroup pressed4ResizeColGroup;
    protected GridColumnsGroup resizingColGroup;
    protected GridColumnsGroup movingColGroup;
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

    public GridColumnsGroup getPressed4ResizeColGroup() {
        return pressed4ResizeColGroup;
    }

    public void setPressed4ResizeColGroup(GridColumnsGroup aColGroup) {
        if (aColGroup == null || aColGroup.isResizable()) {
            pressed4ResizeColGroup = aColGroup;
        }
    }

    public GridColumnsGroup getResizingColGroup() {
        return resizingColGroup;
    }

    public void setResizingColGroup(GridColumnsGroup aColGroup) {
        if (aColGroup == null || aColGroup.isResizable()) {
            resizingColGroup = aColGroup;
            postResizingColumn(resizingColGroup);
        }
    }

    private void postResizingColumn(GridColumnsGroup aColGroup) {
        TableColumn tCol = null;
        if (aColGroup != null) {
            List<GridColumnsGroup> leaves = new ArrayList<>();
            MultiLevelHeader.achieveLeaves(aColGroup, leaves);
            tCol = leaves.get(leaves.size() - 1).getTableColumn();
        }
        if (slaveHeaders != null) {
            for (JTableHeader header : slaveHeaders) {
                header.setResizingColumn(tCol);
            }
        }
    }

    public GridColumnsGroup getMovingColGroup() {
        return movingColGroup;
    }

    public void setMovingColGroup(GridColumnsGroup aColGroup) {
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

    private int processGroups1(List<GridColumnsGroup> aGroups, int aLevel, int maxLevel) {
        int deepChildrenCount = 0;
        for (GridColumnsGroup group : aGroups) {
            GridBagConstraints constraints = group2Constraints.get(group);
            if (constraints == null) {
                constraints = new GridBagConstraints();
                if (group.isLeaf()) {
                    constraints.weightx = 1;
                } else {
                    constraints.weightx = 0;
                }
                constraints.weighty = 1;
                constraints.anchor = GridBagConstraints.CENTER;
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

    private void processGroups2(List<GridColumnsGroup> aGroups) {
        if (!aGroups.isEmpty()) {
            GridColumnsGroup group = aGroups.get(0);
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
            GridColumnsGroup groupPrev = aGroups.get(i - 1);
            GridColumnsGroup group = aGroups.get(i);
            GridBagConstraints constraints = group2Constraints.get(group);
            assert constraints != null;
            GridBagConstraints prevConstraints = group2Constraints.get(groupPrev);
            assert prevConstraints != null;
            constraints.gridx = prevConstraints.gridx + prevConstraints.gridwidth;
            processGroups2(group.getChildren());
        }
    }

    private int getMaxLevel(int aLevel, List<GridColumnsGroup> aRoots) {
        int maxLevel = aLevel;
        for (GridColumnsGroup aRoot : aRoots) {
            if (aRoot.hasChildren()) {
                int level = getMaxLevel(aLevel + 1, aRoot.getChildren());
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        return maxLevel;
    }

    protected GridColumnsGroup getRoot(GridColumnsGroup aGroup) {
        GridColumnsGroup cGroup = aGroup;
        GridColumnsGroup rGroup = aGroup;
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

    public List<GridColumnsGroup> getRoots() {
        return roots;
    }

    public final void setRoots(List<GridColumnsGroup> aValue) {
        if (roots != aValue) {
            if (roots != null) {
                List<GridColumnsGroup> leaves = new ArrayList<>();
                for (GridColumnsGroup root : roots) {
                    MultiLevelHeader.achieveLeaves(root, leaves);
                }
                for (GridColumnsGroup leave : leaves) {
                    leave.setTableColumn(null);
                }
            }
            roots = aValue;
            if (roots != null) {
                List<GridColumnsGroup> leaves = new ArrayList<>();
                for (GridColumnsGroup root : roots) {
                    MultiLevelHeader.achieveLeaves(root, leaves);
                }
                for (int i = 0; i < leaves.size(); i++) {
                    leaves.get(i).setTableColumn(columnModel.getColumn(i));
                }
            }
        }
    }

    protected List<GridColumnsGroup> wrapColumnsCalculateRoots() {
        List<GridColumnsGroup> res = new ArrayList<>();
        Set<GridColumnsGroup> reviewed = new HashSet<>();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn col = columnModel.getColumn(i);
            GridColumnsGroup group = new GridColumnsGroup(col);
            GridColumnsGroup rGroup = getRoot(group);
            if (!reviewed.contains(rGroup)) {
                reviewed.add(rGroup);
                res.add(rGroup);
            }
        }
        return res;
    }

    private void fillControl() {
        setFocusCycleRoot(false);
        setLayout(new GridBagLayout());
        for (Entry<GridColumnsGroup, GridBagConstraints> entry : group2Constraints.entrySet()) {
            add(new HeaderCell(entry.getKey(), this), entry.getValue());
        }
    }

    public static void achieveLeaves(GridColumnsGroup aGroup, List<GridColumnsGroup> aLeaves) {
        if (aGroup.isLeaf()) {
            aLeaves.add(aGroup);
        }
        for (int i = 0; i < aGroup.getChildren().size(); i++) {
            achieveLeaves(aGroup.getChildren().get(i), aLeaves);
        }
    }

    public void checkStructure() {
        List<GridColumnsGroup> leaves = new ArrayList<>();
        for (GridColumnsGroup root : roots) {
            MultiLevelHeader.achieveLeaves(root, leaves);
        }
        assert leaves.size() == columnModel.getColumnCount();
        for (int i = 0; i < leaves.size(); i++) {
            assert leaves.get(i).getTableColumn() == columnModel.getColumn(i);
        }
    }

    public void setPreferredWidth2LeafColGroups(List<GridColumnsGroup> aGroups, int oldWidth, int newWidth) {
        float fW = (float) (newWidth - oldWidth) / (float) aGroups.size();
        int totalWidth = 0;
        for (int i = 0; i < aGroups.size(); i++) {
            GridColumnsGroup colGroup = aGroups.get(i);
            assert colGroup.getChildren().isEmpty() : "setPreferredWidth2LeafColGroups is intended only for leaf column groups.";
            assert colGroup.getTableColumn() != null : "Leaf column group without a table column found.";
            int dW = Math.round(fW);
            int newChildWidth = colGroup.getWidth() + dW;
            totalWidth += newChildWidth;
            if (i == aGroups.size() - 1 && totalWidth != newWidth) {
                newChildWidth += newWidth - totalWidth;
            }
            colGroup.setWidth(newChildWidth);
            //colGroup.getTableColumn().setPreferredWidth(newChildWidth);
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
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            Color backColor = getBackground();
            Color ltBackColor = CascadedStyle.brighterColor(backColor, 0.95);
            Color dkBackColor = CascadedStyle.darkerColor(backColor, 0.95);
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

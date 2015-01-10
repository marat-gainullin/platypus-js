/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableColumn;

/**
 *
 * @author Gala
 */
public class CellResizer extends MouseAdapter {

    protected MultiLevelHeader header;
    protected HeaderCell cell;
    protected Point leftPt;
    protected Point rightPt;

    public CellResizer(HeaderCell aCell, MultiLevelHeader aHeader) {
        super();
        cell = aCell;
        header = aHeader;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        leftPt = null;
        rightPt = null;
        Dimension d = cell.getSize();
        Point pt = e.getPoint();
        if (pt.x < MultiLevelHeader.PICK_MARGIN_SIZE) {
            leftPt = pt;
            Component leftComp = header.getComponentAt(new Point(cell.getX() - 1, cell.getY() + leftPt.y));
            if (leftComp instanceof HeaderCell) {
                HeaderCell hCell = (HeaderCell) leftComp;
                assert cell != hCell;
                MouseListener[] mml = hCell.getMouseListeners();
                for (MouseListener l : mml) {
                    l.mousePressed(new MouseEvent(hCell, e.getID(), e.getWhen(), e.getModifiers(), hCell.getSize().width + e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger()));
                }
            }
            cell.getHeader().setPressed4ResizeColGroup(cell.getColGroup());
        } else if (pt.x >= d.width - MultiLevelHeader.PICK_MARGIN_SIZE - 1) {
            rightPt = pt;
            GridColumnsNode resizingNode = cell.getColGroup();
            cell.getHeader().setResizingColGroup(resizingNode);
            cell.getHeader().setPressed4ResizeColGroup(resizingNode);
            GridColumnsNode resizingLeaf = null;
            List<GridColumnsNode> leaves = new ArrayList<>();
            MultiLevelHeader.achieveLeaves(cell.getColGroup(), leaves);
            assert !leaves.isEmpty();
            for (int i = leaves.size() - 1; i >= 0; i--) {
                GridColumnsNode leaf = leaves.get(i);
                if (leaf.getTableColumn().getResizable()) {
                    resizingLeaf = leaf;
                    break;
                }
            }
            if (resizingLeaf != null) {
                header.getTable().getTableHeader().setResizingColumn(resizingLeaf.getTableColumn());
                resizingColumnMaxWidth = resizingLeaf.getTableColumn().getMaxWidth();
            }

        } else {
            leftPt = null;
            rightPt = null;
        }
    }
    private int resizingColumnMaxWidth;

    @Override
    public void mouseReleased(MouseEvent e) {
        leftPt = null;
        rightPt = null;
        TableColumn resizingColumn = header.getTable().getTableHeader().getResizingColumn();
        if (resizingColumn != null) {
            EventQueue.invokeLater(() -> {
                //resizingColumn.setMaxWidth(resizingColumnMaxWidth);
                header.getTable().getTableHeader().setResizingColumn(null);
            });
        }
        cell.getHeader().setResizingColGroup(null);
        cell.getHeader().setPressed4ResizeColGroup(null);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (rightPt != null) {
            TableColumn resizingColumn = header.getTable().getTableHeader().getResizingColumn();
            if (resizingColumn != null && resizingColumn.getResizable()) {
                Point newRightPt = e.getPoint();
                int dWidth = newRightPt.x - rightPt.x;
                List<GridColumnsNode> leaves = new ArrayList<>();
                MultiLevelHeader.achieveLeaves(cell.getColGroup(), leaves);
                assert !leaves.isEmpty();
                if (Math.abs(dWidth) > leaves.size()) {
                    int newWidth = newRightPt.x;
                    for (int i = leaves.size() - 1; i >= 0; i--) {
                        GridColumnsNode leaf = leaves.get(i);
                        if (leaf.getTableColumn() != resizingColumn) {
                            newWidth -= leaf.getTableColumn().getWidth();
                        }
                    }
                    if (newWidth < 0) {
                        newWidth = 0;
                    }
                    resizingColumn.setWidth(newWidth);
                    resizingColumn.setPreferredWidth(newWidth);
                    resizingColumn.setMaxWidth(newWidth);
                    header.invalidate();
                    header.repaint();
                }
            }
        } else if (leftPt != null) {
            Component leftComp = header.getComponentAt(new Point(cell.getX() - 1, cell.getY() + leftPt.y));
            if (leftComp instanceof HeaderCell) {
                HeaderCell hCell = (HeaderCell) leftComp;
                assert cell != hCell;
                MouseMotionListener[] mml = hCell.getMouseMotionListeners();
                for (MouseMotionListener l : mml) {
                    l.mouseDragged(new MouseEvent(hCell, e.getID(), e.getWhen(), e.getModifiers(), hCell.getSize().width + e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger()));
                }
            }
        }
    }
}

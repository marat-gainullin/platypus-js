/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

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
            cell.getHeader().setResizingColGroup(cell.getColGroup());
            cell.getHeader().setPressed4ResizeColGroup(cell.getColGroup());
        } else {
            leftPt = null;
            rightPt = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        leftPt = null;
        rightPt = null;
        cell.getHeader().setResizingColGroup(null);
        cell.getHeader().setPressed4ResizeColGroup(null);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (rightPt != null) {
            Point newRightPt = e.getPoint();
            int dWidth = newRightPt.x - rightPt.x;
            List<GridColumnsNode> leaves = new ArrayList<>();
            MultiLevelHeader.achieveLeaves(cell.getColGroup(), leaves);
            assert !leaves.isEmpty();
            if (Math.abs(dWidth) > leaves.size()) {
                Dimension oldSize = cell.getSize();
                Dimension newSize = cell.getSize();
                newSize.width += dWidth;
                if (newSize.width >= MultiLevelHeader.PICK_MARGIN_SIZE * 2
                        && (HeaderCell.isValidCellBoundary(e.getComponent()) || dWidth > 0)) {
                    cell.invalidate();
                    //header.setPreferredWidth2LeafColGroups(leaves, oldSize.width, newSize.width);
                    if (oldSize.width != cell.getSize().width) {
                        rightPt = newRightPt;
                        int dX = cell.getSize().width - newSize.width;
                        rightPt.x += dX;
                    }
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

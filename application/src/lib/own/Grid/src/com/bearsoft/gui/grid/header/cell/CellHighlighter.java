/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.Border;

/**
 *
 * @author Gala
 */
public class CellHighlighter extends MouseAdapter {

    protected HeaderCell cell;
    protected Color originalBackground;
    protected Border pressedBorder;
    protected Border originalBorder;
    protected boolean pressed = false;
    protected boolean innerPressed = false;

    public CellHighlighter(HeaderCell aCell) {
        super();
        cell = aCell;
        originalBackground = cell.getBackground();
        originalBorder = cell.getBorder();
        pressedBorder = cell.createCellLoweredBorder();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (innerPressed) {
            cell.setBorder(pressedBorder);
        }
        GridColumnsNode pressedColGroup = cell.getHeader().getPressed4ResizeColGroup();
        GridColumnsNode movingColGroup = cell.getHeader().getMovingColGroup();
        if ((pressedColGroup == null && movingColGroup == null)
                || (pressedColGroup == cell.getColGroup() || movingColGroup == cell.getColGroup())) {
            if (cell.getColGroup().isSortable()
                    || cell.getColGroup().isMovable()
                    || cell.getColGroup().isResizable()) {
                originalBackground = cell.getBackground();
                cell.setBackground(originalBackground.brighter());
                cell.setRolledover(true);
            }
            cell.setLeftRolledover(false);
            cell.setRightRolledover(false);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        GridColumnsNode pressedColGroup = cell.getHeader().getPressed4ResizeColGroup();
        GridColumnsNode movingColGroup = cell.getHeader().getMovingColGroup();
        if (pressedColGroup == null && movingColGroup == null) {
            cell.setBorder(originalBorder);
            cell.setBackground(originalBackground);
            cell.setRolledover(false);
        }
        cell.setLeftRolledover(false);
        cell.setRightRolledover(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Dimension d = cell.getSize();
        if (e.getX() >= MultiLevelHeader.PICK_MARGIN_SIZE
                && e.getX() < d.width - MultiLevelHeader.PICK_MARGIN_SIZE - 1) {
            if (cell.getColGroup().isSortable() || cell.getColGroup().isMovable()) {
                cell.setBorder(pressedBorder);
                innerPressed = true;
            }
        }
        pressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        cell.setBorder(originalBorder);
        pressed = false;
        innerPressed = false;
        Point pt = e.getPoint();
        configureCursors(pt);
        if (!cell.contains(pt)) {
            cell.setBackground(originalBackground);
            cell.setRolledover(false);
            MultiLevelHeader.simulateMouseEntered(cell, e);
            configureCursors(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point pt = e.getPoint();
        configureCursors(pt);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point pt = e.getPoint();
        configureCursors(pt);
    }

    private void configureCursors(Point pt) {
        GridColumnsNode resizingColGroup = cell.getHeader().getResizingColGroup();
        GridColumnsNode movingColGroup = cell.getHeader().getMovingColGroup();
        if (resizingColGroup != null) {
            cell.getHeader().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        } else if (movingColGroup != null) {
            cell.getHeader().setCursor(Cursor.getDefaultCursor());
        } else {
            Dimension d = cell.getSize();
            if (cell.contains(pt) && pt.x >= 0 && pt.x < MultiLevelHeader.PICK_MARGIN_SIZE) {
                Container cellParent = cell.getParent();
                LayoutManager l = cellParent.getLayout();
                if (l instanceof GridBagLayout) {
                    GridBagLayout gl = (GridBagLayout) l;
                    GridBagConstraints c = gl.getConstraints(cell);
                    if (c.gridx != 0) {
                        cell.getHeader().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    } else {
                        cell.getHeader().setCursor(Cursor.getDefaultCursor());
                    }
                } else {
                    cell.getHeader().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                }
            } else if (cell.contains(pt) && pt.x >= d.width - MultiLevelHeader.PICK_MARGIN_SIZE - 1 && pt.x < d.width) {
                cell.getHeader().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else {
                cell.getHeader().setCursor(Cursor.getDefaultCursor());
            }
        }
    }
}

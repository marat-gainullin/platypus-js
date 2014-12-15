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
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Gala
 */
public class CellMover extends MouseAdapter {

    protected HeaderCell cell;
    protected Point pressedPt;
    protected int targetIndex = -1;
    protected int selfIndex = -1;

    public CellMover(HeaderCell aCell) {
        super();
        cell = aCell;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Dimension d = cell.getSize();
        Point pt = e.getPoint();
        if (pt.x >= MultiLevelHeader.PICK_MARGIN_SIZE && pt.x < d.width - MultiLevelHeader.PICK_MARGIN_SIZE - 1) {
            pressedPt = pt;
            cell.getHeader().setMovingColGroup(cell.getColGroup());
        } else {
            pressedPt = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (cell.getColGroup() == cell.getHeader().getMovingColGroup()) {
            Point nullPt = cell.getLocation();
            Point cellPt = e.getPoint();
            Component comp = cell.getHeader().getComponentAt(cellPt.x + nullPt.x, cellPt.y + nullPt.y);
            if (comp instanceof HeaderCell) {
                HeaderCell targetCell = (HeaderCell) comp;
                if (targetCell != cell && targetCell.getColGroup().getParent() == cell.getColGroup().getParent()) {
                    selfIndex = getIndexInParent(cell.getColGroup());
                    targetIndex = getIndexInParent(targetCell.getColGroup());
                    Point targetCellNullPt = targetCell.getLocation();
                    Point targetCellPt = new Point(cellPt.x + nullPt.x - targetCellNullPt.x, cellPt.y + nullPt.y - targetCellNullPt.y);
                    targetCell.setLeftRolledover(false);
                    targetCell.setRightRolledover(false);
                    boolean leftHighlight;
                    if (targetCellPt.x > targetCell.getSize().width / 2) {
                        targetIndex++;
                        leftHighlight = false;
                    } else {
                        leftHighlight = true;
                    }
                    // ajust target index and highlighting side to avoid losting of bad drops
                    if (!leftHighlight && targetIndex == selfIndex) {
                        targetIndex--;
                        leftHighlight = true;
                    } else if (leftHighlight && (targetIndex - selfIndex) == 1) {
                        targetIndex++;
                        leftHighlight = false;
                    }
                    // end of ajusting block
                    if (targetCell.getColGroup().isMovable()) {
                        if (targetIndex < selfIndex || (targetIndex - selfIndex) > 1) {
                            if (leftHighlight) {
                                targetCell.setLeftRolledover(true);
                            } else {
                                targetCell.setRightRolledover(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MultiLevelHeader header = cell.getHeader();
        if (selfIndex != -1 && targetIndex != -1) {
            GridColumnsNode targetColGroup = neightbourColGroup(targetIndex);
            if (targetColGroup == null || targetColGroup.isMovable()) {
                TableColumnModel columnModel = header.getColumnModel();
                GridColumnsNode movingColGroup = getNeightbourByIndex(selfIndex);
                List<GridColumnsNode> leaves = new ArrayList<>();
                MultiLevelHeader.achieveLeaves(movingColGroup, leaves);
                TableColumn movingTableColumn = leaves.get(0).getTableColumn();
                int movingTableColumnIndex = getTableColumnIndex(columnModel, movingTableColumn);

                List<TableColumn> movingColumns = new ArrayList<>();
                for (int i = 0; i < leaves.size(); i++) {
                    movingColumns.add(leaves.get(i).getTableColumn());
                }
                int targetTableColumnIndex = calcTargetColumnIndex(columnModel);
                // Actual work. It will produce events, but we have to disable them
                //header.getColumnModelListener().setEventsEnabled(false);
                //try {
                    for (int i = 0; i < movingColumns.size(); i++) {
                        if (movingTableColumnIndex > targetTableColumnIndex) {
                            columnModel.moveColumn(movingTableColumnIndex + i, targetTableColumnIndex + i);
                        } else if (movingTableColumnIndex < targetTableColumnIndex) {
                            columnModel.moveColumn(movingTableColumnIndex, targetTableColumnIndex - 1);
                        }
                    }
//                } finally {
//                    header.getColumnModelListener().setEventsEnabled(true);
//                }
                if (selfIndex < targetIndex) {
                    moveColGroup(selfIndex, targetIndex - 1);
                } else {
                    moveColGroup(selfIndex, targetIndex);
                }
                cell.getHeader().regenerate();
                cell.getHeader().checkStructure();
            }
        }
        // clear all status information occured while dragging
        header.setMovingColGroup(null);
        pressedPt = null;
        targetIndex = -1;
        selfIndex = -1;
        cell.getHeader().validate();
        MultiLevelHeader.simulateMouseEntered(cell, e);
    }

    private int calcTargetColumnIndex(TableColumnModel columnModel) {
        List<GridColumnsNode> leaves = new ArrayList<>();
        GridColumnsNode targetColGroup = getNeightbourByIndex(targetIndex);
        if (targetColGroup == null) {
            targetColGroup = getNeightbourByIndex(targetIndex - 1);
            assert targetColGroup != null;
            MultiLevelHeader.achieveLeaves(targetColGroup, leaves);
            TableColumn targetTableColumn = leaves.get(leaves.size() - 1).getTableColumn();
            int targetTableColumnIndex = getTableColumnIndex(columnModel, targetTableColumn);
            return targetTableColumnIndex + 1;
        } else {
            MultiLevelHeader.achieveLeaves(targetColGroup, leaves);
            TableColumn targetTableColumn = leaves.get(0).getTableColumn();
            int targetTableColumnIndex = getTableColumnIndex(columnModel, targetTableColumn);
            return targetTableColumnIndex;
        }
    }

    private int getIndexInParent(GridColumnsNode aColGroup) {
        if (aColGroup.getParent() == null) {
            return cell.getHeader().getRoots().indexOf(aColGroup);
        } else {
            return aColGroup.getParent().getChildren().indexOf(aColGroup);
        }
    }

    private GridColumnsNode getNeightbourByIndex(int aIndex) {
        GridColumnsNode parent = cell.getColGroup().getParent();
        if (parent != null) {
            if (aIndex >= 0 && aIndex < parent.getChildren().size()) {
                return parent.getChildren().get(aIndex);
            } else {
                return null;
            }
        } else {
            if (aIndex >= 0 && aIndex < cell.getHeader().getRoots().size()) {
                return cell.getHeader().getRoots().get(aIndex);
            } else {
                return null;
            }
        }
    }

    private int getTableColumnIndex(TableColumnModel aColumnModel, TableColumn aTableColumn) {
        for (int i = 0; i < aColumnModel.getColumnCount(); i++) {
            if (aColumnModel.getColumn(i) == aTableColumn) {
                return i;
            }
        }
        return -1;
    }

    private GridColumnsNode neightbourColGroup(int targetIndex) {
        List<GridColumnsNode> neightbours = null;
        if (cell.getColGroup().getParent() != null) {
            neightbours = cell.getColGroup().getParent().getChildren();
        } else {
            neightbours = cell.getHeader().getRoots();
        }
        return targetIndex >= 0 && targetIndex < neightbours.size() ? neightbours.get(targetIndex) : null;
    }

    private void moveColGroup(int aSelfIndex, int aTargetIndex) {
        List<GridColumnsNode> neightbours = null;
        if (cell.getColGroup().getParent() != null) {
            neightbours = cell.getColGroup().getParent().getChildren();
        } else {
            neightbours = cell.getHeader().getRoots();
        }
        GridColumnsNode moving = neightbours.remove(aSelfIndex);
        neightbours.add(aTargetIndex, moving);
    }
}

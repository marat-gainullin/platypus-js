/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.RowSorter;

/**
 *
 * @author Gala
 */
public class CellSortingToggler extends MouseAdapter {

    protected HeaderCell cell;

    public CellSortingToggler(HeaderCell aCell) {
        super();
        cell = aCell;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GridColumnsNode colGroup = cell.getColGroup();
        if (colGroup.isLeaf() && colGroup.isSortable() && colGroup.getTableColumn() != null
                && cell.getHeader().getRowSorter() != null) {
            RowSorter rowSorter = cell.getHeader().getRowSorter();
            int modelIndex = colGroup.getTableColumn().getModelIndex();
            if (!e.isControlDown() && (rowSorter.getSortKeys().size() != 1 || ((RowSorter.SortKey)rowSorter.getSortKeys().get(0)).getColumn() != modelIndex)) {
                rowSorter.setSortKeys(new ArrayList<>());
            }
            rowSorter.toggleSortOrder(modelIndex);
        }
    }
}

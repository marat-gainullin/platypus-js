/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.header.GridColumnsNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

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
            int modelIndex = colGroup.getTableColumn().getModelIndex();
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            sortKeys.addAll(cell.getHeader().getRowSorter().getSortKeys());
            cell.getHeader().getRowSorter().setSortKeys(sortKeys);
            for (int i = sortKeys.size() - 1; i >= 0; i--) {
                SortKey sk = sortKeys.get(i);
                if (sk.getColumn() == modelIndex) {
                    SortOrder sortOrder = sk.getSortOrder();
                    if (sortOrder == SortOrder.ASCENDING) {
                        sortOrder = SortOrder.DESCENDING;
                    } else if (sortOrder == SortOrder.DESCENDING) {
                        sortKeys.remove(i);
                        if (!e.isControlDown()) {
                            sortKeys.clear();
                        }
                        cell.getHeader().getRowSorter().setSortKeys(sortKeys);
                        return;
                    } else {
                        sortOrder = SortOrder.ASCENDING;
                    }
                    sk = new SortKey(modelIndex, sortOrder);
                    if (!e.isControlDown()) {
                        sortKeys.clear();
                        sortKeys.add(sk);
                    } else {
                        sortKeys.set(i, sk);
                    }
                    cell.getHeader().getRowSorter().setSortKeys(sortKeys);
                    return;
                }
            }
            SortKey sk = new SortKey(modelIndex, SortOrder.ASCENDING);
            if (!e.isControlDown()) {
                sortKeys.clear();
            }
            sortKeys.add(sk);
            cell.getHeader().getRowSorter().setSortKeys(sortKeys);
        }
    }
}

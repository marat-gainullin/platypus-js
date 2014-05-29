/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;

/**
 *
 * @author mg
 * @param <T>
 * @param <C>
 */
public abstract class GridColumn<T, C> extends Column<T, C> {

    protected ColumnSortList.ColumnSortInfo sortInfo;
    
    public GridColumn(Cell<C> aCell) {
        super(aCell);
    }

    public ColumnSortList.ColumnSortInfo getSortInfo() {
        return sortInfo;
    }

    public void setSortInfo(ColumnSortList.ColumnSortInfo aValue) {
        sortInfo = aValue;
    }
    
}

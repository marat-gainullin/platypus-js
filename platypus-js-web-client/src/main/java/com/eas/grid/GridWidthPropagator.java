/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 * Propagates summarized width of columns from <col> tags into the <table> tag
 *
 * @author mg
 * @param <T> the data type of each row
 */
public class GridWidthPropagator<T> implements WidthCallback {

    protected AbstractCellTable<T> table;
    protected boolean propagated;

    public GridWidthPropagator(AbstractCellTable<T> aTable) {
        super();
        table = aTable;
    }

    public AbstractCellTable<T> getTable() {
        return table;
    }

    @Override
    public void changed() {
        if (!propagated) {
            double width = 0;
            for (int i = 0; i < table.getColumnCount(); i++) {
                Column<T, ?> col = table.getColumn(i);
                String colWidth = table.getColumnWidth(col);
                if (colWidth != null && colWidth.endsWith("px")) {
                    width += Double.valueOf(colWidth.substring(0, colWidth.length() - 2));
                }
            }
            width += calcWidthBias();
            table.setWidth(width + "px");
        }
    }

    protected double calcWidthBias() {
        return 0;
    }

    public boolean isPropagated() {
        return propagated;
    }

    public void setPropagated(boolean aValue) {
        propagated = aValue;
    }

}

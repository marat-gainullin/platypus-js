/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 *
 * @author mg
 * @param <T> Type of cell table data element.
 */
public class DraggedColumn<T> {

    public static DraggedColumn<?> instance;

    protected final Column<T, ?> column;
    protected final int columnIndex;
    protected AbstractCellTable<T> table;
    protected Element cellElement;
    protected Element targetElement;

    public DraggedColumn(Column<T, ?> aColumn, AbstractCellTable<T> aTable, Element aCellElement, Element aTargetElement) {
        this(aColumn, aTable.getColumnIndex(aColumn), aTable, aCellElement, aTargetElement);
    }

    public DraggedColumn(Column<T, ?> aColumn, int aColumnIndex, AbstractCellTable<T> aTable, Element aCellElement, Element aTargetElement) {
        super();
        column = aColumn;
        columnIndex = aColumnIndex;
        table = aTable;
        cellElement = aCellElement;
        targetElement = aTargetElement;
    }

    public boolean isResize() {
        return targetElement.getClassName().contains(DraggableHeader.headerStyles.gridHeaderResizer());
    }

    public boolean isMove() {
        return !isResize();
    }

    public Column<T, ?> getColumn() {
        return column;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public AbstractCellTable<T> getTable() {
        return table;
    }

    public Element getCellElement() {
        return cellElement;
    }

    public Element getTargetElement() {
        return targetElement;
    }

}

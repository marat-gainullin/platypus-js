package com.eas.grid;

import com.google.gwt.dom.client.Element;

/**
 * Хранилище состояния операции перетаскивания
 * @author mg
 */
public class ColumnDrag {

    public static ColumnDrag instance;

    protected HeaderView header;
    protected GridSection table;
    protected Element cellElement;
    
    protected ColumnDrag(HeaderView aHeader, GridSection aSection, Element aCellElement) {
        super();
        header = aHeader;
        table = aSection;
        cellElement = aCellElement;
    }

    public boolean isResize() {
        return cellElement.getClassName().contains("header-resizer");
    }

    public boolean isMove() {
        return !isResize();
    }

    public HeaderView getHeader() {
        return header;
    }

    public GridSection getSection() {
        return table;
    }

    public Element getCellElement() {
        return cellElement;
    }

}

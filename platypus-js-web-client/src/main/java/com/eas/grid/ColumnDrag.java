package com.eas.grid;

import com.google.gwt.dom.client.Element;

/**
 * Состояние операции перетаскивания
 * @author mg
 */
public class ColumnDrag {

    public static ColumnDrag instance;

    protected HeaderView header;
    protected Element decorationElement;
    
    protected ColumnDrag(HeaderView aHeader, Element aDecorationElement) {
        super();
        header = aHeader;
        decorationElement = aDecorationElement;
    }

    public boolean isResize() {
        return decorationElement.getClassName().contains("header-resizer");
    }

    public boolean isMove() {
        return !isResize();
    }

    public HeaderView getHeader() {
        return header;
    }

    public Element getDecorationElement() {
        return decorationElement;
    }

}

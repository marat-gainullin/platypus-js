package com.eas.grid.columns;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;

public class UsualServiceColumn extends Column {

    public UsualServiceColumn() {
        super();
        width = 22;
        minWidth = width;
        maxWidth = width;
    }

    @Override
    public void render(int viewIndex, JavaScriptObject dataRow, TableCellElement viewCell) {
        // TODO: Add data cursor and data changes driven data rendering
        JavaScriptObject rows = grid.getRowsData();
        boolean currentRow = rows != null && rows.<JsObject>cast().getJs(grid.getCursorProperty()) == value;
        if (currentRow) {
            Element content = Document.get().createDivElement();
            content.setClassName("grid-marker-cell-cursor");
            viewCell.appendChild(content);
        }
        /*
        if (value.isInserted())
                content.appendHtmlConstant("<div class=\"grid-marker-inserted\"></div>");
        else if (value.isUpdated())
                content.appendHtmlConstant("<div class=\"grid-marker-cell-dirty\"></div>");
         */
    }

    @Override
    public JavaScriptObject getValue(JavaScriptObject anElement) {
        return anElement;
    }
}

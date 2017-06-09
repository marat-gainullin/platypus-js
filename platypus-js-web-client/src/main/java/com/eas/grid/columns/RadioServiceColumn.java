package com.eas.grid.columns;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.TableCellElement;

public class RadioServiceColumn extends Column {

    public RadioServiceColumn() {
        super();
        width = 22;
        minWidth = width;
        maxWidth = width;
    }

    @Override
    public void setWidth(double aValue) {
        super.setWidth(aValue);
        minWidth = width;
        maxWidth = width;
    }

    @Override
    public void setMinWidth(double aValue) {
        // no op
    }

    @Override
    public void setMaxWidth(double aValue) {
        // no op
    }

    @Override
    public void render(int viewIndex, JavaScriptObject dataRow, TableCellElement viewCell) {
        // TODO: Add grid.isSelected() driven rendering
    }

    @Override
    public Boolean getValue(JavaScriptObject object) {
        return grid.isSelected(object);
    }
}

package com.eas.grid.columns.header;

import com.eas.grid.HeaderView;
import com.eas.grid.columns.CheckServiceColumn;
import com.eas.ui.Widget;

public class CheckHeaderNode extends HeaderNode {

    public CheckHeaderNode() {
        super();
        column = new CheckServiceColumn();
        header = new HeaderView("\\", this);
        setResizable(false);
    }

    @Override
    public CheckHeaderNode lightCopy() {
        CheckHeaderNode copied = new CheckHeaderNode();
        copied.setColumn(column);
        copied.setHeader(header);
        return copied;
    }

    @Override
    public Widget getEditor() {
        return null;
    }

    @Override
    public void setEditor(Widget aWidget) {
        // no op
    }

}

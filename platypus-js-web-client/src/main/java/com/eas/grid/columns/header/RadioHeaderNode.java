package com.eas.grid.columns.header;

import com.eas.grid.HeaderView;
import com.eas.grid.columns.RadioServiceColumn;
import com.eas.ui.Widget;

public class RadioHeaderNode extends HeaderNode {

    public RadioHeaderNode() {
        super();
        column = new RadioServiceColumn();
        header = new HeaderView("\\", this);
        setResizable(false);
    }

    @Override
    public RadioHeaderNode lightCopy() {
        RadioHeaderNode copied = new RadioHeaderNode();
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

package com.eas.grid.columns.header;

import com.eas.grid.HeaderView;
import com.eas.grid.columns.UsualServiceColumn;
import com.eas.ui.Widget;

public class ServiceHeaderNode extends HeaderNode {

    public ServiceHeaderNode() {
        super();
        column = new UsualServiceColumn();
        header = new HeaderView("\\", this);
        setResizable(false);
    }

    @Override
    public ServiceHeaderNode lightCopy() {
        ServiceHeaderNode copied = new ServiceHeaderNode();
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

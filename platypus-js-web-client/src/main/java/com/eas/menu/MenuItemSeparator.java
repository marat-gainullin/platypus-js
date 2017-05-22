package com.eas.menu;

import com.eas.core.HasPublished;
import com.eas.ui.Widget;
import com.google.gwt.core.client.JavaScriptObject;

public class MenuItemSeparator extends MenuItem {

    public MenuItemSeparator() {
        super();
        element.setClassName("menu-separator");
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
    }-*/;
}

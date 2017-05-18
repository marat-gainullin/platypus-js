package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.PublishedAbsoluteConstraints;
import com.eas.ui.Widget;
import com.eas.widgets.containers.AnchorsContainer;
import com.google.gwt.core.client.JavaScriptObject;

public class Absolute extends AnchorsContainer {

    public Absolute() {
        super();
    }

    @Override
    public void add(Widget aChild, PublishedAbsoluteConstraints aConstraints) {
        super.add(aChild, aConstraints != null ? aConstraints : PublishedAbsoluteConstraints.createDefaultAnchors());
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        published.add = function(toAdd, aConstraints) {
            if(toAdd && toAdd.unwrap) {
                if(toAdd.parent == published)
                    throw 'A widget already added to this container';
                if(!aConstraints){
                    aConstraints = {left: toAdd.left, top: toAdd.top, width: toAdd.width, height: toAdd.height};
                }
                aWidget.@com.eas.widgets.AbsolutePane::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/ui/PublishedAbsoluteConstraints;)(toAdd.unwrap(), aConstraints || null);
            }
        };
    }-*/;
}

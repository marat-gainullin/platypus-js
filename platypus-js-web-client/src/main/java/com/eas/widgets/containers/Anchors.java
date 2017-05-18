package com.eas.widgets.containers;

import com.eas.core.HasPublished;
import com.eas.ui.PublishedMarginConstraints;
import com.eas.ui.Widget;
import com.eas.widgets.containers.AnchorsContainer;
import com.google.gwt.core.client.JavaScriptObject;

public class Anchors extends AnchorsContainer {

    public Anchors() {
        super();
    }

    @Override
    public void add(Widget aChild, PublishedMarginConstraints aConstraints) {
        super.add(aChild, aConstraints != null ? aConstraints : PublishedMarginConstraints.createDefaultAnchors());
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
                aWidget.@com.eas.widgets.AnchorsPane::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/eas/ui/PublishedMarginConstraints;)(toAdd.unwrap(), aConstraints || null);
            }
        };
    }-*/;
}

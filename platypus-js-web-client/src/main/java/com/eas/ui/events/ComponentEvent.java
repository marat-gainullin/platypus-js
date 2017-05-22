package com.eas.ui.events;

import com.eas.ui.Widget;

public class ComponentEvent extends Event {

    private final Widget widget;

    /**
     * Creates a new close event.
     *
     * @param w the target
     */
    public ComponentEvent(Widget w) {
        super(w, w);
        widget = w;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public Widget getWidget() {
        return widget;
    }
}

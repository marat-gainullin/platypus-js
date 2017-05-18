package com.eas.ui.events;

import com.eas.ui.Widget;

public class ShowEvent extends Event {

    private final Widget widget;

    /**
     * Creates a new close event.
     *
     * @param w the target
     */
    public ShowEvent(Widget w) {
        super(w);
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

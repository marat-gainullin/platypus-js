package com.eas.ui.events;

import com.eas.ui.Widget;

public class ActionEvent extends Event {

    /**
     * Creates a new action event.
     *
     * @param aWidget the target
     */
    public ActionEvent(Widget aWidget) {
        super(aWidget, aWidget);
    }

}

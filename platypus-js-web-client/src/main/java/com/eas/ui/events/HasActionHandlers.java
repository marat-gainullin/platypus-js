package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasActionHandlers {

    /**
     * Adds a {@link ActionEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addActionHandler(ActionHandler handler);
}

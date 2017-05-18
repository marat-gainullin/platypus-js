package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasAddHandlers {

    /**
     * Adds a {@link AddEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addAddHandler(AddHandler handler);
}

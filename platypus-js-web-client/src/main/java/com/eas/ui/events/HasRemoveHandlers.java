package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasRemoveHandlers {

    /**
     * Adds a {@link RemoveEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRemoveHandler(RemoveHandler handler);
}

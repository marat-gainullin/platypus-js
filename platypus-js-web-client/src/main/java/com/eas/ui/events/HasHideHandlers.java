package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasHideHandlers {

    /**
     * Adds a {@link HideEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addHideHandler(HideHandler handler);
}

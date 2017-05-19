package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasDeactivateHandlers {

    /**
     * Adds a {@link DeactivateEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addDeactivateHandler(DeactivateHandler handler);
}

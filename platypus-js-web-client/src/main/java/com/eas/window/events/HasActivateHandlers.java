package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasActivateHandlers {

    /**
     * Adds a {@link ActivateEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addActivateHandler(ActivateHandler handler);
}

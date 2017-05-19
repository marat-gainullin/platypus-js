package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasClosedHandlers {

    /**
     * Adds a {@link ClosedEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addClosedHandler(ClosedHandler handler);
}

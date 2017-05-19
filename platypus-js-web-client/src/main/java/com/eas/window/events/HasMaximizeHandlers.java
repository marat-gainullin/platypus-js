package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasMaximizeHandlers {

    /**
     * Adds a {@link MaximizeEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addMaximizeHandler(MaximizeHandler handler);
}

package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasMinimizeHandlers {

    /**
     * Adds a {@link MinimizeEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addMinimizeHandler(MinimizeHandler handler);
}

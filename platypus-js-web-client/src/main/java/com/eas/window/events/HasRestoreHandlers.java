package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasRestoreHandlers {

    /**
     * Adds a {@link RestoreEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addRestoreHandler(RestoreHandler handler);
}

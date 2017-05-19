package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mg
 */
public interface HasMoveHandlers {

    /**
     * Adds a {@link MoveEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addMoveHandler(MoveHandler handler);
}

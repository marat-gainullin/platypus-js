package com.eas.window.events;

import com.eas.ui.events.Event;

/**
 * Represents a closed event.
 *
 * @author mg
 */
public class OpenEvent<T> extends Event {

    /**
     * Creates a new closed event.
     *
     * @param target the target
     */
    public OpenEvent(T target) {
        super(target, target);
    }
}

package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a close event.
 *
 * @author mg
 */
public class DeactivateEvent extends Event<WindowPanel> {

    /**
     * Creates a new deactivate event.
     *
     * @param target the target
     */
    public DeactivateEvent(WindowPanel target) {
        super(target, target);
    }

}

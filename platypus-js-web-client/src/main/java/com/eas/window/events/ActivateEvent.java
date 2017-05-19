package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a close event.
 *
 * @author mg
 */
public class ActivateEvent extends Event<WindowPanel> {

    /**
     * Creates a new activate event.
     *
     * @param target the target
     */
    public ActivateEvent(WindowPanel target) {
        super(target, target);
    }
}

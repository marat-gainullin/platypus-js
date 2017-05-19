package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a close event.
 *
 * @author mg
 */
public class RestoreEvent extends Event<WindowPanel> {

    /**
     * Creates a new restore event.
     *
     * @param target the target
     */
    public RestoreEvent(WindowPanel target) {
        super(target, target);
    }

}

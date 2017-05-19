package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a close event.
 *
 * @author mg
 */
public class MinimizeEvent extends Event<WindowPanel> {

    /**
     * Creates a new minimize event.
     *
     * @param target the target
     */
    public MinimizeEvent(WindowPanel target) {
        super(target, target);
    }

}

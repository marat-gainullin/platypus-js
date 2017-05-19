package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a maximize event.
 *
 * @author mg
 */
public class MaximizeEvent extends Event<WindowPanel> {

    /**
     * Creates a new maximize event.
     *
     * @param target the target
     */
    public MaximizeEvent(WindowPanel target) {
        super(target, target);
    }

}

package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a close event.
 *
 * @author mg
 */
public class BeforeCloseEvent extends Event<WindowPanel> {

    protected boolean cancelled;

    /**
     * Creates a new before close event.
     *
     * @param target the target
     */
    public BeforeCloseEvent(WindowPanel target) {
        super(target, target);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean aValue) {
        cancelled = aValue;
    }
}

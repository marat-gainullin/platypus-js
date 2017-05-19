package com.eas.window.events;

import com.eas.ui.events.Event;
import com.eas.window.WindowPanel;

/**
 * Represents a move event.
 *
 * @author mg
 */
public class MoveEvent extends Event<WindowPanel> {

    private final double x;
    private final double y;

    /**
     * Creates a new move event.
     *
     * @param target the target
     */
    public MoveEvent(WindowPanel target, double aX, double aY) {
        super(target, target);
        x = aX;
        y = aY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

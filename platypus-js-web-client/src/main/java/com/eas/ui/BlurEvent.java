package com.eas.ui;

import com.eas.ui.events.Event;

/**
 *
 * @author mgainullin
 */
public class BlurEvent extends Event {

    public BlurEvent(Widget target) {
        super(target, target);
    }

}

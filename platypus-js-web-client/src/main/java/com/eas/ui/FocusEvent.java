package com.eas.ui;

import com.eas.ui.events.Event;

/**
 *
 * @author mgainullin
 */
public class FocusEvent extends Event {

    public FocusEvent(Widget target) {
        super(target, target);
    }

}

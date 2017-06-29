package com.eas.ui.events;

import com.eas.ui.Widget;
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

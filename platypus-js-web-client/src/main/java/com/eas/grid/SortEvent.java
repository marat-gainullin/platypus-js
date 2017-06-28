package com.eas.grid;

import com.eas.ui.events.Event;

/**
 *
 * @author mgainullin
 */
public class SortEvent extends Event {

    public SortEvent(Grid aTarget) {
        super(aTarget, aTarget);
    }

}

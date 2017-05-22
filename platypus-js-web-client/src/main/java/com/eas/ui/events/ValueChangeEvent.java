package com.eas.ui.events;

import com.eas.ui.Widget;

/**
 *
 * @author mgainullin
 */
public class ValueChangeEvent extends Event {

    protected Object oldValue;
    protected Object newValue;

    public ValueChangeEvent(Widget source, Object oldValue, Object newValue) {
        super(source, source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

}

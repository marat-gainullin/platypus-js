package com.eas.ui.events;

import com.google.gwt.dom.client.NativeEvent;

/**
 *
 */
public class Event<T> {

    private NativeEvent event;

    protected Object source;
    private final T target;

    public Event(Object source, T target) {
        this(source, target, null);
    }
    
    public Event(Object source, T target, NativeEvent event) {
        super();
        this.source = source;
        this.target = target;
        this.event = event;
    }

    public NativeEvent getEvent() {
        return event;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public T getTarget() {
        return target;
    }
}

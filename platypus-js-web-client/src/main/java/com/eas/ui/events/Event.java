package com.eas.ui.events;

/**
 *
 */
public class Event<T> {

    protected Object source;
    private final T target;

    public Event(Object source, T target) {
        super();
        this.source = source;
        this.target = target;
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

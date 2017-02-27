/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a close event.
 *
 * @param <T> the type being closed
 * @author mg
 */
public class BeforeCloseEvent<T> extends GwtEvent<BeforeCloseHandler<T>> {

    /**
     * Handler type.
     */
    private static Type<BeforeCloseHandler<?>> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     * @return Whether event's handler has cnacelled event been fired
     */
    public static <T> boolean fire(HasBeforeCloseHandlers<T> source, T target) {
        if (TYPE != null) {
            BeforeCloseEvent<T> event = new BeforeCloseEvent<>(target);
            source.fireEvent(event);
            return event.isCancelled();
        } else {
            return false;
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<BeforeCloseHandler<?>> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final T target;
    protected boolean cancelled;

    /**
     * Creates a new before close event.
     *
     * @param target the target
     */
    protected BeforeCloseEvent(T target) {
        this.target = target;
    }

    // The instance knows its of type T, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @SuppressWarnings("unchecked")
    @Override
    public final Type<BeforeCloseHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public T getTarget() {
        return target;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean aValue) {
        cancelled = aValue;
    }

    @Override
    protected void dispatch(BeforeCloseHandler<T> handler) {
        handler.onBeforeClose(this);
    }
}

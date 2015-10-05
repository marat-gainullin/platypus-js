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
 * @param <T> the type being activated
 * @author mg
 */
public class ActivateEvent<T> extends GwtEvent<ActivateHandler<T>> {

    /**
     * Handler type.
     */
    private static Type<ActivateHandler<?>> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static <T> void fire(HasActivateHandlers<T> source, T target) {
        if (TYPE != null) {
            ActivateEvent<T> event = new ActivateEvent<>(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<ActivateHandler<?>> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final T target;

    /**
     * Creates a new activate event.
     *
     * @param target the target
     */
    protected ActivateEvent(T target) {
        this.target = target;
    }

    // The instance knows its of type T, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @SuppressWarnings("unchecked")
    @Override
    public final Type<ActivateHandler<T>> getAssociatedType() {
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

    @Override
    protected void dispatch(ActivateHandler<T> handler) {
        handler.onActivate(this);
    }
}

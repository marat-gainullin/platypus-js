package com.eas.ui.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.UIObject;

public class RemoveEvent extends GwtEvent<RemoveHandler>{

    /**
     * Handler type.
     */
    private static Type<RemoveHandler> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static void fire(HasRemoveHandlers source, UIObject target) {
        if (TYPE != null) {
            RemoveEvent event = new RemoveEvent(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<RemoveHandler> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final UIObject widget;

    /**
     * Creates a new close event.
     *
     * @param aWidget the target
     */
    protected RemoveEvent(UIObject aWidget) {
        widget = aWidget;
    }

    @Override
    public final Type<RemoveHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public UIObject getWidget() {
        return widget;
    }

    @Override
    protected void dispatch(RemoveHandler handler) {
        handler.onRemove(this);
    }
}

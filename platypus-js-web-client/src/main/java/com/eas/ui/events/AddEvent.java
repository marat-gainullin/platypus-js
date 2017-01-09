package com.eas.ui.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.UIObject;

public class AddEvent extends GwtEvent<AddHandler>{

    /**
     * Handler type.
     */
    private static Type<AddHandler> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static void fire(HasAddHandlers source, UIObject target) {
        if (TYPE != null) {
            AddEvent event = new AddEvent(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<AddHandler> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final UIObject widget;

    /**
     * Creates a new close event.
     *
     * @param aWidget the target
     */
    protected AddEvent(UIObject aWidget) {
        widget = aWidget;
    }

    @Override
    public final Type<AddHandler> getAssociatedType() {
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
    protected void dispatch(AddHandler handler) {
        handler.onAdd(this);
    }
}

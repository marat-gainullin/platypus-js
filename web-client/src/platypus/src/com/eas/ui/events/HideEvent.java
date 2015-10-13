package com.eas.ui.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.UIObject;

public class HideEvent extends GwtEvent<HideHandler>{

    /**
     * Handler type.
     */
    private static Type<HideHandler> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static void fire(HasHideHandlers source, UIObject target) {
        if (TYPE != null) {
            HideEvent event = new HideEvent(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<HideHandler> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final UIObject widget;

    /**
     * Creates a new close event.
     *
     * @param aWidget the target
     */
    protected HideEvent(UIObject aWidget) {
        widget = aWidget;
    }

    @Override
    public final Type<HideHandler> getAssociatedType() {
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
    protected void dispatch(HideHandler handler) {
        handler.onHide(this);
    }
}

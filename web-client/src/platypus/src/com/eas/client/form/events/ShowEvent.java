package com.eas.client.form.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class ShowEvent extends GwtEvent<ShowHandler>{

    /**
     * Handler type.
     */
    private static Type<ShowHandler> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static void fire(HasShowHandlers source, Widget target) {
        if (TYPE != null) {
            ShowEvent event = new ShowEvent(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<ShowHandler> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final Widget widget;

    /**
     * Creates a new close event.
     *
     * @param aWidget the target
     */
    protected ShowEvent(Widget aWidget) {
        widget = aWidget;
    }

    @Override
    public final Type<ShowHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public Widget getWidget() {
        return widget;
    }

    @Override
    protected void dispatch(ShowHandler handler) {
        handler.onShow(this);
    }
}

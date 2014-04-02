package com.eas.client.form.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ActionEvent extends GwtEvent<ActionHandler>{

    /**
     * Handler type.
     */
    private static Type<ActionHandler> TYPE;

    /**
     * Fires a close event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     *
     * @param <T> the target type
     * @param source the source of the handlers
     * @param target the target
     */
    public static void fire(HasActionHandlers source, Widget target) {
        if (TYPE != null) {
            ActionEvent event = new ActionEvent(target);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<ActionHandler> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<>());
    }

    private final UIObject target;

    /**
     * Creates a new close event.
     *
     * @param aSource the target
     */
    protected ActionEvent(UIObject aSource) {
    	super();
    	setSource(aSource);
        target = aSource;
    }

    @Override
    public final Type<ActionHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the target.
     *
     * @return the target
     */
    public UIObject getTarget() {
        return target;
    }

    @Override
    protected void dispatch(ActionHandler handler) {
        handler.onAction(this);
    }
}

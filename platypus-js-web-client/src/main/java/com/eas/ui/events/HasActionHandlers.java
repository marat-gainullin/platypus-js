package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasActionHandlers extends HasHandlers {

	/**
	 * Adds a {@link ActionEvent} handler.
	 * 
	 * @param handler
	 *            the handler
	 * @return the registration for the event
	 */
	HandlerRegistration addActionHandler(ActionHandler handler);
}

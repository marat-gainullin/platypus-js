package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAddHandlers extends HasHandlers {

	/**
	 * Adds a {@link AddEvent} handler.
	 * 
	 * @param handler
	 *            the handler
	 * @return the registration for the event
	 */
	HandlerRegistration addAddHandler(AddHandler handler);
}

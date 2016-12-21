package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasHideHandlers extends HasHandlers {

	/**
	 * Adds a {@link HideEvent} handler.
	 * 
	 * @param handler
	 *            the handler
	 * @return the registration for the event
	 */
	HandlerRegistration addHideHandler(HideHandler handler);
}

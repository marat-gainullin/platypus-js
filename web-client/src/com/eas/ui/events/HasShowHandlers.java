package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasShowHandlers extends HasHandlers {

	/**
	 * Shows a {@link ShowEvent} handler.
	 * 
	 * @param handler
	 *            the handler
	 * @return the registration for the event
	 */
	HandlerRegistration addShowHandler(ShowHandler handler);
}

package com.bearsoft.gwt.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasCollapsedHandlers<T> {

	public HandlerRegistration addCollapsedHandler(final CollapsedHandler<T> aHandler);

}

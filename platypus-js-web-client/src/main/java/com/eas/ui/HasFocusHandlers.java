package com.eas.ui;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mgainullin
 */
public interface HasFocusHandlers {

    HandlerRegistration addFocusHandler(FocusHandler handler);
}

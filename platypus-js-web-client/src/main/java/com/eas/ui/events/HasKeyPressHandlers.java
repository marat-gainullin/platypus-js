package com.eas.ui.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mgainullin
 */
public interface HasKeyPressHandlers {

    HandlerRegistration addKeyPressHandler(KeyPressHandler handler);
}

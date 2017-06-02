package com.eas.ui;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author mgainullin
 */
public interface HasBlurHandlers {

    HandlerRegistration addBlurHandler(BlurHandler handler);
}

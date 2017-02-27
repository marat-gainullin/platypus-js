/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 *
 * @author mg
 * @param <T> Type being minimized
 */
public interface HasMinimizeHandlers<T> extends HasHandlers {

    /**
     * Adds a {@link MinimizeEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addMinimizeHandler(MinimizeHandler<T> handler);
}

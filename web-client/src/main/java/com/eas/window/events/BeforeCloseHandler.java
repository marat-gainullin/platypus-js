/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.window.events;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author mg
 * @param <T> Type being closed
 */
public interface BeforeCloseHandler<T> extends EventHandler {

    public void onBeforeClose(BeforeCloseEvent<T> anEvent);
}

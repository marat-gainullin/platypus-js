/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.client.forms.api.Component;

/**
 *
 * @author mg
 */
public class WindowEvent extends Event<java.awt.event.WindowEvent> {

    protected WindowEvent(java.awt.event.WindowEvent aEvent) {
        super(aEvent);
    }

    @Override
    public Component<?> getSource() {
        return super.getSource();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WindowEvent extends Event<java.awt.event.WindowEvent> {

    protected WindowEvent(java.awt.event.WindowEvent aEvent) {
        super(aEvent);
    }

    @Override
    public JSObject getPublished() {
        return published;
    }
}

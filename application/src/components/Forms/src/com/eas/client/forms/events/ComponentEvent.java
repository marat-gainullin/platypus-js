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
public class ComponentEvent extends Event<java.awt.event.ComponentEvent> {

    protected ComponentEvent(java.awt.event.ComponentEvent aEvent) {
        super(aEvent);
    }

    @Override
    public JSObject getPublished() {
        return published;
    }
}

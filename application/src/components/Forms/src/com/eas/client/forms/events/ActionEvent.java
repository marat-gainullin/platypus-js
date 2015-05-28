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
public class ActionEvent extends Event<java.awt.event.ActionEvent> {

    protected ActionEvent(java.awt.event.ActionEvent aEvent) {
        super(aEvent);
    }

    @Override
    public JSObject getPublished() {
        return published;
    }

}

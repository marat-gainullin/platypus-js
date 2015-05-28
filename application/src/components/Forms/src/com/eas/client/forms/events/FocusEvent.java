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
public class FocusEvent extends Event<java.awt.event.FocusEvent> {

    protected FocusEvent(java.awt.event.FocusEvent aEvent) {
        super(aEvent);
    }

    /*
     * There is no analogs for browser client. Should be uncommented when they will.
     * 
     * 
     public Component<?> getComponent() {
     return delegate.getComponent() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getComponent()) : null;
     }

     public Component<?> getOppositeComponent() {
     return delegate.getOppositeComponent() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getOppositeComponent()) : null;
     }
     */
    @Override
    public JSObject getPublished() {
        return published;
    }
}

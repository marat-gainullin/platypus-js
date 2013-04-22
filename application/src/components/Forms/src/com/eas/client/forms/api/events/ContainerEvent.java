/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.client.forms.api.Component;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public class ContainerEvent extends Event<java.awt.event.ContainerEvent> {

    protected ContainerEvent(java.awt.event.ContainerEvent aEvent) {
        super(aEvent);
    }

    public Component<?> getChild() {
        return delegate.getChild() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getChild()) : null;
    }
}

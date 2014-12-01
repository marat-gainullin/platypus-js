/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.client.forms.api.events.EventsWrapper;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public class FormEventsIProxy extends ControlEventsIProxy {

    public FormEventsIProxy() {
        super();
    }

    public FormEventsIProxy(JComponent aHandlee) {
        super();
        setHandlee(aHandlee);
    }

    @Override
    protected Object executeEvent(int aEventId, Object anEvent) {
        if (anEvent instanceof java.awt.event.MouseEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.MouseEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.KeyEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.KeyEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.FocusEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.FocusEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ContainerEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ContainerEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ComponentEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ComponentEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.ActionEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.ActionEvent) anEvent);
        } else if (anEvent instanceof javax.swing.event.ChangeEvent) {
            anEvent = EventsWrapper.wrap((javax.swing.event.ChangeEvent) anEvent);
        }
        return super.executeEvent(aEventId, anEvent);
    }
}

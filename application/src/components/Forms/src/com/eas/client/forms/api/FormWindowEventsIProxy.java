/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.client.forms.FormRunnerPrototype;
import com.eas.client.forms.api.events.EventsWrapper;
import com.eas.controls.FormEventsExecutor;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.controls.events.WindowEventsIProxy;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class FormWindowEventsIProxy extends WindowEventsIProxy {

    public FormWindowEventsIProxy(FormEventsExecutor aExecutor) {
        super(aExecutor);
    }

    @Override
    protected FormEventsExecutor lookupExecutor(Scriptable aScope) {
        return FormRunnerPrototype.lookupFormEventsExecutor(aScope);
    }

    @Override
    protected Object executeEvent(int aEventId, Object anEvent) {
        if (anEvent instanceof java.awt.event.MouseEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.MouseEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.WindowEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.WindowEvent) anEvent);
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

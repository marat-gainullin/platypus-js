/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events.rt;

import com.eas.client.forms.Form;
import com.eas.client.forms.events.EventsWrapper;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class FormWindowEventsIProxy extends WindowEventsIProxy {

    protected Form form;

    public FormWindowEventsIProxy(Form aForm) {
        super();
        form = aForm;
    }

    @Override
    protected Object executeEvent(int aEventId, Object anEvent) {
        if (anEvent instanceof java.awt.event.MouseEvent) {
            anEvent = EventsWrapper.wrap((java.awt.event.MouseEvent) anEvent);
        } else if (anEvent instanceof java.awt.event.WindowEvent) {
            if (eventThis == null) {
                setEventThis((JSObject) form.getPublished());
            }
            ((java.awt.event.WindowEvent) anEvent).setSource(form);
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

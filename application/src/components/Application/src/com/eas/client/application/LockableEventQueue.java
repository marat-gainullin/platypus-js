/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.forms.Form;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.ScriptUtils;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 *
 * @author mg
 */
class LockableEventQueue extends EventQueue {

    public LockableEventQueue() {
        super();
    }

    @Override
    protected void dispatchEvent(AWTEvent aEvent) {
        if (aEvent.getSource() instanceof Component) {
            Component source = (Component) aEvent.getSource();
            Component window = SwingUtilities.getRoot(source);
            if (window instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer) window).getRootPane();
                Object principal = root.getClientProperty(Form.PRINCIPAL_KEY);
                Object lock = root.getClientProperty(Form.LOCK_KEY) != null ? root.getClientProperty(Form.LOCK_KEY) : this;
                ScriptUtils.setLock(lock);
                PlatypusPrincipal.setInstance((PlatypusPrincipal) principal);
                try {
                    synchronized (lock) {
                        super.dispatchEvent(aEvent);
                    }
                } finally {
                    PlatypusPrincipal.setInstance(null);
                    ScriptUtils.setLock(null);
                }
            } else {
                super.dispatchEvent(aEvent);
            }
        } else {
            super.dispatchEvent(aEvent);
        }
    }

}

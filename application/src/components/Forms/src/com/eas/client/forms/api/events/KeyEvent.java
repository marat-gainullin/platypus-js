/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

/**
 *
 * @author mg
 */
public class KeyEvent extends Event<java.awt.event.KeyEvent> {

    protected KeyEvent(java.awt.event.KeyEvent aDelegate) {
        super(aDelegate);
    }

    public int getKey() {
        return delegate.getKeyCode();
    }

    public boolean isAltDown() {
        return delegate.isAltDown() || delegate.isAltGraphDown();
    }

    public boolean isControlDown() {
        return delegate.isControlDown();
    }

    public boolean isShiftDown() {
        return delegate.isShiftDown();
    }

    public boolean isMetaDown() {
        return delegate.isMetaDown();
    }
}

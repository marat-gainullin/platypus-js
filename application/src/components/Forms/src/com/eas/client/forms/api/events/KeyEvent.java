/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class KeyEvent extends Event<java.awt.event.KeyEvent> {

    protected KeyEvent(java.awt.event.KeyEvent aDelegate) {
        super(aDelegate);
    }

    @ScriptFunction(jsDoc = "Virtual key code.")
    public int getKey() {
        return delegate.getKeyCode();
    }

    @ScriptFunction(jsDoc = "Alt key")
    public boolean isAltDown() {
        return delegate.isAltDown() || delegate.isAltGraphDown();
    }

    @ScriptFunction(jsDoc = "Ctrl key")
    public boolean isControlDown() {
        return delegate.isControlDown();
    }

    @ScriptFunction(jsDoc = "Shift key")
    public boolean isShiftDown() {
        return delegate.isShiftDown();
    }

    @ScriptFunction(jsDoc = "Meta key")
    public boolean isMetaDown() {
        return delegate.isMetaDown();
    }
}

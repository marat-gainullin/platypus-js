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
public class MouseEvent extends Event<java.awt.event.MouseEvent> {

    protected MouseEvent(java.awt.event.MouseEvent aDelegate) {
        super(aDelegate);
    }

    @ScriptFunction(jsDoc = "X cursor coordinate in component's space.")
    public int getX() {
        return delegate.getX();
    }

    @ScriptFunction(jsDoc = "Y cursor coordinate in component's space.")
    public int getY() {
        return delegate.getY();
    }

    @ScriptFunction(jsDoc = "Left or right or another button is pressed.")
    public int getButton() {
        return delegate.getButton();
    }

    @ScriptFunction(jsDoc = "Clicks count.")
    public int getClickCount() {
        return delegate.getClickCount();
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

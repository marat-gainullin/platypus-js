/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

/**
 *
 * @author mg
 */
public class MouseEvent extends Event<java.awt.event.MouseEvent> {

    protected MouseEvent(java.awt.event.MouseEvent aDelegate) {
        super(aDelegate);
    }

    public int getX() {
        return delegate.getX();
    }

    public int getY() {
        return delegate.getY();
    }

    public int getButton() {
        return delegate.getButton();
    }

    public int getClickCount() {
        return delegate.getClickCount();
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

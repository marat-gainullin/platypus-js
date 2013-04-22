/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls;

import com.eas.store.Serial;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class SerialKeyStroke {
    
    protected KeyStroke delegate;

    public SerialKeyStroke() {
        delegate = KeyStroke.getKeyStroke(0, 0);
    }

    public SerialKeyStroke(KeyStroke aDelegate) {
        super();
        delegate = aDelegate;
    }

    public KeyStroke getDelegate() {
        return delegate;
    }

    @Serial
    public int getKeyCode()
    {
        return delegate.getKeyCode();
    }

    @Serial
    public void setKeyCode(int aCode)
    {
        delegate = KeyStroke.getKeyStroke(aCode, delegate.getModifiers(), delegate.isOnKeyRelease());
    }

    @Serial
    public int getModifiers()
    {
        return delegate.getModifiers();
    }

    @Serial
    public void setModifiers(int aModifiers)
    {
        delegate = KeyStroke.getKeyStroke(delegate.getKeyCode(), aModifiers, delegate.isOnKeyRelease());
    }

    @Serial
    public boolean isOnKeyRelease()
    {
        return delegate.isOnKeyRelease();
    }
    @Serial
    public void setOnKeyRelease(boolean aOnKeyRelease)
    {
        delegate = KeyStroke.getKeyStroke(delegate.getKeyCode(), delegate.getModifiers(), aOnKeyRelease);
    }

}

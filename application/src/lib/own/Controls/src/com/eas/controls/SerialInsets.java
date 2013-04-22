/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.store.Serial;
import java.awt.Insets;

/**
 *
 * @author mg
 */
public class SerialInsets {

    protected Insets delegate;

    public SerialInsets() {
        delegate = new Insets(0, 0, 0, 0);
    }

    public SerialInsets(Insets aDelegate) {
        super();
        delegate = aDelegate;
    }

    public Insets getDelegate() {
        return delegate;
    }

    @Serial
    public int getTop() {
        return delegate.top;
    }

    @Serial
    public void setTop(int aValue) {
        delegate.top = aValue;
    }

    @Serial
    public int getBottom() {
        return delegate.bottom;
    }

    @Serial
    public void setBottom(int aValue) {
        delegate.bottom = aValue;
    }

    @Serial
    public int getLeft() {
        return delegate.left;
    }

    @Serial
    public void setLeft(int aValue) {
        delegate.left = aValue;
    }

    @Serial
    public int getRight() {
        return delegate.right;
    }

    @Serial
    public void setRight(int aValue) {
        delegate.right = aValue;
    }
}

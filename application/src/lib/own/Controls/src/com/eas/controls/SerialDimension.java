/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.controls;

import com.eas.store.Serial;
import java.awt.Dimension;

/**
 *
 * @author mg
 */
public class SerialDimension {

    protected Dimension delegate;

    public SerialDimension() {
        delegate = new Dimension(0, 0);
    }

    public SerialDimension(Dimension aDelegate) {
        super();
        delegate = aDelegate;
    }

    public Dimension getDelegate() {
        return delegate;
    }

    @Serial
    public int getWidth()
    {
        return delegate.width;
    }

    @Serial
    public void setWidth(int aValue)
    {
        delegate.width = aValue;
    }

    @Serial
    public int getHeight()
    {
        return delegate.height;
    }

    @Serial
    public void setHeight(int aValue)
    {
        delegate.height = aValue;
    }
}

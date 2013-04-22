/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.store;

import java.awt.Color;

/**
 *
 * @author mg
 */
public class SerialColor {

    protected Color delegate = null;

    public SerialColor()
    {
        super();
        delegate = Color.white;
    }

    public SerialColor(Color aDelegate)
    {
        super();
        delegate = aDelegate;
    }

    public Color getDelegate() {
        return delegate;
    }

    @Serial
    public int getRGB()
    {
        return delegate != null?delegate.getRGB():0;
    }

    @Serial
    public void setRGB(int aRgb)
    {
        delegate = new Color(aRgb, true);
    }
}

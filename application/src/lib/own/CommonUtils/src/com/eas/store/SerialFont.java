/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.store;

import java.awt.Font;

/**
 *
 * @author mg
 */
public class SerialFont
{
    protected Font delegate = null;

    public SerialFont()
    {
        super();
        delegate = new Font(Font.MONOSPACED, 0, 12);
    }

    public SerialFont(Font aDelegate)
    {
        super();
        delegate = aDelegate;
    }

    public Font getDelegate() {
        return delegate;
    }

    @Serial
    public String getName() {
        return delegate.getName();
    }

    @Serial
    public void setName(String aName)
    {
        delegate = new Font(aName, delegate.getStyle(), delegate.getSize());
    }

    @Serial
    public int getStyle()
    {
        return delegate.getStyle();
    }

    @Serial
    public void setStyle(int style)
    {
        delegate = new Font(delegate.getName(), style, delegate.getSize());
    }

    @Serial
    public int getSize()
    {
        return delegate.getSize();
    }

    @Serial
    public void setSize(int size)
    {
        delegate = new Font(delegate.getName(), delegate.getStyle(), size);
    }
}
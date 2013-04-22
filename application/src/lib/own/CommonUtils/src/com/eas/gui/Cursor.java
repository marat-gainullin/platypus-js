/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

/**
 *
 * @author mg
 */
public class Cursor {
    
    public static final Cursor CROSSHAIR = new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR);
    public static final Cursor DEFAULT = new Cursor(java.awt.Cursor.DEFAULT_CURSOR);
    public static final Cursor AUTO = DEFAULT;
    public static final Cursor E_RESIZE = new Cursor(java.awt.Cursor.E_RESIZE_CURSOR);
    public static final Cursor HAND = new Cursor(java.awt.Cursor.HAND_CURSOR);
    public static final Cursor MOVE = new Cursor(java.awt.Cursor.MOVE_CURSOR);
    public static final Cursor NE_RESIZE = new Cursor(java.awt.Cursor.NE_RESIZE_CURSOR);
    public static final Cursor NW_RESIZE = new Cursor(java.awt.Cursor.NW_RESIZE_CURSOR);
    public static final Cursor N_RESIZE = new Cursor(java.awt.Cursor.N_RESIZE_CURSOR);
    public static final Cursor SE_RESIZE = new Cursor(java.awt.Cursor.SE_RESIZE_CURSOR);
    public static final Cursor SW_RESIZE = new Cursor(java.awt.Cursor.SW_RESIZE_CURSOR);
    public static final Cursor S_RESIZE = new Cursor(java.awt.Cursor.S_RESIZE_CURSOR);
    public static final Cursor TEXT = new Cursor(java.awt.Cursor.TEXT_CURSOR);
    public static final Cursor WAIT = new Cursor(java.awt.Cursor.WAIT_CURSOR);
    public static final Cursor W_RESIZE = new Cursor(java.awt.Cursor.W_RESIZE_CURSOR);
    
    protected java.awt.Cursor delegate;
    
    protected Cursor(java.awt.Cursor aDelegate)
    {
        delegate = aDelegate;
    }
    
    protected Cursor(int aCursorType)
    {
        delegate = new java.awt.Cursor(aCursorType);
    }
    
    public java.awt.Cursor unwrap()
    {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.getName();
    }
}

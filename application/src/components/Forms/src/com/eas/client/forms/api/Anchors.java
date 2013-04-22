/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

/**
 *
 * @author mg
 */
public class Anchors {

    public Anchors(Object aLeft, Object aWidth, Object aRight,
            Object aTop, Object aHeight, Object aBottom)
    {
        super();
        left = aLeft;
        width = aWidth;
        right = aRight;
        top = aTop;
        height = aHeight;
        bottom = aBottom;
    }
    
    public Object left, width, right;
    public Object top, height, bottom;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class Anchors {

    @ScriptFunction(params = {"left", "width", "right", "top", "height", "bottom"}, jsDoc = "/**\n"
            + "* Component's binding anchors for anchors layout.\n"
            + "* Two anchor binding of three possible must be provided for X and Y axis, other constraints must be set to <code>null</code>."
            + "* Parameters values can be provided in pixels, for this set values as a <code>Number</code> or <code>string</code> with 'px' suffix, e.g. '30px'.\n"
            + "* Also they can be provided in procents with usage of '%' suffixes, e.g. '10%'.\n"
            + "* @param left a left anchor\n"
            + "* @param width a width value\n"
            + "* @param right a right anchor\n"
            + "* @param top a top anchor\n"
            + "* @param height a height value\n"
            + "* @param bottom a bottom anchor\n"
            + "*/")
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

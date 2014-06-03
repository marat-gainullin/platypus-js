/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Anchors implements HasPublished {

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
            Object aTop, Object aHeight, Object aBottom) {
        super();
        left = aLeft;
        width = aWidth;
        right = aRight;
        top = aTop;
        height = aHeight;
        bottom = aBottom;
    }

    private static JSObject publisher;
    //
    public Object left, width, right;
    public Object top, height, bottom;
    protected Object published;

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}

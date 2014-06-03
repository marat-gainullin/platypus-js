/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(jsDoc = "/**\n"
        + "* Mouse cursor constansts.\n"
        + "*/")
public class Cursor implements HasPublished {

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

    private static JSObject publisher;
    protected java.awt.Cursor delegate;
    protected Object published;

    protected Cursor(java.awt.Cursor aDelegate) {
        delegate = aDelegate;
    }

    protected Cursor(int aCursorType) {
        delegate = new java.awt.Cursor(aCursorType);
    }

    public java.awt.Cursor unwrap() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.getName();
    }

    @ScriptFunction
    public Cursor getCROSSHAIR() {
        return CROSSHAIR;
    }

    @ScriptFunction
    public Cursor getDEFAULT() {
        return DEFAULT;
    }

    @ScriptFunction
    public Cursor getAUTO() {
        return AUTO;
    }

    @ScriptFunction
    public Cursor getE_RESIZE() {
        return E_RESIZE;
    }

    @ScriptFunction
    public Cursor getHAND() {
        return HAND;
    }

    @ScriptFunction
    public Cursor getMOVE() {
        return MOVE;
    }

    @ScriptFunction
    public Cursor getNE_RESIZE() {
        return NE_RESIZE;
    }

    @ScriptFunction
    public Cursor getNW_RESIZE() {
        return NW_RESIZE;
    }

    @ScriptFunction
    public Cursor getN_RESIZE() {
        return N_RESIZE;
    }

    @ScriptFunction
    public Cursor getSE_RESIZE() {
        return SE_RESIZE;
    }

    @ScriptFunction
    public Cursor getSW_RESIZE() {
        return SW_RESIZE;
    }

    @ScriptFunction
    public Cursor getS_RESIZE() {
        return S_RESIZE;
    }

    @ScriptFunction
    public Cursor getTEXT() {
        return TEXT;
    }

    @ScriptFunction
    public Cursor getWAIT() {
        return WAIT;
    }

    @ScriptFunction
    public Cursor getW_RESIZE() {
        return W_RESIZE;
    }

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

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
public class Cursor extends java.awt.Cursor implements HasPublished{

    private static final Cursor CROSSHAIR = new Cursor(java.awt.Cursor.CROSSHAIR_CURSOR);
    private static final Cursor DEFAULT = new Cursor(java.awt.Cursor.DEFAULT_CURSOR);
    private static final Cursor AUTO = DEFAULT;
    private static final Cursor E_RESIZE = new Cursor(java.awt.Cursor.E_RESIZE_CURSOR);
    private static final Cursor HAND = new Cursor(java.awt.Cursor.HAND_CURSOR);
    private static final Cursor MOVE = new Cursor(java.awt.Cursor.MOVE_CURSOR);
    private static final Cursor NE_RESIZE = new Cursor(java.awt.Cursor.NE_RESIZE_CURSOR);
    private static final Cursor NW_RESIZE = new Cursor(java.awt.Cursor.NW_RESIZE_CURSOR);
    private static final Cursor N_RESIZE = new Cursor(java.awt.Cursor.N_RESIZE_CURSOR);
    private static final Cursor SE_RESIZE = new Cursor(java.awt.Cursor.SE_RESIZE_CURSOR);
    private static final Cursor SW_RESIZE = new Cursor(java.awt.Cursor.SW_RESIZE_CURSOR);
    private static final Cursor S_RESIZE = new Cursor(java.awt.Cursor.S_RESIZE_CURSOR);
    private static final Cursor TEXT = new Cursor(java.awt.Cursor.TEXT_CURSOR);
    private static final Cursor WAIT = new Cursor(java.awt.Cursor.WAIT_CURSOR);
    private static final Cursor W_RESIZE = new Cursor(java.awt.Cursor.W_RESIZE_CURSOR);

    protected java.awt.Cursor delegate;
    protected JSObject published;
    
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Constructs new cursor object.\n"
            + " * @param type Type of new cursor.\n"
            + " */", params = {"type"})
    public Cursor(int aCursorType) {
        super(aCursorType);
    }

    public static Cursor getCROSSHAIR() {
        return CROSSHAIR;
    }

    public static Cursor getDEFAULT() {
        return DEFAULT;
    }

    public static Cursor getAUTO() {
        return AUTO;
    }

    public static Cursor getE_RESIZE() {
        return E_RESIZE;
    }

    public static Cursor getHAND() {
        return HAND;
    }

    public static Cursor getMOVE() {
        return MOVE;
    }

    public static Cursor getNE_RESIZE() {
        return NE_RESIZE;
    }

    public static Cursor getNW_RESIZE() {
        return NW_RESIZE;
    }

    public static Cursor getN_RESIZE() {
        return N_RESIZE;
    }

    public static Cursor getSE_RESIZE() {
        return SE_RESIZE;
    }

    public static Cursor getSW_RESIZE() {
        return SW_RESIZE;
    }

    public static Cursor getS_RESIZE() {
        return S_RESIZE;
    }

    public static Cursor getTEXT() {
        return TEXT;
    }

    public static Cursor getWAIT() {
        return WAIT;
    }

    public static Cursor getW_RESIZE() {
        return W_RESIZE;
    }

    @Override
    public JSObject getPublished() {
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }
}

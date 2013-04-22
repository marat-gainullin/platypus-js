/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

/**
 * This class hides cursor construction from the script.
 *
 * @author mg
 */
public class CursorFactory {

    public static Cursor getCursor(int aAwtCursorType) {
        switch(aAwtCursorType)
        {
            case java.awt.Cursor.CROSSHAIR_CURSOR:
                return Cursor.CROSSHAIR;
            case java.awt.Cursor.DEFAULT_CURSOR:
                return Cursor.DEFAULT;
            case java.awt.Cursor.E_RESIZE_CURSOR:
                return Cursor.E_RESIZE;
            case java.awt.Cursor.HAND_CURSOR:
                return Cursor.HAND;
            case java.awt.Cursor.MOVE_CURSOR:
                return Cursor.MOVE;
            case java.awt.Cursor.NE_RESIZE_CURSOR:
                return Cursor.NE_RESIZE;
            case java.awt.Cursor.NW_RESIZE_CURSOR:
                return Cursor.NW_RESIZE;
            case java.awt.Cursor.N_RESIZE_CURSOR:
                return Cursor.N_RESIZE;
            case java.awt.Cursor.SE_RESIZE_CURSOR:
                return Cursor.SE_RESIZE;
            case java.awt.Cursor.SW_RESIZE_CURSOR:
                return Cursor.SW_RESIZE;
            case java.awt.Cursor.S_RESIZE_CURSOR:
                return Cursor.S_RESIZE;
            case java.awt.Cursor.TEXT_CURSOR:
                return Cursor.TEXT;
            case java.awt.Cursor.WAIT_CURSOR:
                return Cursor.WAIT;
            case java.awt.Cursor.W_RESIZE_CURSOR:
                return Cursor.W_RESIZE;
            default:
                return new Cursor(aAwtCursorType);
        }
    }

    public static Cursor getCursor(java.awt.Cursor aAwtCursor) {
        if (aAwtCursor != null) {
            Cursor cursor = getCursor(aAwtCursor.getType());
            if (cursor != null) {
                return cursor;
            }
        }
        return new Cursor(aAwtCursor);
    }
}

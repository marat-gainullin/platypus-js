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
        switch (aAwtCursorType) {
            case java.awt.Cursor.CROSSHAIR_CURSOR:
                return Cursor.getCROSSHAIR();
            case java.awt.Cursor.DEFAULT_CURSOR:
                return Cursor.getDEFAULT();
            case java.awt.Cursor.E_RESIZE_CURSOR:
                return Cursor.getE_RESIZE();
            case java.awt.Cursor.HAND_CURSOR:
                return Cursor.getHAND();
            case java.awt.Cursor.MOVE_CURSOR:
                return Cursor.getMOVE();
            case java.awt.Cursor.NE_RESIZE_CURSOR:
                return Cursor.getNE_RESIZE();
            case java.awt.Cursor.NW_RESIZE_CURSOR:
                return Cursor.getNW_RESIZE();
            case java.awt.Cursor.N_RESIZE_CURSOR:
                return Cursor.getN_RESIZE();
            case java.awt.Cursor.SE_RESIZE_CURSOR:
                return Cursor.getSE_RESIZE();
            case java.awt.Cursor.SW_RESIZE_CURSOR:
                return Cursor.getSW_RESIZE();
            case java.awt.Cursor.S_RESIZE_CURSOR:
                return Cursor.getS_RESIZE();
            case java.awt.Cursor.TEXT_CURSOR:
                return Cursor.getTEXT();
            case java.awt.Cursor.WAIT_CURSOR:
                return Cursor.getWAIT();
            case java.awt.Cursor.W_RESIZE_CURSOR:
                return Cursor.getW_RESIZE();
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

/**
 *
 * @author mg
 */
public class FontStyle {

    public static int getNORMAL() {
        return NORMAL;
    }

    public static int getBOLD() {
        return BOLD;
    }

    public static int getITALIC() {
        return ITALIC;
    }

    public static int getBOLD_ITALIC() {
        return BOLD_ITALIC;
    }

    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = BOLD | ITALIC;
}

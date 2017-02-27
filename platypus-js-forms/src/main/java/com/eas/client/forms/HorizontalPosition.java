/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class HorizontalPosition {

    public static final int LEFT = SwingConstants.LEFT;
    public static final int CENTER = SwingConstants.CENTER;
    public static final int RIGHT = SwingConstants.RIGHT;

    protected Object published;

    public static int getLEFT() {
        return LEFT;
    }

    public static int getCENTER() {
        return CENTER;
    }

    public static int getRIGHT() {
        return RIGHT;
    }
}

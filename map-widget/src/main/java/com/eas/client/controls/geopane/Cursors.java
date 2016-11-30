/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.controls.geopane;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 *
 * @author mg
 */
public class Cursors {
    private static final String RESOURCES_PATH = "com/eas/client/controls/geopane/resources/32x32/";

    public static Cursor HAND = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(Cursors.class.getClassLoader().getResource(RESOURCES_PATH+"hand.png")).getImage(), new Point(16, 16), "Geo pane pan offer cursor");
    public static Cursor DRAW = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(Cursors.class.getClassLoader().getResource(RESOURCES_PATH+"draw.png")).getImage(), new Point(7, 13), "Geo pane draw cursor");
    public static Cursor PAN = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(Cursors.class.getClassLoader().getResource(RESOURCES_PATH+"pan.png")).getImage(), new Point(16, 16), "Geo pane pan cursor");
    public static Cursor ZOOM = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(Cursors.class.getClassLoader().getResource(RESOURCES_PATH+"magnifier-left.png")).getImage(), new Point(8, 9), "Geo pane zoom/pan view cursor");
    public static Cursor CROSS = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    public static Cursor PAN_POINT = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(Cursors.class.getClassLoader().getResource(RESOURCES_PATH+"panPoint.png")).getImage(), new Point(16, 16), "Geo pane pan point cursor");
}

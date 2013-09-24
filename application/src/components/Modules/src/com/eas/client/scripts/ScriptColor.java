/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.gui.CascadedStyle;
import java.awt.Color;

/**
 *
 * @author mg
 */
public class ScriptColor extends java.awt.Color {

    public ScriptColor() {
        this(0, 0, 0, 0xff);
    }

    public ScriptColor(int r, int g, int b) {
        super(r, g, b);
    }

    public ScriptColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public ScriptColor(java.awt.Color aColor) {
        this(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), aColor.getAlpha());
    }

    public ScriptColor(String aEncoded) {
        this(Color.decode(aEncoded));
    }

    @Override
    public String toString() {
        return CascadedStyle.encodeColor(this);
    }
}
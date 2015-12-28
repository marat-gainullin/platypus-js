/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import java.awt.Color;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(name = "Color", jsDoc = ""
        + "/**\n"
        + " * The <code>Color</code> class is used to encapsulate colors in the default RGB color space.\n"
        + " */")
public class ScriptColor extends java.awt.Color implements HasPublished {

    protected JSObject published;

    @ScriptFunction(name = "Color", params = {"red", "green", "blue", "alpha"}, jsDoc = ""
            + "/**\n"
            + " * The <code>Color</code> class is used to encapsulate colors in the default RGB color space.\n"
            + " * @param red Red compontent (optional)\n"
            + " * @param green Green compontent (optional)\n"
            + " * @param blue Blue compontent (optional)\n"
            + " * @param alpha Alpha compontent (optional)\n"
            + " */")
    public ScriptColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public ScriptColor() {
        this(0, 0, 0, 0xff);
    }

    public ScriptColor(int r, int g, int b) {
        super(r, g, b);
    }

    public ScriptColor(java.awt.Color aColor) {
        this(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), aColor.getAlpha());
    }

    public ScriptColor(String aEncoded) {
        this(Color.decode(aEncoded));
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

    @ScriptFunction
    public static Color getWHITE() {
        return WHITE;
    }

    @ScriptFunction
    public static Color getLIGHT_GRAY() {
        return LIGHT_GRAY;
    }

    @ScriptFunction
    public static Color getGRAY() {
        return GRAY;
    }

    @ScriptFunction
    public static Color getDARK_GRAY() {
        return DARK_GRAY;
    }

    @ScriptFunction
    public static Color getBLACK() {
        return BLACK;
    }

    @ScriptFunction
    public static Color getRED() {
        return RED;
    }

    @ScriptFunction
    public static Color getPINK() {
        return PINK;
    }

    @ScriptFunction
    public static Color getORANGE() {
        return ORANGE;
    }

    @ScriptFunction
    public static Color getYELLOW() {
        return YELLOW;
    }

    @ScriptFunction
    public static Color getMAGENTA() {
        return MAGENTA;
    }

    @ScriptFunction
    public static Color getCYAN() {
        return CYAN;
    }

    @ScriptFunction
    public static Color getBLUE() {
        return BLUE;
    }

    @ScriptFunction
    public static Color getGREEN() {
        return GREEN;
    }

    @Override
    public String toString() {
        return encode(this);
    }

    public static String encode(Color aValue) {
        StringBuilder sb = new StringBuilder();
        sb.append('#');
        String hex = Integer.toHexString(0x00ffffff & aValue.getRGB());
        for (int i = 0; i < 6 - hex.length(); i++) {
            sb.append('0');
        }
        sb.append(hex);
        return sb.toString();
    }


    public static Color darker(Color color, double factorColor) {
        if (color == null) {
            return null;
        }
        return new Color(Math.max((int) (color.getRed() * factorColor), 0),
                Math.max((int) (color.getGreen() * factorColor), 0),
                Math.max((int) (color.getBlue() * factorColor), 0),
                color.getAlpha());
    }
    
    public static Color brighter(Color color, double factorColor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - factorColor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        return new Color(Math.min((int) (r / factorColor), 255),
                Math.min((int) (g / factorColor), 255),
                Math.min((int) (b / factorColor), 255),
                alpha);
    }
}

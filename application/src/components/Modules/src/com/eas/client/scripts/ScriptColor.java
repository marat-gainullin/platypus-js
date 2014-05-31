/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.gui.CascadedStyle;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import java.awt.Color;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(name = "Color", jsDoc = "/**\n"
        + "* The <code>Color</code> class is used to encapsulate colors in the default RGB color space.\n"
        + "*/")
public class ScriptColor extends java.awt.Color implements HasPublished {

    protected static JSObject publisher;
    protected Object published;
    
    @ScriptFunction(name = "Color", params = {"red", "green", "blue", "alpha"}, jsDoc = "/**\n"
            + "* The <code>Color</code> class is used to encapsulate colors in the default RGB color space."
            + "* @param red Red compontent (optional)\n"
            + "* @param red Green compontent (optional)\n"
            + "* @param red Blue compontent (optional)\n"
            + "* @param red Alpha compontent (optional)\n"
            + "*/")
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
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{});
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
        return CascadedStyle.encodeColor(this);
    }
}

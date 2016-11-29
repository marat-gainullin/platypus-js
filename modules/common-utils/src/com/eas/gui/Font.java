/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Font extends java.awt.Font implements HasPublished {

    protected JSObject published;

    @ScriptFunction(params = {"family", "style", "size"}, jsDoc = ""
            + "/**\n"
            + " * Font object, which is used to render text in a visible way.\n"
            + " * @param family a font family name, e.g. 'SansSerif'\n"
            + " * @param style a FontStyle object\n"
            + " * @param size the size of the font\n"
            + " */")
    public Font(String aFamily, int aStyle, int aSize) {
        super(aFamily, aStyle, aSize);
    }

    public boolean isEqual(Font obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Font other = (Font) obj;
        if (this.style != other.style) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return this.name == null ? other.name == null : this.name.equals(other.name);
    }

    @Override
    public String getFamily() {
        return super.getFamily();
    }
/*
    public void setFammily(String aValue) {
        family = aValue;
    }
*/
    @Override
    public int getStyle() {
        return super.getStyle();
    }
/*
    public void setStyle(int aValue) {
        style = aValue;
    }
*/
    @Override
    public int getSize() {
        return super.getSize();
    }
/*
    public void setSize(int aValue) {
        size = aValue;
    }
*/
    @Override
    public String toString() {
        return String.format("Font [family:%s, size:%d, style:%s]", name, size, fontStyleToString(style));
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
    
    protected String fontStyleToString(int aStyle) {
        switch (aStyle) {
            case FontStyle.NORMAL:
                return "normal";
            case FontStyle.BOLD:
                return "bold";
            case FontStyle.ITALIC:
                return "italic";
            case FontStyle.BOLD_ITALIC:
                return "bold italic";
            default:
                return "normal";
        }
    }
}

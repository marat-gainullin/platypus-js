/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Font implements HasPublished {

    private static JSObject publisher;
    
    protected String family;
    protected int style;
    protected int size;
    protected JSObject published;

    @ScriptFunction(params = {"family", "style", "size"}, jsDoc = "/**\n"
            + "* Font object, which is used to render text in a visible way.\n"
            + "* @param family a font family name, e.g. 'SansSerif'\n"
            + "* @param style a FontStyle object\n"
            + "* @param size the size of the font\n"
            + "*/")
    public Font(String aFamily, int aStyle, int aSize) {
        super();
        family = aFamily;
        style = aStyle;
        size = aSize;
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
        if (this.family != other.family && (this.family == null || !this.family.equals(other.family))) {
            return false;
        }
        return true;
    }

    public String getFamily() {
        return family;
    }
/*
    public void setFammily(String aValue) {
        family = aValue;
    }
*/
    public int getStyle() {
        return style;
    }
/*
    public void setStyle(int aValue) {
        style = aValue;
    }
*/
    public int getSize() {
        return size;
    }
/*
    public void setSize(int aValue) {
        size = aValue;
    }
*/
    @Override
    public String toString() {
        return String.format("Font [family:%s, size:%d, style:%s]", family, size, fontStyleToString(style));
    }
    
    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
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

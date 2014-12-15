/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Class, aggregating row data, style information and substituting information,
 * that should be displayed instead of real data.
 *
 * @author mg
 */
public class CellData implements Comparable<Object>, HasPublished  {

    public Object data;
    public String display;

    private static JSObject publisher;
    protected JSObject published;
    /**
     * Simple constructor for controls models data.
     *
     * @param aData Real data, took from subject area data.
     * @param aDisplay Data, thet should be displayed instead of real data
     */
    public CellData(Object aData, String aDisplay) {
        super();
        data = aData;
        display = aDisplay;
    }

    /**
     * Returns real data from subject area.
     *
     * @return Real data.
     */
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The cell's data.\n"
            + " */")
    public Object getData() {
        return data;
    }

    /**
     * Returns data, that should be displayed instead of rela data.
     *
     * @return
     */
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The displayed text.\n"
            + " */")
    public Object getDisplay() {
        return display;
    }

    @ScriptFunction
    public void setDisplay(String aValue) {
        display = aValue;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CellData) {
            CellData cd = (CellData) o;
            Object o1 = display != null ? display : data;
            Object o2 = cd.display != null ? cd.display : cd.data;
            if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
                Comparable<Object> c1 = (Comparable<Object>) o1;
                Comparable<Object> c2 = (Comparable<Object>) o2;
                return c1.compareTo(c2);
            } else if (o1 == null && o2 != null) {
                return -1;
            } else if (o2 == null && o1 != null) {
                return 1;
            }
        }
        return 0;
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
}

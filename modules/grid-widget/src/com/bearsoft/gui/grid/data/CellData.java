/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.data;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Class, aggregating row data, style information and substituting information,
 * that should be displayed instead of real data.
 *
 * @author mg
 */
public class CellData implements HasPublished {

    public Object data;
    public String display;

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
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
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
}

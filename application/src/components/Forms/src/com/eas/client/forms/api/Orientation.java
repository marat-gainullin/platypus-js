/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ScriptObj(jsDoc = "/**\n"
        + "* Orientation types constants.\n"
        + "*/")
public class Orientation implements HasPublished {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    protected static JSObject publisher;
    protected Object published;
    
    @ScriptFunction
    public static int getHORIZONTAL() {
        return HORIZONTAL;
    }
    
    @ScriptFunction
    public static int getVERTICAL() {
        return VERTICAL;
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
}

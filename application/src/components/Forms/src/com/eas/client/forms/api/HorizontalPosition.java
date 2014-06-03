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
        + "* Horizontal position constants.\n"
        + "*/")
public class HorizontalPosition implements HasPublished {

    public static final int LEFT = 2;
    public static final int CENTER = 0;
    public static final int RIGHT = 4;
    
    private static JSObject publisher;
    protected Object published;
    
    @ScriptFunction
    public static int getLEFT() {
        return LEFT;
    }
    
    @ScriptFunction
    public static int getCENTER() {
        return CENTER;
    }
    
    @ScriptFunction
    public static int getRIGHT() {
        return RIGHT;
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
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
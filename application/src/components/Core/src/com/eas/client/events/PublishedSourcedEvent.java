/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.events;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class PublishedSourcedEvent implements HasPublished {

    protected static JSObject publisher;
    protected Object published;
    protected HasPublished source;

    public PublishedSourcedEvent(HasPublished aSource) {
        super();
        source = aSource;
    }

    private static final String SOURCE_JS_DOC = "/**\n"
            + "* The source object of the event.\n"
            + "*/";
    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    public HasPublished getSource() {
        return source;
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

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
 * The base event type.
 * @author vv
 */
public class PublishedSourcedEvent implements HasPublished {

    private static JSObject publisher;
    //
    protected JSObject published;
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

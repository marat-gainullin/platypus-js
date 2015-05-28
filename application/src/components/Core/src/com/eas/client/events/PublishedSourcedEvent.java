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
public class PublishedSourcedEvent implements SourcedEvent {

    protected JSObject published;
    protected HasPublished source;

    public PublishedSourcedEvent(HasPublished aSource) {
        super();
        source = aSource;
    }

    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    @Override
    public HasPublished getSource() {
        return source;
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

}

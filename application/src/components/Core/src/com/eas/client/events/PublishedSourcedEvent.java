/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.events;

import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 *
 * @author vv
 */
public class PublishedSourcedEvent implements HasPublished{

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
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        this.published = aValue;
    }    
}

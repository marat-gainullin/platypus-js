/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Represents an event of an entity's instance row insert.
 *
 * @author vv
 */
public class EntityInstanceInsertEvent extends PublishedSourcedEvent {

    protected JSObject inserted;

    public EntityInstanceInsertEvent(HasPublished source, JSObject inserted) {
        super(source);
        this.inserted = inserted;
    }

    private static final String INSERTED_JSDOC = ""
            + "/**\n"
            + " * The inserted element.\n"
            + " */";

    @ScriptFunction(jsDoc = INSERTED_JSDOC)
    public JSObject getInserted() {
        return inserted;
    }

    private static final String OBJECT_JSDOC = ""
            + "/**\n"
            + " * The inserted element.\n"
            + " */";

    @ScriptFunction(jsDoc = OBJECT_JSDOC)
    public JSObject getObject() {
        return inserted;
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

}

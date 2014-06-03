/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Row;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Represents an event of an entity's instance row insert.
 *
 * @author vv
 */
public class EntityInstanceInsertEvent extends PublishedSourcedEvent {

    protected Row inserted;

    public EntityInstanceInsertEvent(HasPublished source, Row inserted) {
        super(source);
        this.inserted = inserted;
    }

    private static final String INSERTED_JSDOC = ""
            + "/**\n"
            + "* The inserted element.\n"
            + "*/";

    @ScriptFunction(jsDoc = INSERTED_JSDOC)
    public Row getInserted() {
        return inserted;
    }

    private static final String OBJECT_JSDOC = ""
            + "/**\n"
            + "* The inserted element.\n"
            + "*/";

    @ScriptFunction(jsDoc = OBJECT_JSDOC)
    public Row getObject() {
        return inserted;
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

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}

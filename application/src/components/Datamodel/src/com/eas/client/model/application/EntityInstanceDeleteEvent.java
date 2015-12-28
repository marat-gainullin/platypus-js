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
 * An event for an entity's instance row delete.
 *
 * @author vv
 */
public class EntityInstanceDeleteEvent extends PublishedSourcedEvent {

    protected JSObject deleted;

    public EntityInstanceDeleteEvent(HasPublished aSource, JSObject aDeleted) {
        super(aSource);
        deleted = aDeleted;
    }

    private static final String DELETED_JSDOC = ""
            + "/**\n"
            + " * The deleted element.\n"
            + " */";

    @ScriptFunction(jsDoc = DELETED_JSDOC)
    public JSObject getDeleted() {
        return deleted;
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

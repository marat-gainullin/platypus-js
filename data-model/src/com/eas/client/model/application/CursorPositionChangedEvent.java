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
 * An event of an entity cursor positioning.
 *
 * @author mg, vv refactoring
 */
public class CursorPositionChangedEvent extends PublishedSourcedEvent {

    protected int oldIndex;
    protected int newIndex;

    public CursorPositionChangedEvent(HasPublished aSource, int aOldIndex, int aNewIndex) {
        super(aSource);
        oldIndex = aOldIndex;
        newIndex = aNewIndex;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Cursor position the cursor was on.\n"
            + " */")
    public int getOldIndex() {
        return oldIndex;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + "* Cursor position the cursor has been set on.\n"
            + "*/")
    public int getNewIndex() {
        return newIndex;
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

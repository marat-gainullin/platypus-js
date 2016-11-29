/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.metadata.Field;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Represents an entity's instance change event.
 *
 * @author vv
 */
public class EntityInstanceChangeEvent extends PublishedSourcedEvent {

    protected Field field;
    protected Object oldValue;
    protected Object newValue;

    public EntityInstanceChangeEvent(HasPublished aSource, Field aField, Object aOldValue, Object aNewValue) {
        super(aSource);
        field = aField;
        oldValue = aOldValue;
        newValue = aNewValue;
    }

    public Field getField() {
        return field;
    }

    private static final String PROPERTY_NAME_JSDOC = ""
            + "/**\n"
            + " * The changed property name.\n"
            + " */";

    @ScriptFunction(jsDoc = PROPERTY_NAME_JSDOC)
    public String getPropertyName() {
        return field != null ? field.getName() : null;
    }

    private static final String OLD_VALUE_JSDOC = ""
            + "/**\n"
            + " * The old value.\n"
            + " */";

    @ScriptFunction(jsDoc = OLD_VALUE_JSDOC)
    public Object getOldValue() {
        return oldValue;
    }

    private static final String NEW_VALUE_JSDOC = ""
            + "/**\n"
            + " * The new value.\n"
            + " */";

    @ScriptFunction(jsDoc = NEW_VALUE_JSDOC)
    public Object getNewValue() {
        return newValue;
    }

    private static final String OBJECT_JSDOC = ""
            + "/**\n"
            + " * The updated element.\n"
            + " */";

    @ScriptFunction(jsDoc = OBJECT_JSDOC)
    public HasPublished getObject() {
        return source;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.script.StoredFunction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * Script event class, intended to hold information on script event, encountered at runtime while working on datamodel's data.
 * Some event are enqueued and than pumped(executed) in the order of appearance.
 * @author mg
 */
public class ScriptEvent<E extends ApplicationEntity<?, ?, E>> {

    protected E entity;
    protected Function handler;
    protected ScriptSourcedEvent event;

    protected Scriptable scope = null;

    /**
     * Standard script event constructor, suitable for enqueueing and further pumping.
     * @param aEntity Entity that had issued the event
     * @param aHandler Name o the script event handler
     * @param aEvent First argument passing to the handler
     * @param aArg2 Second argument passing to the handler
     * @param aArg3 Third argument passing to the handler
     */
    public ScriptEvent(E aEntity, Function aHandler, ScriptSourcedEvent aEvent) {
        super();
        entity = aEntity;
        handler = aHandler;
        event = aEvent;
    }

    /**
     * Warning! This contructor is intended _only_ for testing and debugging purposes!.
     * @param aEntity Entity that had issued the event
     * @param aScope Script scope in with event code will be executed.
     * @param aName Name o the script event handler
     * @param aEvent First argument passing to the handler
     * @param aArg2 Second argument passing to the handler
     * @param aArg3 Third argument passing to the handler
     */
    public ScriptEvent(E aEntity, Scriptable aScope, Function aName, ScriptSourcedEvent aEvent) {
        this(aEntity, aName, aEvent);
        scope = aScope;
    }

    public void invoke() throws Exception {
        assert entity != null;
        assert handler != null;
        entity.executeScriptEvent(handler, event);
    }

    public Function getHandler() {
        return handler;
    }

    public E getEntity() {
        return entity;
    }

    public ScriptSourcedEvent getEvent() {
        return event;
    }

    public Scriptable getScope() {
        return scope;
    }

    public void resolveHandler() {
        if (handler instanceof StoredFunction && entity != null && entity.getModel() != null) {
            handler = entity.getModel().getHandler(((StoredFunction) handler).getName());
        }
    }
}
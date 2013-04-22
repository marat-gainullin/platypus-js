/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.script.ScriptEvent;
import java.util.HashSet;
import java.util.Set;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ModelScriptEventsSupport<E extends ApplicationEntity<?, ?, E>> {

    protected Set<ModelScriptEventsListener<E>> listeners = new HashSet<>();

    public void addListener(ModelScriptEventsListener<E> aListener) {
        listeners.add(aListener);
    }

    public boolean removeListener(ModelScriptEventsListener<E> aListener) {
        return listeners.remove(aListener);
    }

    public void fireScriptEventEnqueueing(E aEntity, Function aHandler, ScriptSourcedEvent aEvent) {
        if (!listeners.isEmpty()) {
            ScriptEvent<E> event = new ScriptEvent<>(aEntity, aHandler, aEvent);
            for (ModelScriptEventsListener<E> l : listeners) {
                l.eventEnqueueing(event);
            }
        }
    }

    public void fireScriptEventExecuting(E aEntity, Scriptable aScope, Function aHandler, ScriptSourcedEvent aEvent) {
        if (!listeners.isEmpty()) {
            ScriptEvent<E> event = new ScriptEvent<>(aEntity, aScope, aHandler, aEvent);
            for (ModelScriptEventsListener<E> l : listeners) {
                l.eventExecuting(event);
            }
        }
    }
}

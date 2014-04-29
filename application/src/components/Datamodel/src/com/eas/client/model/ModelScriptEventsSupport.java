/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.events.PublishedSourcedEvent;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 */
public class ModelScriptEventsSupport {

    protected Set<ModelScriptEventsListener> listeners = new HashSet<>();

    public void addListener(ModelScriptEventsListener aListener) {
        listeners.add(aListener);
    }

    public boolean removeListener(ModelScriptEventsListener aListener) {
        return listeners.remove(aListener);
    }

    public void fireScriptEventExecuting(PublishedSourcedEvent aEvent) {
        if (!listeners.isEmpty()) {
            for (ModelScriptEventsListener l : listeners) {
                l.eventExecuting(aEvent);
            }
        }
    }
}

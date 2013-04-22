/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.script.ScriptEvent;

/**
 *
 * @author mg
 */
public class DataScriptEventsListener implements ModelScriptEventsListener {

    protected int events;
    protected int scrollEvents;
    protected ApplicationEntity<?, ?, ?> entity;

    public DataScriptEventsListener(ApplicationEntity<?, ?, ?> aEntity) {
        super();
        entity = aEntity;
    }

    @Override
    public void eventEnqueueing(ScriptEvent anEvent) {
    }

    @Override
    public void eventExecuting(ScriptEvent anEvent) {
        if (anEvent.getEntity() == entity) {
            if (anEvent.getEvent() instanceof ApplicationEntity.CursorPositionChangedEvent
                    || anEvent.getEvent() instanceof ApplicationEntity.CursorPositionWillChangeEvent) {
                scrollEvents++;
            } else {
                events++;
            }
        }
    }

    public void reset() {
        events = 0;
        scrollEvents = 0;
    }

    public int getEvents() {
        return events;
    }

    public int getScrollEvents() {
        return scrollEvents;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.CursorPositionChangedEvent;
import com.eas.client.model.application.CursorPositionWillChangeEvent;

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
    public void eventExecuting(PublishedSourcedEvent anEvent) {
        if (anEvent.getSource() == entity.getPublished()) {
            if (anEvent instanceof CursorPositionChangedEvent
                    || anEvent instanceof CursorPositionWillChangeEvent) {
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

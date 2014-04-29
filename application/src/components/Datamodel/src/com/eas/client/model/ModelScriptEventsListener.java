/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.events.PublishedSourcedEvent;

/**
 *
 * @author mg
 */
public interface ModelScriptEventsListener {

    public void eventExecuting(PublishedSourcedEvent anEvent);
}

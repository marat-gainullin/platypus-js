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
public interface ModelScriptEventsListener<E extends ApplicationEntity<?, ?, E>> {

    public void eventEnqueueing(ScriptEvent<E> anEvent);

    public void eventExecuting(ScriptEvent<E> anEvent);
}

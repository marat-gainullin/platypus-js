/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.beans.PropertyChangeEvent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ValueChangeEvent extends Event<PropertyChangeEvent>{

    public ValueChangeEvent(PropertyChangeEvent aDelegate) {
        super(aDelegate);
    }
    
    @ScriptFunction
    public Object getOldValue(){
        return delegate.getOldValue();
    }
    
    @ScriptFunction
    public Object getNewValue(){
        return delegate.getNewValue();
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

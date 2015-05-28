/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
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
        return published;
    }
}

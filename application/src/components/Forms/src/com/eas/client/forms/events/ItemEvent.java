/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ItemEvent extends Event<javax.swing.event.ChangeEvent> {

    protected JSObject item;
    
    public ItemEvent(Object aSource, JSObject aItem) {
        super(new javax.swing.event.ChangeEvent(aSource));
        item = aItem;
    }

    @ScriptFunction
    public JSObject getItem() {
        return item;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import javax.swing.JComponent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ContainerEvent extends Event<java.awt.event.ContainerEvent> {

    protected ContainerEvent(java.awt.event.ContainerEvent aEvent) {
        super(aEvent);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The child component the operation is performed on.\n"
            + " */")
    public HasPublished getChild() {
        return delegate.getChild() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getChild()) : null;
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

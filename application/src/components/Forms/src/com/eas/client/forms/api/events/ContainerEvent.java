/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
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
    public Component<?> getChild() {
        return delegate.getChild() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getChild()) : null;
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}

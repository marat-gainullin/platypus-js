/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.HasPublished;
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
    public HasPublished getChild() {
        return delegate.getChild() instanceof JComponent ? lookupApiComponent((JComponent) delegate.getChild()) : null;
    }

    @Override
    public JSObject getPublished() {
        return published;
    }

}

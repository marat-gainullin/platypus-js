/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import java.util.EventObject;
import javax.swing.JComponent;

/**
 * Base class for any GUI event.
 *
 * @author mg
 * @param <E>
 */
public abstract class Event<E extends EventObject> extends PublishedSourcedEvent {

    protected E delegate;

    protected Event(E aDelegate) {
        super(null);
        delegate = aDelegate;
    }

    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    @Override
    public HasPublished getSource() {
        Object oSource = delegate.getSource();
        if (oSource instanceof JComponent) {
            return lookupApiComponent((JComponent) oSource);
        } else if (oSource instanceof HasPublished) {
            return (HasPublished) oSource;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s on %s", getClass().getSimpleName(), getSource() != null ? getSource().toString() : "");
    }

    public static HasPublished lookupApiComponent(JComponent aComp) {
        JComponent comp = aComp;
        while (comp.getParent() != null && !(comp instanceof HasPublished)) {
            comp = (JComponent) comp.getParent();
        }
        return comp instanceof HasPublished ? (HasPublished) comp : null;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import static com.eas.client.forms.api.Component.getEventsProxy;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import java.awt.event.ContainerEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 * @param <D> a Swing component delegate type
 */
public abstract class Container<D extends JComponent> extends Component<D> {

    private static final String CHILD_JSDOC = ""
            + "/**\n"
            + " * Gets the container's nth component.\n"
            + " * @param index the component's index in the container\n"
            + " * @return the child component\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getComponent(aIndex));
    }

    private static final String CHILDREN_JSDOC = ""
            + "/**\n"
            + "* Gets the container's children components.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CHILDREN_JSDOC)
    public Component<?>[] getChildren() {
        List<Component<?>> ch = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            ch.add(child(i));
        }
        return ch.toArray(new Component<?>[]{});
    }

    private static final String REMOVE_JSDOC = ""
            + "/**\n"
            + "* Removes the specified component from this container.\n"
            + "* @param component the component to remove\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    public void remove(Component<?> aComp) {
        delegate.remove(unwrap(aComp));
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String CLEAR_JSDOC = ""
            + "/**\n"
            + "* Removes all the components from this container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CLEAR_JSDOC)
    public void clear() {
        delegate.removeAll();
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String COUNT_JSDOC = ""
            + "/**\n"
            + "* Gets the number of components in this panel.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = COUNT_JSDOC)
    public int getCount() {
        return delegate.getComponentCount();
    }
    
    private static final String ON_COMPONENT_ADDED_JSDOC = ""
        + "/**\n"
        + "* Component added event hanler function.\n"
        + "*/";

    @ScriptFunction(jsDoc = ON_COMPONENT_ADDED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    public Function getOnComponentAdded() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentAdded) : null;
    }
    
    @ScriptFunction
    public void setOnComponentAdded(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentAdded, aValue);
        }
    }
    
    private static final String ON_COMPONENT_REMOVED_JSDOC = ""
        + "/**\n"
        + "* Component removed event handler function.\n"
        + "*/";

    @ScriptFunction(jsDoc = ON_COMPONENT_REMOVED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    public Function getOnComponentRemoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentRemoved) : null;
    }

    @ScriptFunction
    public void setOnComponentRemoved(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.componentRemoved, aValue);
        }
    }
    
    
    @Override
    public String toString() {
        return String.format("%s [%s] count:%d", delegate.getName() != null ? delegate.getName() : "", getClass().getSimpleName(), getCount());
    }
}

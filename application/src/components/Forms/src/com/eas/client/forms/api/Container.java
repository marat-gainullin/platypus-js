/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.controls.events.ControlEventsIProxy;
import com.eas.script.EventMethod;
import com.eas.script.HasPublishedInvalidatableCollection;
import com.eas.script.ScriptFunction;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 * @param <D> a Swing component delegate type
 */
public abstract class Container<D extends JComponent> extends Component<D> implements HasPublishedInvalidatableCollection {

    protected ContainerListener invalidatorListener = new ContainerAdapter() {

        @Override
        public void componentAdded(ContainerEvent e) {
            invalidatePublishedCollection();
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            invalidatePublishedCollection();
        }

    };

    protected JSObject publishedCollectionInvalidator;

    @Override
    public JSObject getPublishedCollectionInvalidator() {
        return publishedCollectionInvalidator;
    }

    @Override
    public void setPublishedCollectionInvalidator(JSObject aValue) {
        publishedCollectionInvalidator = aValue;
    }

    @Override
    public void invalidatePublishedCollection() {
        if (publishedCollectionInvalidator != null && publishedCollectionInvalidator.isFunction()) {
            publishedCollectionInvalidator.call(getPublished(), new Object[]{});
        }
    }

    @Override
    protected void setDelegate(D aDelegate) {
        if (delegate != null) {
            delegate.removeContainerListener(invalidatorListener);
        }
        super.setDelegate(aDelegate);
        if (delegate != null) {
            delegate.addContainerListener(invalidatorListener);
        }
    }

    protected static final String CHILD_JSDOC = ""
            + "/**\n"
            + " * Gets the container's n-th component.\n"
            + " * @param index the component's index in the container\n"
            + " * @return the child component\n"
            + "*/";

    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getComponent(aIndex));
    }

    protected static final String CHILDREN_JSDOC = ""
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

    protected static final String REMOVE_JSDOC = ""
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

    protected static final String COUNT_JSDOC = ""
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
    public JSObject getOnComponentAdded() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentAdded) : null;
    }

    @ScriptFunction
    public void setOnComponentAdded(JSObject aValue) {
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
    public JSObject getOnComponentRemoved() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.componentRemoved) : null;
    }

    @ScriptFunction
    public void setOnComponentRemoved(JSObject aValue) {
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

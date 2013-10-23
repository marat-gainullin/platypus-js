/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class TabbedPane extends Container<JTabbedPane> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A component that lets the user switch between a group of components by\n" 
            + "* clicking on a tab with a given title and/or icon.\n" 
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public TabbedPane() {
        super();
        setDelegate(new JTabbedPane());
    }

    protected TabbedPane(JTabbedPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "Appends the component whith specified text to the end of this container.")
    public void add(Component<?> aComp, String aText) {
        delegate.addTab(aText, unwrap(aComp));
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction(jsDoc = "Appends the component whith specified text and Icon to the end of this container.")
    public void add(Component<?> aComp, String aText, Icon aIcon) {
        if (aComp != null) {
            delegate.addTab(aText, aIcon, unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = "The selected component.")
    public Component<?> getSelectedComponent() {
        return getComponentWrapper(delegate.getSelectedComponent());
    }

    @ScriptFunction
    public void setSelectedComponent(Component<?> aComp) {
        if (aComp == null) {
            delegate.setSelectedIndex(-1);
        } else {
            delegate.setSelectedComponent(unwrap(aComp));
        }
    }

    @ScriptFunction(jsDoc = "The selected component's index.")
    public int getSelectedIndex() {
        return delegate.getSelectedIndex();
    }

    @ScriptFunction
    public void setSelectedIndex(int aIndex) {
        delegate.setSelectedIndex(aIndex);
    }

    @ScriptFunction(jsDoc = "Selected tab change event.")
    public Function getOnStateChanged() {
        ControlEventsIProxy proxy = getEventsProxy(delegate);
        return proxy != null ? proxy.getHandlers().get(ControlEventsIProxy.stateChanged) : null;
    }

    @ScriptFunction
    public void setOnStateChanged(Function aValue) {
        ControlEventsIProxy proxy = checkEventsProxy(delegate);
        if (proxy != null) {
            proxy.getHandlers().put(ControlEventsIProxy.stateChanged, aValue);
        }
    }
}

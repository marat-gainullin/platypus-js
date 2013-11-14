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
    
    private static final String ADD_JSDOC = "/**\n"
            + "* Appends the component whith specified text to the end of this container.\n"
            + "* @param component the component to add\n"
            + "* @param text the text for the tab\n"
            + "* @param icon the icon for the tab (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "text", "icon"})
    public void add(Component<?> aComp, String aText) {
        delegate.addTab(aText, unwrap(aComp));
        delegate.revalidate();
        delegate.repaint();
    }

    public void add(Component<?> aComp, String aText, Icon aIcon) {
        if (aComp != null) {
            delegate.addTab(aText, aIcon, unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String SELECTED_COMPONENT_JSDOC = "/**\n"
            + "* The selected component.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SELECTED_COMPONENT_JSDOC)
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

    private static final String SELECTED_INDEX_JSDOC = "/**\n"
            + "* The selected component's index.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SELECTED_INDEX_JSDOC)
    public int getSelectedIndex() {
        return delegate.getSelectedIndex();
    }

    @ScriptFunction
    public void setSelectedIndex(int aIndex) {
        delegate.setSelectedIndex(aIndex);
    }

    private static final String ON_STATE_CHANGED_JSDOC = "/**\n"
            + "* Selected tab change event handler function.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_STATE_CHANGED_JSDOC)
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

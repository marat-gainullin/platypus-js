/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.events.ChangeEvent;
import com.eas.script.EventMethod;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class TabbedPane extends Container<JTabbedPane> {

    private static JSObject publisher;
    
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A component that lets the user switch between a group of components by\n"
            + " * clicking on a tab with a given title and/or icon.\n"
            + " */";

    protected JSObject onItemSelected;
    
    protected ChangeListener tabsChangeListener = (javax.swing.event.ChangeEvent e) -> {
        try {
            onItemSelected.call(getPublished(), new Object[]{new ChangeEvent(e).getPublished()});
        } catch (Exception ex) {
            Logger.getLogger(TabbedPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
    
    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public TabbedPane() {
        super();
        setDelegate(new JTabbedPane());
    }

    protected TabbedPane(JTabbedPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    @Override
    protected void setDelegate(JTabbedPane aDelegate) {
        if(delegate != null){
            delegate.removeChangeListener(tabsChangeListener);
        }
        super.setDelegate(aDelegate);
        if(delegate != null){
            delegate.addChangeListener(tabsChangeListener);
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when one of the components is selected in this tabbed pane.\n"
            + " */")
    @EventMethod(eventClass = ChangeEvent.class)
    public JSObject getOnItemSelected() {
        return onItemSelected;
    }

    @ScriptFunction
    public void setOnItemSelected(JSObject aValue) {
        if (onItemSelected != aValue) {
            onItemSelected = aValue;
        }
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + " * Appends the component whith specified text to the end of this container.\n"
            + " * @param component the component to add.\n"
            + " * @param text the text for the tab.\n"
            + " * @param icon the icon for the tab (optional).\n"
            + " */";

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

    private static final String SELECTED_COMPONENT_JSDOC = ""
            + "/**\n"
            + " * The selected component.\n"
            + " */";

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

    private static final String SELECTED_INDEX_JSDOC = ""
            + "/**\n"
            + " * The selected component's index.\n"
            + " */";

    @ScriptFunction(jsDoc = SELECTED_INDEX_JSDOC)
    public int getSelectedIndex() {
        return delegate.getSelectedIndex();
    }

    @ScriptFunction
    public void setSelectedIndex(int aIndex) {
        delegate.setSelectedIndex(aIndex);
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }
    
    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}

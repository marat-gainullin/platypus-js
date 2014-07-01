/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.HasGroup;
import com.eas.client.forms.api.events.ChangeEvent;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.script.EventMethod;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ButtonGroup extends Container<ButtonGroupWrapper> {

    protected JSObject onItemSelected;
    protected ItemListener itemListener = (ItemEvent e) -> {
        fireItemSelected();
    };

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Creates a multiple-exclusion scope for a set of buttons.\n"
            + "  * Creating a set of buttons with the same <code>ButtonGroup</code> object means that turning \"on\" one of those buttons turns off all other buttons in the group.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public ButtonGroup() {
        super();
        setDelegate(new ButtonGroupWrapper());
    }

    protected ButtonGroup(ButtonGroupWrapper aDelegate) {
        super();
        setDelegate(aDelegate);
        settingButtonGroup = true;
        try {
            for (Component comp : getChildren()) {
                if (comp instanceof HasGroup) {
                    ((HasGroup) comp).setButtonGroup(this);
                }
            }
        } finally {
            settingButtonGroup = false;
        }
    }

    @Override
    protected void setDelegate(ButtonGroupWrapper aDelegate) {
        if (delegate != null) {
            delegate.removeItemListener(itemListener);
        }
        super.setDelegate(aDelegate);
        if (delegate != null) {
            delegate.addItemListener(itemListener);
        }
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Event that is fired when one of the components is selected in this group.\n"
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

    private boolean settingButtonGroup;
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + " * Appends the specified component to the end of this group.\n"
            + " * @param component Component to add to the group.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params={"component"})
    public void add(Component<?> aComp) {
        if (!settingButtonGroup && aComp != null) {
            JComponent itemDelegate = unwrap(aComp);
            delegate.add(itemDelegate);
            invalidatePublishedCollection();
            if (aComp instanceof HasGroup) {
                settingButtonGroup = true;
                try {
                    ((HasGroup) aComp).setButtonGroup(this);
                } finally {
                    settingButtonGroup = false;
                }
            }
        }
    }

    private static final String GROUP_REMOVE_JSDOC = ""
            + "/**\n"
            + " * Removes the specified component from the group.\n"
            + " * @param component Component to remove from the group.\n"
            + "*/";

    @ScriptFunction(jsDoc = GROUP_REMOVE_JSDOC, params={"component"})
    @Override
    public void remove(Component<?> aComp) {
        if (!settingButtonGroup && aComp != null) {
            JComponent itemDelegate = unwrap(aComp);
            delegate.remove(itemDelegate);
            invalidatePublishedCollection();
            if (aComp instanceof HasGroup) {
                settingButtonGroup = true;
                try {
                    ((HasGroup) aComp).setButtonGroup(null);
                } finally {
                    settingButtonGroup = false;
                }
            }
        }
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }

    public void clearSelection() {
        delegate.clearSelection();
        fireItemSelected();
    }

    protected void fireItemSelected() {
        try {
            onItemSelected.call(getPublished(), new Object[]{new ChangeEvent(new javax.swing.event.ChangeEvent(delegate)).getPublished()});
        } catch (Exception ex) {
            Logger.getLogger(CardPane.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}

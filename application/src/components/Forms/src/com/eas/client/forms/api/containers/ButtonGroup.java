/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.HasGroup;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ButtonGroup extends Container<ButtonGroupWrapper> {

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
        Component[] comps = getChildren();
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

    private boolean settingButtonGroup;
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this group.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC)
    public void add(Component<?> aComp) {
        if (!settingButtonGroup && aComp != null) {
            delegate.add(unwrap(aComp));
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

    private static final String REMOVE_JSDOC = ""
            + "/**\n"
            + "* Removes the specified component from the group.\n"
            + "*/";

    @ScriptFunction(jsDoc = REMOVE_JSDOC)
    @Override
    public void remove(Component<?> aComp) {
        if (!settingButtonGroup && aComp != null) {
            delegate.remove(unwrap(aComp));
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

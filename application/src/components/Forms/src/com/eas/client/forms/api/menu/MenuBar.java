/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Container;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class MenuBar extends Container<JMenuBar> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * An implementation of a menu bar.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public MenuBar() {
        super();
        setDelegate(new JMenuBar());
    }

    protected MenuBar(JMenuBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Adds the item to the menu.\n"
            + "* @param menu the menu component to add\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"menu"})
    public void add(Menu aMenu) {
        delegate.add((JMenu) unwrap(aMenu));
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

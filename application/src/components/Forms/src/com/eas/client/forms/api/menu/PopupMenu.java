/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PopupMenu extends Container<JPopupMenu> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* An implementation of a popup menu -- a small window that pops up and displays a series of choices.\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public PopupMenu() {
        super();
        setDelegate(new JPopupMenu());
    }

    protected PopupMenu(JPopupMenu aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Adds the item to the menu.\n"
            + "* @param menu the menu component to add.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"menu"})
    public void add(Menu aMenu) {
        delegate.add((JMenu) unwrap(aMenu));
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
